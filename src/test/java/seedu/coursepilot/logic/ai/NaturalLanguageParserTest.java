package seedu.coursepilot.logic.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;

/**
 * Unit tests for {@link NaturalLanguageParser}.
 */
public class NaturalLanguageParserTest {

    /**
     * Creates a stub ChatLanguageModel that returns the given text for any input.
     */
    private static ChatLanguageModel stubModel(String responseText) {
        return new ChatLanguageModel() {
            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                return new Response<>(AiMessage.from(responseText));
            }
        };
    }

    /**
     * Creates a stub ChatLanguageModel that throws an exception for any input.
     */
    private static ChatLanguageModel failingModel() {
        return new ChatLanguageModel() {
            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                throw new RuntimeException("Connection refused");
            }
        };
    }

    // ==================== translate tests ====================

    @Test
    public void translate_validCommand_returnsCommand() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel("delete 3"));
        Optional<String> result = parser.translate("remove the third student");
        assertEquals(Optional.of("delete 3"), result);
    }

    @Test
    public void translate_responseWithBackticks_sanitizesCorrectly() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel("```\nfind Alice\n```"));
        Optional<String> result = parser.translate("search for Alice");
        assertEquals(Optional.of("find Alice"), result);
    }

    @Test
    public void translate_implausibleCommand_returnsEmpty() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel("I don't understand"));
        Optional<String> result = parser.translate("do something weird");
        assertTrue(result.isEmpty());
    }

    @Test
    public void translate_modelThrowsException_returnsEmpty() {
        NaturalLanguageParser parser = new NaturalLanguageParser(failingModel());
        Optional<String> result = parser.translate("show all students");
        assertTrue(result.isEmpty());
    }

    @Test
    public void translate_blankResponse_returnsEmpty() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel("   "));
        Optional<String> result = parser.translate("hello");
        assertTrue(result.isEmpty());
    }

    // ==================== classifyInput tests ====================

    @Test
    public void classifyInput_questionMark_returnsQuestion() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel(""));
        assertEquals(NaturalLanguageParser.InputType.QUESTION,
                parser.classifyInput("how do I add a student?"));
    }

    @Test
    public void classifyInput_interrogativeWord_returnsQuestion() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel(""));
        assertEquals(NaturalLanguageParser.InputType.QUESTION,
                parser.classifyInput("what commands are available"));
    }

    @Test
    public void classifyInput_tellMe_returnsQuestion() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel(""));
        assertEquals(NaturalLanguageParser.InputType.QUESTION,
                parser.classifyInput("tell me how to get started"));
    }

    @Test
    public void classifyInput_commandLike_returnsCommand() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel(""));
        assertEquals(NaturalLanguageParser.InputType.COMMAND,
                parser.classifyInput("remove the third student"));
    }

    @Test
    public void classifyInput_ambiguousInput_returnsCommand() {
        NaturalLanguageParser parser = new NaturalLanguageParser(stubModel(""));
        assertEquals(NaturalLanguageParser.InputType.COMMAND,
                parser.classifyInput("show me all students"));
    }

    // ==================== chat tests ====================

    @Test
    public void chat_validQuestion_returnsResponse() {
        NaturalLanguageParser parser = new NaturalLanguageParser(
                stubModel("Use the add command: add n/NAME p/PHONE e/EMAIL m/MATRIC"));
        String response = parser.chat("how do I add a student?");
        assertEquals("Use the add command: add n/NAME p/PHONE e/EMAIL m/MATRIC", response);
    }

    @Test
    public void chat_modelThrowsException_returnsFallback() {
        NaturalLanguageParser parser = new NaturalLanguageParser(failingModel());
        String response = parser.chat("how do I get started?");
        assertTrue(response.contains("AI assistant is currently unavailable"));
    }

    @Test
    public void chat_multiTurn_maintainsMemory() {
        // Track the number of user messages seen by the model
        final int[] messageCount = {0};
        ChatLanguageModel countingModel = new ChatLanguageModel() {
            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                // Count UserMessage instances (excludes SystemMessage)
                messageCount[0] = (int) messages.stream()
                        .filter(m -> m instanceof dev.langchain4j.data.message.UserMessage)
                        .count();
                return new Response<>(AiMessage.from("response"));
            }
        };

        NaturalLanguageParser parser = new NaturalLanguageParser(countingModel);
        parser.chat("first question");
        assertEquals(1, messageCount[0]);

        parser.chat("second question");
        assertEquals(2, messageCount[0]);
    }
}
