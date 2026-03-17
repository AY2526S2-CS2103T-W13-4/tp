package seedu.coursepilot.logic.commands;

import static seedu.coursepilot.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.coursepilot.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.coursepilot.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.coursepilot.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.coursepilot.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.coursepilot.model.Model;
import seedu.coursepilot.model.ModelManager;
import seedu.coursepilot.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listTutorial_success() {
        assertCommandSuccess(new ListCommand(ListCommand.ListTarget.TUTORIAL),
            model, ListCommand.MESSAGE_SUCCESS_TUTORIAL, expectedModel);
    }

    @Test
    public void execute_listStudentWithNoCurrentOperatingTutorial_throwsCommandException() {
        assertCommandFailure(new ListCommand(ListCommand.ListTarget.STUDENT),
            model, ListCommand.MESSAGE_NO_CURRENT_OPERATING_TUTORIAL);
    }

    @Test
    public void execute_listStudentWithCurrentOperatingTutorial_success() {
        // Set the current operating tutorial to the first tutorial in the list
        model.setCurrentOperatingTutorial(model.getTutorialList().get(0));
        expectedModel.setCurrentOperatingTutorial(expectedModel.getTutorialList().get(0));

        // Update filter to show only students in the current tutorial
        expectedModel.updateFilteredPersonList(student ->
            expectedModel.getCurrentOperatingTutorial().get().hasStudent(student));

        assertCommandSuccess(new ListCommand(ListCommand.ListTarget.STUDENT),
            model, ListCommand.MESSAGE_SUCCESS_STUDENT, expectedModel);
    }
}
