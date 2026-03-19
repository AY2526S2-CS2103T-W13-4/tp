package seedu.coursepilot.testutil;

import seedu.coursepilot.model.CoursePilot;
import seedu.coursepilot.model.student.Student;

/**
 * A utility class to help with building Addressbook objects.
 * Example usage: <br>
 *     {@code CoursePilot ab = new CoursePilotBuilder().withStudent("John", "Doe").build();}
 */
public class CoursePilotBuilder {

    private CoursePilot coursePilot;

    public CoursePilotBuilder() {
        coursePilot = new CoursePilot();
    }

    public CoursePilotBuilder(CoursePilot coursePilot) {
        this.coursePilot = coursePilot;
    }

    /**
     * Adds a new {@code Student} to the {@code CoursePilot} that we are building.
     */
    public CoursePilotBuilder withStudent(Student student) {
        coursePilot.addStudent(student);
        return this;
    }

    public CoursePilot build() {
        return coursePilot;
    }
}
