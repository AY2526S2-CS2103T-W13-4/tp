package seedu.coursepilot.logic.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
 * Provides AI-powered assistance using a three-tier approach:
 * 1. Keyword matching against known command usage messages
 * 2. Gemini API for conversational responses (when API key is configured)
 * 3. Fallback message directing users to 'help'
 */
public class NaturalLanguageParser {

    private static final Logger logger = LogsCenter.getLogger(NaturalLanguageParser.class);

    private static final String CHAT_SYSTEM_PROMPT =
            "You are CoursePilot Assistant, a helpful chatbot for a student/tutorial management app.\n"
            + "Answer questions about how to use the app concisely.\n\n"
            + "Available commands:\n"
            + "- add -student n/NAME p/PHONE e/EMAIL m/MATRIC [t/TAG]... : Add a student\n"
            + "- add -tutorial TUTORIAL_CODE : Add a tutorial\n"
            + "- edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [m/MATRIC] [t/TAG]... : Edit a student\n"
            + "- delete -student INDEX : Delete a student\n"
            + "- delete -tutorial TUTORIAL_CODE : Delete a tutorial\n"
            + "- find KEYWORD [MORE_KEYWORDS] : Search students by name\n"
            + "- find /phone PHONE_PREFIX : Search by phone\n"
            + "- find /email EMAIL_KEYWORD : Search by email\n"
            + "- find /matric MATRIC_PREFIX : Search by matric number\n"
            + "- list -student : Show all students\n"
            + "- list -tutorial : Show all tutorials\n"
            + "- select TUTORIAL_CODE : Select a tutorial\n"
            + "- clear : Remove all data\n"
            + "- help : Show help\n"
            + "- exit : Close the app\n\n"
            + "Prefixes: n/=name, p/=phone, e/=email, m/=matric, t/=tag.\n"
            + "If the user asks how to do something, explain which command to use with an example.";

    private static final String FALLBACK_MESSAGE =
            "I couldn't find a specific answer. Type 'help' to see all available commands,"
            + " or try asking about a specific command like 'add', 'edit', 'delete', 'find', or 'list'.";

    private final KeywordMatcher keywordMatcher;
    private ChatLanguageModel geminiModel;
    private final ChatMemory chatMemory;

    /**
     * Creates a NaturalLanguageParser with no Gemini model (keyword matching only).
     */
    public NaturalLanguageParser() {
        this.keywordMatcher = new KeywordMatcher();
        this.geminiModel = null;
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    }

    /**
     * Creates a NaturalLanguageParser with a custom keyword matcher and model (for testing).
     */
    NaturalLanguageParser(KeywordMatcher keywordMatcher, ChatLanguageModel geminiModel) {
        this.keywordMatcher = keywordMatcher;
        this.geminiModel = geminiModel;
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    }

    /**
     * Sets the Gemini API key, creating or clearing the Gemini model.
     */
    public void setGeminiApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            this.geminiModel = null;
            logger.info("Gemini API key cleared.");
        } else {
            Optional<ChatLanguageModel> model = ChatModelFactory.createGeminiModel(apiKey);
            this.geminiModel = model.orElse(null);
            if (this.geminiModel != null) {
                logger.info("Gemini model configured successfully.");
            }
        }
    }

    /**
     * Returns true if the Gemini model is configured and available.
     */
    public boolean isGeminiAvailable() {
        return geminiModel != null;
    }

    /**
     * Handles unknown user input through a three-tier approach:
     * 1. Try keyword matching first (instant, no network)
     * 2. If no keyword match and Gemini is available, ask Gemini
     * 3. Otherwise return a fallback message
     *
     * @param userInput The user's input that was not recognized as a valid command.
     * @return A helpful response string.
     */
    public String handleUnknownInput(String userInput) {
        // Tier 1: Keyword matching
        Optional<String> keywordResponse = keywordMatcher.match(userInput);
        if (keywordResponse.isPresent()) {
            return keywordResponse.get();
        }

        // Tier 2: Gemini API
        if (geminiModel != null) {
            return chatWithGemini(userInput);
        }

        // Tier 3: Fallback
        return FALLBACK_MESSAGE;
    }

    /**
     * Sends a conversational question to Gemini and returns the response.
     * Maintains conversation history via ChatMemory.
     */
    private String chatWithGemini(String userInput) {
        try {
            chatMemory.add(UserMessage.from(userInput));

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(SystemMessage.from(CHAT_SYSTEM_PROMPT));
            messages.addAll(chatMemory.messages());

            String responseText = geminiModel.generate(messages).content().text();

            chatMemory.add(AiMessage.from(responseText));
            return responseText;
        } catch (Exception e) {
            logger.info("Gemini API error: " + e.getMessage());
            return "AI error: " + e.getMessage() + "\n" + FALLBACK_MESSAGE;
        }
    }
}
