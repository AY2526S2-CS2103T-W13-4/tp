package seedu.coursepilot.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.coursepilot.model.AddressBook;
import seedu.coursepilot.model.ReadOnlyAddressBook;
import seedu.coursepilot.model.person.Email;
import seedu.coursepilot.model.person.MatricNumber;
import seedu.coursepilot.model.person.Name;
import seedu.coursepilot.model.person.Phone;
import seedu.coursepilot.model.person.Remark;
import seedu.coursepilot.model.person.Student;
import seedu.coursepilot.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    public static final Remark EMPTY_REMARK = new Remark("");

    public static Student[] getSamplePersons() {
        return new Student[] {
            new Student(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new MatricNumber("A000000"), EMPTY_REMARK,
                getTagSet("friends")),
            new Student(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new MatricNumber("A000001"), EMPTY_REMARK,
                getTagSet("colleagues", "friends")),
            new Student(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new MatricNumber("A000002"), EMPTY_REMARK,
                getTagSet("neighbours")),
            new Student(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new MatricNumber("A000003"), EMPTY_REMARK,
                getTagSet("family")),
            new Student(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new MatricNumber("A000004"), EMPTY_REMARK,
                getTagSet("classmates")),
            new Student(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new MatricNumber("A000005"), EMPTY_REMARK,
                getTagSet("colleagues"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Student sampleStudent : getSamplePersons()) {
            sampleAb.addPerson(sampleStudent);
        }
        return sampleAb;
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
