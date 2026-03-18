package seedu.coursepilot.logic.ai;

import java.time.Duration;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * Factory for creating LangChain4j chat model instances configured for CoursePilot.
 */
public class ChatModelFactory {

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL_NAME = "qwen2.5:1.5b";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Creates a ChatLanguageModel configured to use the local Ollama instance.
     */
    public static ChatLanguageModel createChatModel() {
        return createChatModel(DEFAULT_BASE_URL, DEFAULT_MODEL_NAME, DEFAULT_TIMEOUT);
    }

    /**
     * Creates a ChatLanguageModel with the specified configuration.
     *
     * @param baseUrl The base URL of the Ollama server.
     * @param modelName The name of the model to use.
     * @param timeout The timeout duration for requests.
     * @return A configured ChatLanguageModel instance.
     */
    public static ChatLanguageModel createChatModel(String baseUrl, String modelName, Duration timeout) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(timeout)
                .build();
    }
}
