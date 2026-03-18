package seedu.coursepilot.ui;

import java.util.Optional;
import java.util.logging.Logger;

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
    private PersonListPanel personListPanel;
    private TutorialCodeListPanel tutorialCodeListPanel;
    private TutorialDetailsPanel tutorialDetailsPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private CommandBox commandBox;
    private final NaturalLanguageParser nlParser = new NaturalLanguageParser();

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

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

    private void setPersonListPanelVisible(boolean visible) {
        this.personListPanel.getRoot().setVisible(visible);
        this.personListPanel.getRoot().setManaged(visible);
    }

    private void setTutorialDetailsPanelVisible(boolean visible) {
        this.tutorialDetailsPanel.getRoot().setVisible(visible);
        this.tutorialDetailsPanel.getRoot().setManaged(visible);
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        tutorialCodeListPanel = new TutorialCodeListPanel(logic.getTutorialList());
        tutorialListPanelPlaceholder.getChildren().add(tutorialCodeListPanel.getRoot());

        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        setPersonListPanelVisible(false);
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        tutorialDetailsPanel = new TutorialDetailsPanel(logic.getTutorialList());
        setTutorialDetailsPanelVisible(true);
        personListPanelPlaceholder.getChildren().add(tutorialDetailsPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
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

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    private void updateCenterPanel(String commandText) {
        String trimmedCommand = commandText.trim();
        if (trimmedCommand.equals("list -tutorial")) {
            setPersonListPanelVisible(false);
            setTutorialDetailsPanelVisible(true);
        } else if (trimmedCommand.equals("list -student")) {
            setTutorialDetailsPanelVisible(false);
            setPersonListPanelVisible(true);
        }
    }

    /**
     * Executes the command and returns the result.
     * If the command is unknown, attempts AI translation via Ollama.
     *
     * @see seedu.coursepilot.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.getSuggestedCommand().isPresent()) {
                handleAiSuggestion(commandResult);
                return commandResult;
            }

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
                handleAiTranslation(commandText);
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
     * Handles unknown input by classifying it as a question or command,
     * then routing to chat or translation accordingly on a background thread.
     */
    private void handleAiTranslation(String userInput) {
        NaturalLanguageParser.InputType inputType = nlParser.classifyInput(userInput);

        if (inputType == NaturalLanguageParser.InputType.QUESTION) {
            resultDisplay.setFeedbackToUser("Thinking...");
            Thread chatThread = new Thread(() -> {
                String response = nlParser.chat(userInput);
                Platform.runLater(() -> resultDisplay.setFeedbackToUser(response));
            });
            chatThread.setDaemon(true);
            chatThread.start();
        } else {
            resultDisplay.setFeedbackToUser("Translating your input with AI...");
            Thread translationThread = new Thread(() -> {
                Optional<String> translated = nlParser.translate(userInput);
                Platform.runLater(() -> {
                    if (translated.isPresent()) {
                        commandBox.setCommandText(translated.get());
                        resultDisplay.setFeedbackToUser(
                                "AI suggested the command above. Press Enter to run, or edit it first.");
                    } else {
                        resultDisplay.setFeedbackToUser(
                                "Unknown command. AI could not interpret your input.\n"
                                + "Type 'help' to see available commands.");
                    }
                });
            });
            translationThread.setDaemon(true);
            translationThread.start();
        }
    }

    /**
     * Handles a command result that contains an AI-suggested command.
     */
    private void handleAiSuggestion(CommandResult commandResult) {
        commandResult.getSuggestedCommand().ifPresent(suggested -> {
            commandBox.setCommandText(suggested);
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
        });
    }
}
