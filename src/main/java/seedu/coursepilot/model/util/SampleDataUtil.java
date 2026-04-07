package seedu.coursepilot.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.coursepilot.model.CoursePilot;
import seedu.coursepilot.model.ReadOnlyCoursePilot;
import seedu.coursepilot.model.student.Email;
import seedu.coursepilot.model.student.MatricNumber;
import seedu.coursepilot.model.student.Name;
import seedu.coursepilot.model.student.Phone;
import seedu.coursepilot.model.student.Student;
import seedu.coursepilot.model.tag.Tag;
import seedu.coursepilot.model.tutorial.Capacity;
import seedu.coursepilot.model.tutorial.Day;
import seedu.coursepilot.model.tutorial.TimeSlot;
import seedu.coursepilot.model.tutorial.Tutorial;
import seedu.coursepilot.model.tutorial.TutorialCode;

/**
 * Contains utility methods for populating {@code CoursePilot} with sample data.
 */
public class SampleDataUtil {
    public static Student[] getSampleStudents1() {
        return new Student[] {
            new Student(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new MatricNumber("A000000"),
                getTagSet("DeanList")),
            new Student(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new MatricNumber("A000001"),
                getTagSet("HonorRoll", "DeanList")),
        };
    }

    public static Student[] getSampleStudents2() {
        return new Student[] {
            new Student(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new MatricNumber("A000002"),
                getTagSet("Improving")),
            new Student(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new MatricNumber("A000003"),
                getTagSet("Distinction", "HonorRoll")),
        };
    }

    public static Student[] getSampleStudents3() {
        return new Student[] {
            new Student(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new MatricNumber("A000004"),
                getTagSet("Exceptional", "DeanList", "Distinction")),
            new Student(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new MatricNumber("A000005"),
                getTagSet("Underperforming", "Failing"))
        };
    }

    public static ObservableList<Tutorial> getSampleTutorials() {
        return FXCollections.observableArrayList(
                new Tutorial(new TutorialCode("T01"), new Day("Mon"),
                        new TimeSlot("10:00-11:00"), new Capacity(20)),
                new Tutorial(new TutorialCode("T02"), new Day("Wed"),
                        new TimeSlot("12:00-13:00"), new Capacity(15)),
                new Tutorial(new TutorialCode("T03"), new Day("Fri"),
                        new TimeSlot("14:00-15:00"), new Capacity(10))
        );
    }

    public static ReadOnlyCoursePilot getSampleCoursePilot() {
        CoursePilot sampleCp = new CoursePilot();
        ObservableList<Tutorial> sampleTutorials = getSampleTutorials();

        for (Tutorial tutorial : sampleTutorials) {
            sampleCp.addTutorial(tutorial);
        }

        Student[][] allSampleBatches = {getSampleStudents1(), getSampleStudents2(), getSampleStudents3()};

        for (int i = 0; i < allSampleBatches.length && i < sampleTutorials.size(); i++) {
            Tutorial target = sampleTutorials.get(i);
            for (Student student : allSampleBatches[i]) {
                sampleCp.addStudent(student);
                target.addStudent(student);
            }
        }

        return sampleCp;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
