package seedu.coursepilot.ui;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
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

    private final CommandExecutor commandExecutor;
    private final CommandAutoCompleter autoCompleter = new CommandAutoCompleter();
    private final ContextMenu suggestionMenu = new ContextMenu();
    private boolean suppressSuggestions = false;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> {
            setStyleToDefault();
            if (suppressSuggestions) {
                return;
            }
            updateSuggestions();
        });
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        commandTextField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                suggestionMenu.hide();
            }
        });
        commandTextField.setOnMouseClicked(event -> suggestionMenu.hide());
    }

    /**
     * Handles key presses for Tab completion, Enter to execute, and Escape to dismiss.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            suggestionMenu.hide();
            // Don't consume — let the onAction handler in FXML fire
        } else if (event.getCode() == KeyCode.TAB) {
            if (suggestionMenu.isShowing() && !suggestionMenu.getItems().isEmpty()) {
                applySuggestion(((Label) ((CustomMenuItem)
                        suggestionMenu.getItems().get(0)).getContent()).getText());
                event.consume();
            }
        } else if (event.getCode() == KeyCode.ESCAPE) {
            suggestionMenu.hide();
        }
    }

    /**
     * Updates the autocomplete suggestion menu based on the current text.
     */
    private void updateSuggestions() {
        String text = commandTextField.getText();

        if (text == null || text.isEmpty()) {
            suggestionMenu.hide();
            return;
        }

        List<String> suggestions = autoCompleter.getSuggestions(text);

        suggestionMenu.getItems().clear();

        if (suggestions.isEmpty()) {
            suggestionMenu.hide();
            return;
        }

        suggestions.stream()
                .limit(MAX_SUGGESTIONS)
                .forEach(suggestion -> {
                    Label label = new Label(suggestion);
                    label.getStyleClass().add("autocomplete-label");
                    CustomMenuItem item = new CustomMenuItem(label, true);
                    item.setOnAction(e -> applySuggestion(suggestion));
                    suggestionMenu.getItems().add(item);
                });

        if (!suggestionMenu.isShowing()) {
            suggestionMenu.show(commandTextField, Side.BOTTOM, 0, 0);
        }
    }

    /**
     * Applies the selected suggestion to the command text field.
     */
    private void applySuggestion(String suggestion) {
        String text = commandTextField.getText();
        String trimmed = text.stripLeading();
        String[] parts = trimmed.split("\\s+");

        String newText;
        if (!text.contains(" ")) {
            newText = suggestion + " ";
        } else if (parts.length > 0 && !text.endsWith(" ")) {
            String lastToken = parts[parts.length - 1];
            newText = text.substring(0, text.lastIndexOf(lastToken)) + suggestion + " ";
        } else {
            newText = text + suggestion + " ";
        }

        suppressSuggestions = true;
        commandTextField.setText(newText);
        commandTextField.positionCaret(newText.length());
        suppressSuggestions = false;
        suggestionMenu.hide();
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        suggestionMenu.hide();
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            suppressSuggestions = true;
            commandTextField.setText("");
            suppressSuggestions = false;
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
