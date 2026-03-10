package seedu.coursepilot.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.coursepilot.logic.commands.exceptions.CommandException;
import seedu.coursepilot.model.Model;

/**
 * Lists all students or tutorials based on the argument.
 */
public class ListCommand extends Command {

    public enum ListTarget {
        STUDENT,
        TUTORIAL
    }

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD
        + "Lists tutorial details or students in the current operating tutorial.\n"
        + "Parameters: -student | -tutorial\n"
        + "Examples: " + COMMAND_WORD + " -student\n"
        + "          " + COMMAND_WORD + " -tutorial";

    public static final String MESSAGE_SUCCESS_STUDENT =
        "Listed students in the current operating tutorial";

    public static final String MESSAGE_SUCCESS_TUTORIAL =
        "Listed tutorial details";

    public static final String MESSAGE_NO_CURRENT_OPERATING_TUTORIAL =
        "No current operating tutorial selected. Use find first.";

    private final ListTarget listTarget;

    public ListCommand(ListTarget listTarget) {
        this.listTarget = listTarget;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (listTarget == ListTarget.TUTORIAL) {
            model.clearCurrentOperatingTutorial();
            return new CommandResult(MESSAGE_SUCCESS_TUTORIAL);
        }

        if (model.getCurrentOperatingTutorial().isEmpty()) {
            throw new CommandException(MESSAGE_NO_CURRENT_OPERATING_TUTORIAL);
        }

        model.updateFilteredPersonList(
            student -> model.getCurrentOperatingTutorial().get().hasStudent(student)
        );
        return new CommandResult(MESSAGE_SUCCESS_STUDENT);
    }
}
