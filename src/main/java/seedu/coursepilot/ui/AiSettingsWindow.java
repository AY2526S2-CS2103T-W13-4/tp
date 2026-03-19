package seedu.coursepilot.ui;

import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seedu.coursepilot.commons.core.LogsCenter;

/**
 * Controller for the AI Settings window where users can configure their Gemini API key.
 */
public class AiSettingsWindow extends UiPart<Stage> {

    public static final String GEMINI_URL = "https://aistudio.google.com/apikey";

    private static final Logger logger = LogsCenter.getLogger(AiSettingsWindow.class);
    private static final String FXML = "AiSettingsWindow.fxml";

    private final Consumer<String> onApiKeySaved;
    private final HostServices hostServices;

    @FXML
    private TextField apiKeyField;

    @FXML
    private Label statusLabel;

    /**
     * Creates a new AiSettingsWindow.
     *
     * @param root Stage to use as the root.
     * @param onApiKeySaved Callback invoked when the user saves an API key.
     * @param hostServices HostServices for opening URLs in browser.
     * @param currentKey The current API key to pre-fill, or empty string.
     */
    public AiSettingsWindow(Stage root, Consumer<String> onApiKeySaved,
                            HostServices hostServices, String currentKey) {
        super(FXML, root);
        this.onApiKeySaved = onApiKeySaved;
        this.hostServices = hostServices;
        if (currentKey != null && !currentKey.isBlank()) {
            apiKeyField.setText(currentKey);
            statusLabel.setText("AI features are enabled.");
            statusLabel.setStyle("-fx-text-fill: #4CAF50;");
        }
    }

    /**
     * Creates a new AiSettingsWindow with a new Stage.
     */
    public AiSettingsWindow(Consumer<String> onApiKeySaved,
                            HostServices hostServices, String currentKey) {
        this(new Stage(), onApiKeySaved, hostServices, currentKey);
    }

    /**
     * Shows the AI settings window.
     */
    public void show() {
        logger.fine("Showing AI settings window.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Opens the Google AI Studio link in the default browser.
     */
    @FXML
    private void openGeminiLink() {
        if (hostServices != null) {
            hostServices.showDocument(GEMINI_URL);
        }
    }

    /**
     * Saves the API key and notifies the callback.
     */
    @FXML
    private void handleSave() {
        String apiKey = apiKeyField.getText().strip();
        if (apiKey.isEmpty()) {
            statusLabel.setText("Please enter an API key.");
            statusLabel.setStyle("-fx-text-fill: #FF5252;");
            return;
        }
        onApiKeySaved.accept(apiKey);
        statusLabel.setText("API key saved. AI features are now enabled.");
        statusLabel.setStyle("-fx-text-fill: #4CAF50;");
    }

    /**
     * Clears the API key and notifies the callback.
     */
    @FXML
    private void handleClear() {
        apiKeyField.setText("");
        onApiKeySaved.accept("");
        statusLabel.setText("API key cleared. AI features are disabled.");
        statusLabel.setStyle("-fx-text-fill: #FF9800;");
    }
}
