package seedu.coursepilot.logic.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;

import seedu.coursepilot.commons.core.LogsCenter;

/**
 * Translates natural language input into CoursePilot commands using a local LLM,
 * and provides conversational assistance for user questions.
 */
public class NaturalLanguageParser {

    /**
     * Represents the type of user input: a command to translate or a question to answer.
     */
    public enum InputType {
        COMMAND,
        QUESTION
    }

    private static final Logger logger = LogsCenter.getLogger(NaturalLanguageParser.class);

    private static final Set<String> VALID_COMMAND_WORDS = Set.of(
            "add", "edit", "delete", "find", "list", "clear", "help", "exit"
    );

    private static final String PROMPT_TEMPLATE =
            "You are CoursePilot command translator. Convert the user input into exactly one command. "
            + "Output ONLY the command, no explanation.\n\n"
            + "COMMANDS (use these prefixes exactly: n/ p/ e/ m/ t/):\n"
            + "1. add n/NAME p/PHONE e/EMAIL m/MATRIC [t/TAG]...\n"
            + "2. edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [m/MATRIC] [t/TAG]...\n"
            + "3. delete INDEX\n"
            + "4. find KEYWORD [MORE_KEYWORDS]\n"
            + "5. list\n"
            + "6. clear\n"
            + "7. help\n"
            + "8. exit\n\n"
            + "PREFIXES: n/=name, p/=phone, e/=email, m/=matric, t/=tag\n"
            + "- find does NOT use prefixes, just space-separated keywords\n"
            + "- Multiple tags use separate t/ prefixes: t/tag1 t/tag2\n\n"
            + "RULES:\n"
            + "- \"remove\", \"delete\", \"drop\" = delete\n"
            + "- \"show\", \"display\", \"all\" = list\n"
            + "- \"search\", \"look for\", \"find\" = find\n"
            + "- \"change\", \"update\", \"modify\" = edit\n"
            + "- \"add\", \"create\", \"new\" = add\n"
            + "- INDEX is a number (1st=1, 2nd=2, 3rd=3, etc.)\n\n"
            + "EXAMPLES:\n"
            + "\"remove the third student\" -> delete 3\n"
            + "\"show all students\" -> list\n"
            + "\"search for Alice\" -> find Alice\n"
            + "\"find Bob or Alice\" -> find Bob Alice\n"
            + "\"change first student phone to 99991111\" -> edit 1 p/99991111\n"
            + "\"add Bob phone 81234567 email bob@u.nus.edu matric A654321 tag groupA\" "
            + "-> add n/Bob p/81234567 e/bob@u.nus.edu m/A654321 t/groupA\n"
            + "\"add Alice phone 91234567 email a@b.com matric A111111 tags friends and classmates\" "
            + "-> add n/Alice p/91234567 e/a@b.com m/A111111 t/friends t/classmates\n\n"
            + "INPUT: %s";

    private static final String CHAT_SYSTEM_PROMPT =
            "You are CoursePilot Assistant, a helpful chatbot for a student contact management app. "
            + "Answer questions about how to use the app concisely.\n\n"
            + "Available commands:\n"
            + "- add n/NAME p/PHONE e/EMAIL m/MATRIC [t/TAG]... : Add a student\n"
            + "- edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [m/MATRIC] [t/TAG]... : Edit a student\n"
            + "- delete INDEX : Delete a student\n"
            + "- find KEYWORD [MORE_KEYWORDS] : Search students by name\n"
            + "- list : Show all students\n"
            + "- clear : Remove all students\n"
            + "- help : Show help\n"
            + "- exit : Close the app\n\n"
            + "Prefixes: n/=name, p/=phone, e/=email, m/=matric, t/=tag.\n"
            + "If the user asks how to do something, explain which command to use with an example.";

    private final ChatLanguageModel chatModel;
    private final ChatMemory chatMemory;

    /**
     * Creates a NaturalLanguageParser with the default Ollama chat model.
     */
    public NaturalLanguageParser() {
        this.chatModel = ChatModelFactory.createChatModel();
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    }

    /**
     * Creates a NaturalLanguageParser with the given chat model.
     */
    public NaturalLanguageParser(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    }

    /**
     * Creates a NaturalLanguageParser with the given chat model and memory (for testing).
     */
    NaturalLanguageParser(ChatLanguageModel chatModel, ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
    }

    /**
     * Classifies user input as either a question or a command attempt.
     */
    public InputType classifyInput(String userInput) {
        String trimmed = userInput.strip().toLowerCase();
        if (trimmed.endsWith("?")) {
            return InputType.QUESTION;
        }
        if (trimmed.matches(
                "^(what|how|why|when|where|who|which|is|are|can|do|does|could|should|will|would|tell me)\\b.*")) {
            return InputType.QUESTION;
        }
        return InputType.COMMAND;
    }

    /**
     * Sends a conversational question to the LLM and returns the response.
     * Maintains conversation history via ChatMemory.
     *
     * @param userInput The user's question.
     * @return The AI's response, or a fallback message if the service is unavailable.
     */
    public String chat(String userInput) {
        try {
            chatMemory.add(UserMessage.from(userInput));

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from(CHAT_SYSTEM_PROMPT));
            messages.addAll(chatMemory.messages());

            String responseText = chatModel.generate(messages).content().text();

            chatMemory.add(AiMessage.from(responseText));
            return responseText;
        } catch (Exception e) {
            logger.info("AI chat service unavailable: " + e.getMessage());
            return "Sorry, the AI assistant is currently unavailable. Type 'help' to see available commands.";
        }
    }

    /**
     * Translates natural language input into a CoursePilot command.
     *
     * @param userInput The natural language input from the user.
     * @return An Optional containing the translated command, or empty if translation failed.
     */
    public Optional<String> translate(String userInput) {
        String prompt = String.format(PROMPT_TEMPLATE, userInput);
        try {
            String response = chatModel.generate(prompt);
            return Optional.ofNullable(response)
                    .filter(s -> !s.isBlank())
                    .map(this::sanitizeResponse)
                    .filter(this::isPlausibleCommand);
        } catch (Exception e) {
            logger.info("AI service unavailable: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Cleans up LLM response by removing backticks, extra whitespace, and taking only the first line.
     */
    String sanitizeResponse(String response) {
        String sanitized = response.strip();

        // Remove markdown code block markers
        if (sanitized.startsWith("```")) {
            sanitized = sanitized.replaceAll("```\\w*\\n?", "");
        }
        sanitized = sanitized.replace("`", "");

        // Take only the first non-empty line
        String[] lines = sanitized.split("\n");
        for (String line : lines) {
            String trimmed = line.strip();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        return sanitized.strip();
    }

    /**
     * Checks if the response starts with a valid command word.
     */
    boolean isPlausibleCommand(String command) {
        if (command == null || command.isBlank()) {
            return false;
        }
        String firstWord = command.split("\\s+")[0].toLowerCase();
        boolean plausible = VALID_COMMAND_WORDS.contains(firstWord);
        if (!plausible) {
            logger.info("LLM returned implausible command: " + command);
        }
        return plausible;
    }
}
