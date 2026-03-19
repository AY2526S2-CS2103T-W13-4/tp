package seedu.coursepilot.ui;

import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.coursepilot.commons.core.GuiSettings;
import seedu.coursepilot.commons.core.LogsCenter;
import seedu.coursepilot.logic.Logic;
import seedu.coursepilot.logic.ai.NaturalLanguageParser;
import seedu.coursepilot.logic.commands.CommandResult;
import seedu.coursepilot.logic.commands.exceptions.CommandException;
import seedu.coursepilot.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private StudentListPanel studentListPanel;
    private TutorialCodeListPanel tutorialCodeListPanel;
    private TutorialDetailsPanel tutorialDetailsPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private AiSettingsWindow aiSettingsWindow;
    private CommandBox commandBox;
    private final NaturalLanguageParser nlParser = new NaturalLanguageParser();
    private HostServices hostServices;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane studentListPanelPlaceholder;

    @FXML
    private StackPane tutorialListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();

        // Initialize Gemini if API key is configured
        String savedKey = logic.getGeminiApiKey();
        if (savedKey != null && !savedKey.isBlank()) {
            nlParser.setGeminiApiKey(savedKey);
        }
    }

    /**
     * Sets the HostServices for opening URLs in the browser.
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    private void setStudentListPanelVisible(boolean visible) {
        this.studentListPanel.getRoot().setVisible(visible);
        this.studentListPanel.getRoot().setManaged(visible);
    }

    private void setTutorialDetailsPanelVisible(boolean visible) {
        this.tutorialDetailsPanel.getRoot().setVisible(visible);
        this.tutorialDetailsPanel.getRoot().setManaged(visible);
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        tutorialCodeListPanel = new TutorialCodeListPanel(logic.getFilteredTutorialList(),
                logic.getCurrentOperatingTutorialProperty());
        tutorialListPanelPlaceholder.getChildren().add(tutorialCodeListPanel.getRoot());

        studentListPanel = new StudentListPanel(logic.getFilteredStudentList());
        setStudentListPanelVisible(false);
        studentListPanelPlaceholder.getChildren().add(studentListPanel.getRoot());

        tutorialDetailsPanel = new TutorialDetailsPanel(logic.getFilteredTutorialList());
        setTutorialDetailsPanelVisible(true);
        studentListPanelPlaceholder.getChildren().add(tutorialDetailsPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(
            logic.getCurrentOperatingTutorialProperty());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public StudentListPanel getStudentListPanel() {
        return studentListPanel;
    }

    private void updateCenterPanel(String commandText) {
        String trimmedCommand = commandText.trim();
        if (trimmedCommand.equals("list -tutorial")) {
            setStudentListPanelVisible(false);
            setTutorialDetailsPanelVisible(true);
        } else if (trimmedCommand.equals("list -student")) {
            setTutorialDetailsPanelVisible(false);
            setStudentListPanelVisible(true);
        }
    }

    /**
     * Opens the AI settings window or focuses on it if it's already opened.
     */
    @FXML
    public void handleAiSettings() {
        if (aiSettingsWindow == null || !aiSettingsWindow.isShowing()) {
            String currentKey = logic.getGeminiApiKey();
            aiSettingsWindow = new AiSettingsWindow(
                    this::onApiKeySaved, hostServices,
                    currentKey != null ? currentKey : "");
            aiSettingsWindow.show();
        } else {
            aiSettingsWindow.focus();
        }
    }

    /**
     * Callback when the user saves or clears the Gemini API key.
     */
    private void onApiKeySaved(String apiKey) {
        logic.setGeminiApiKey(apiKey);
        nlParser.setGeminiApiKey(apiKey);
    }

    /**
     * Executes the command and returns the result.
     * If the command is unknown, falls back to AI assistance.
     *
     * @see seedu.coursepilot.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            updateCenterPanel(commandText);

            return commandResult;
        } catch (ParseException e) {
            if (e.getMessage().contains("Unknown command")) {
                handleUnknownCommand(commandText);
                throw e;
            }
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        } catch (CommandException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /**
     * Handles an unknown command by trying keyword matching, then Gemini API.
     * Keyword matching is instant; Gemini runs on a background thread.
     */
    private void handleUnknownCommand(String userInput) {
        if (nlParser.isGeminiAvailable()) {
            // Check keyword match first (instant)
            Optional<String> keywordMatch =
                    new seedu.coursepilot.logic.ai.KeywordMatcher().match(userInput);
            if (keywordMatch.isPresent()) {
                resultDisplay.setFeedbackToUser(keywordMatch.get());
                return;
            }
            // Fall through to Gemini on background thread
            resultDisplay.setFeedbackToUser("Asking AI assistant...");
            Thread aiThread = new Thread(() -> {
                String response = nlParser.handleUnknownInput(userInput);
                Platform.runLater(() -> resultDisplay.setFeedbackToUser(response));
            });
            aiThread.setDaemon(true);
            aiThread.start();
        } else {
            // No Gemini — use keyword matcher with fallback
            String response = nlParser.handleUnknownInput(userInput);
            resultDisplay.setFeedbackToUser(response);
        }
    }
}
