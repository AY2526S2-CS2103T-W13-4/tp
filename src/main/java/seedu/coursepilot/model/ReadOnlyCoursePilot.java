package seedu.coursepilot.model;

import javafx.collections.ObservableList;
import seedu.coursepilot.model.student.Student;
import seedu.coursepilot.model.tutorial.Tutorial;

/**
 * Unmodifiable view of an course pilot
 */
public interface ReadOnlyCoursePilot {

    /**
     * Returns an unmodifiable view of the students list.
     * This list will not contain any duplicate students.
     */
    ObservableList<Student> getStudentList();
    ObservableList<Tutorial> getTutorialList();
}
