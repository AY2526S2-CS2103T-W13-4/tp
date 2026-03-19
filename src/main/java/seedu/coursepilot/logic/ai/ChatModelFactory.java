package seedu.coursepilot.logic.ai;

import java.util.Optional;
import java.util.logging.Logger;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import seedu.coursepilot.commons.core.LogsCenter;

/**
 * Factory for creating LangChain4j chat model instances configured for CoursePilot.
 */
public class ChatModelFactory {

    private static final Logger logger = LogsCenter.getLogger(ChatModelFactory.class);
    private static final String DEFAULT_MODEL_NAME = "gemini-2.0-flash";

    /**
     * Creates a ChatLanguageModel using the Google Gemini API with the given key.
     *
     * @param apiKey The Google Gemini API key.
     * @return An Optional containing the model if creation succeeded, or empty on failure.
     */
    public static Optional<ChatLanguageModel> createGeminiModel(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }
        try {
            ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(DEFAULT_MODEL_NAME)
                    .build();
            return Optional.of(model);
        } catch (Exception e) {
            logger.info("Failed to create Gemini model: " + e.getMessage());
            return Optional.empty();
        }
    }
}
