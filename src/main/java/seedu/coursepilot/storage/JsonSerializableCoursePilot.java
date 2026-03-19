package seedu.coursepilot.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.coursepilot.commons.exceptions.IllegalValueException;
import seedu.coursepilot.model.CoursePilot;
import seedu.coursepilot.model.ReadOnlyCoursePilot;
import seedu.coursepilot.model.student.Student;
import seedu.coursepilot.model.tutorial.Tutorial;

/**
 * An Immutable CoursePilot that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableCoursePilot {

    public static final String MESSAGE_DUPLICATE_STUDENT = "Students list contains duplicate student(s).";
    public static final String MESSAGE_DUPLICATE_TUTORIAL = "Tutorials list contains duplicate tutorial(s).";

    private final List<JsonAdaptedStudent> students = new ArrayList<>();
    private final List<JsonAdaptedTutorial> tutorials = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableCoursePilot} with the given students.
     */
    @JsonCreator
    public JsonSerializableCoursePilot(@JsonProperty("students") List<JsonAdaptedStudent> students,
                                       @JsonProperty("tutorials") List<JsonAdaptedTutorial> tutorials) {
        if (students != null) {
            this.students.addAll(students);
        }
        if (tutorials != null) {
            this.tutorials.addAll(tutorials);
        }
    }

    /**
     * Converts a given {@code ReadOnlyCoursePilot} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableCoursePilot}.
     */
    public JsonSerializableCoursePilot(ReadOnlyCoursePilot source) {
        students.addAll(source.getStudentList().stream().map(JsonAdaptedStudent::new).collect(Collectors.toList()));
        tutorials.addAll(source.getTutorialList().stream().map(JsonAdaptedTutorial::new).collect(Collectors.toList()));
    }

    /**
     * Converts this course pilot into the model's {@code CoursePilot} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public CoursePilot toModelType() throws IllegalValueException {
        CoursePilot coursePilot = new CoursePilot();
        for (JsonAdaptedStudent jsonAdaptedStudent : students) {
            Student student = jsonAdaptedStudent.toModelType();
            if (coursePilot.hasStudent(student)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_STUDENT);
            }
            coursePilot.addStudent(student);
        }
        for (JsonAdaptedTutorial jsonAdaptedTutorial : tutorials) {
            Tutorial tutorial = jsonAdaptedTutorial.toModelType();
            if (coursePilot.hasTutorial(tutorial)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_TUTORIAL);
            }
            coursePilot.addTutorial(tutorial);
        }
        return coursePilot;
    }

}
