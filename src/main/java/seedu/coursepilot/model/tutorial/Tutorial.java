package seedu.coursepilot.model.tutorial;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import seedu.coursepilot.model.person.Student;

/**
 * Represents a tutorial in CoursePilot.
 * A tutorial has a code, scheduled day, time slot, a maximum capacity,
 * and a list of associated students.
 */
public class Tutorial {
    private final String tutorialCode;
    private final String day;
    private final String timeSlot;
    private final int capacity;
    private final List<Student> students;

    /**
     * Constructs a {@code Tutorial}.
     *
     * @param tutorialCode unique code identifying the tutorial
     * @param day the day the tutorial is held
     * @param timeSlot the time slot of the tutorial
     * @param capacity maximum number of students allowed
     * @param students students in this tutorial
     */
    public Tutorial(
        String tutorialCode, String day, String timeSlot, int capacity, List<Student> students) {
        requireNonNull(tutorialCode);
        requireNonNull(day);
        requireNonNull(timeSlot);
        requireNonNull(students);

        this.tutorialCode = tutorialCode;
        this.day = day;
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.students = new ArrayList<>(students);
    }

    /**
     * Returns the tutorial code.
     */
    public String getTutorialCode() {
        return tutorialCode;
    }

    /**
     * Returns the day the tutorial takes place.
     */
    public String getDay() {
        return day;
    }

    /**
     * Returns the time slot of the tutorial.
     */
    public String getTimeSlot() {
        return timeSlot;
    }

    /**
     * Returns the maximum capacity of the tutorial.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the list of students in this tutorial
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * Returns true if this tutorial contains a student with the same identity as {@code Student}
     */
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return students.stream().anyMatch(student::isSamePerson);
    }

    /**
     * Returns true if both tutorials have the same fields.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Tutorial)) {
            return false;
        }

        Tutorial otherTutorial = (Tutorial) other;
        return tutorialCode.equals(otherTutorial.tutorialCode)
        && day.equals(otherTutorial.day)
        && timeSlot.equals(otherTutorial.timeSlot)
        && capacity == otherTutorial.capacity
        && students.equals(otherTutorial.students);
    }

    /**
     * Returns the hash code based on tutorial attributes.
     */
    @Override
    public int hashCode() {
        return Objects.hash(tutorialCode, day, timeSlot, capacity, students);
    }
}
