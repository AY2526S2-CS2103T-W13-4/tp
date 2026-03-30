package seedu.coursepilot.ui;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import seedu.coursepilot.logic.commands.CommandResult;
import seedu.coursepilot.logic.commands.exceptions.CommandException;
import seedu.coursepilot.logic.parser.CommandAutoCompleter;
import seedu.coursepilot.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final int MAX_SUGGESTIONS = 8;
    private static final double LIST_CELL_HEIGHT = 30.0;

    private final CommandExecutor commandExecutor;
    private final CommandAutoCompleter autoCompleter = new CommandAutoCompleter();

    private final Popup suggestionPopup = new Popup();
    private final ListView<String> suggestionListView = new ListView<>();

    private boolean isApplyingSuggestion = false;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;

        setupSuggestionUi();

        commandTextField.textProperty().addListener((unused1, unused2, newVal) -> {
            setStyleToDefault();
            updateSuggestions(newVal);
        });

        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

        commandTextField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                commandTextField.requestFocus();
                commandTextField.end();
            }
        });
    }

    /**
     * Initializes the UI constraints for the suggestion box.
     */
    private void setupSuggestionUi() {
        suggestionListView.getStyleClass().add("suggestion-list-view");

        suggestionListView.setFocusTraversable(false);
        suggestionListView.setMouseTransparent(false);

        suggestionListView.prefWidthProperty().bind(commandTextField.widthProperty());

        suggestionPopup.getContent().add(suggestionListView);
        suggestionPopup.setAutoHide(true);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            if (suggestionPopup.isShowing()) {
                String selected = suggestionListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    applySuggestion(selected);
                }
            }
            event.consume();
            return;
        }

        if (!suggestionPopup.isShowing()) {
            return;
        }

        switch (event.getCode()) {
        case DOWN:
            int nextIndex = (suggestionListView.getSelectionModel().getSelectedIndex() + 1)
                    % suggestionListView.getItems().size();
            suggestionListView.getSelectionModel().select(nextIndex);
            event.consume();
            break;

        case UP:
            int size = suggestionListView.getItems().size();
            int prevIndex = (suggestionListView.getSelectionModel().getSelectedIndex() - 1 + size) % size;
            suggestionListView.getSelectionModel().select(prevIndex);
            event.consume();
            break;

        case ENTER:
            String selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                applySuggestion(selected);
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                suggestionPopup.hide();
            }
            break;

        case ESCAPE:
            suggestionPopup.hide();
            event.consume();
            break;

        default:
            break;
        }
    }

    private void updateSuggestions(String text) {
        List<String> suggestions = autoCompleter.getSuggestions(text);

        if (suggestions.isEmpty() || text.isEmpty()) {
            suggestionPopup.hide();
            return;
        }

        ObservableList<String> items = FXCollections.observableArrayList(suggestions);
        if (items.size() > MAX_SUGGESTIONS) {
            items = FXCollections.observableArrayList(items.subList(0, MAX_SUGGESTIONS));
        }

        suggestionListView.setItems(items);

        suggestionListView.getSelectionModel().selectFirst();

        double height = Math.min(items.size() * LIST_CELL_HEIGHT + 2, 200);
        suggestionListView.setPrefHeight(height);

        showPopupAboveTextField(height);
    }

    private void showPopupAboveTextField(double listHeight) {
        if (commandTextField.getScene() == null) {
            return;
        }

        Bounds bounds = commandTextField.localToScreen(commandTextField.getBoundsInLocal());
        double x = bounds.getMinX();
        double y = bounds.getMinY() - listHeight;

        if (!suggestionPopup.isShowing()) {
            suggestionPopup.show(commandTextField.getScene().getWindow(), x, y);
        } else {
            suggestionPopup.setX(x);
            suggestionPopup.setY(y);
        }
    }

    private void applySuggestion(String suggestion) {
        isApplyingSuggestion = true;

        String text = commandTextField.getText();
        String[] parts = text.stripLeading().split("\\s+");

        String newText;
        if (!text.contains(" ")) {
            newText = suggestion + " ";
        } else if (parts.length > 0 && !text.endsWith(" ")) {
            String lastToken = parts[parts.length - 1];
            newText = text.substring(0, text.lastIndexOf(lastToken)) + suggestion + " ";
        } else {
            newText = text + suggestion + " ";
        }

        commandTextField.setText(newText);
        commandTextField.positionCaret(newText.length());
        suggestionPopup.hide();

        isApplyingSuggestion = false;
    }

    /**
     * Handles button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isEmpty()) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            isApplyingSuggestion = true;
            commandTextField.setText("");
            isApplyingSuggestion = false;
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.coursepilot.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
