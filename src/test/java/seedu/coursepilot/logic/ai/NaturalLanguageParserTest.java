package seedu.coursepilot.logic.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;

/**
 * Unit tests for {@link NaturalLanguageParser}.
 */
public class NaturalLanguageParserTest {

    private static ChatLanguageModel stubModel(String responseText) {
        return new ChatLanguageModel() {
            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                return new Response<>(AiMessage.from(responseText));
            }
        };
    }

    private static ChatLanguageModel failingModel() {
        return new ChatLanguageModel() {
            @Override
            public Response<AiMessage> generate(List<ChatMessage> messages) {
                throw new RuntimeException("Connection refused");
            }
        };
    }

    @Test
    public void handleUnknownInput_keywordMatch_returnsUsage() {
        NaturalLanguageParser parser = new NaturalLanguageParser();
        String result = parser.handleUnknownInput("how to add a student");
        assertTrue(result.contains("add"));
    }

    @Test
    public void handleUnknownInput_noKeywordNoGemini_returnsFallback() {
        NaturalLanguageParser parser = new NaturalLanguageParser();
        String result = parser.handleUnknownInput("what is quantum physics");
        assertTrue(result.contains("help"));
    }

    @Test
    public void handleUnknownInput_withGemini_callsGemini() {
        NaturalLanguageParser parser = new NaturalLanguageParser(
                new KeywordMatcher(), stubModel("Use the add command"));
        String result = parser.handleUnknownInput("what is quantum physics");
        assertEquals("Use the add command", result);
    }

    @Test
    public void handleUnknownInput_geminiKeywordMatchFirst_returnsKeyword() {
        NaturalLanguageParser parser = new NaturalLanguageParser(
                new KeywordMatcher(), stubModel("Gemini response"));
        // "add" should match keyword first, not call Gemini
        String result = parser.handleUnknownInput("how to add");
        assertTrue(result.contains("add"));
        assertFalse(result.equals("Gemini response"));
    }

    @Test
    public void handleUnknownInput_geminiError_returnsFallback() {
        NaturalLanguageParser parser = new NaturalLanguageParser(
                new KeywordMatcher(), failingModel());
        String result = parser.handleUnknownInput("something random");
        assertTrue(result.contains("help"));
    }

    @Test
    public void isGeminiAvailable_noModel_returnsFalse() {
        NaturalLanguageParser parser = new NaturalLanguageParser();
        assertFalse(parser.isGeminiAvailable());
    }

    @Test
    public void isGeminiAvailable_withModel_returnsTrue() {
        NaturalLanguageParser parser = new NaturalLanguageParser(
                new KeywordMatcher(), stubModel("test"));
        assertTrue(parser.isGeminiAvailable());
    }

    @Test
    public void setGeminiApiKey_blank_clearsModel() {
        NaturalLanguageParser parser = new NaturalLanguageParser(
                new KeywordMatcher(), stubModel("test"));
        assertTrue(parser.isGeminiAvailable());
        parser.setGeminiApiKey("");
        assertFalse(parser.isGeminiAvailable());
    }
}
