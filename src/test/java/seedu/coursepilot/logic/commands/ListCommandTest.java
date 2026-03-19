package seedu.coursepilot.logic.commands;

import static seedu.coursepilot.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.coursepilot.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.coursepilot.logic.commands.CommandTestUtil.showStudentAtIndex;
import static seedu.coursepilot.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.coursepilot.testutil.TypicalStudents.getTypicalCoursePilot;

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
        model = new ModelManager(getTypicalCoursePilot(), new UserPrefs());
        expectedModel = new ModelManager(model.getCoursePilot(), new UserPrefs());
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
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);
        expectedModel.setCurrentOperatingTutorial(expectedModel.getFilteredTutorialList().get(0));
        expectedModel.updateFilteredStudentList(student ->
            expectedModel.getCurrentOperatingTutorial().get().hasStudent(student));
        model.setCurrentOperatingTutorial(model.getFilteredTutorialList().get(0));

        assertCommandSuccess(new ListCommand(ListCommand.ListTarget.STUDENT),
            model, ListCommand.MESSAGE_SUCCESS_STUDENT, expectedModel);
    }
}
