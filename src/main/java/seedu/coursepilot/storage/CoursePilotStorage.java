package seedu.coursepilot.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.coursepilot.commons.exceptions.DataLoadingException;
import seedu.coursepilot.model.ReadOnlyCoursePilot;

/**
 * Represents a storage for {@link seedu.coursepilot.model.CoursePilot}.
 */
public interface CoursePilotStorage {

    /**
     * Returns the file path of the data file.
     */
    Path getCoursePilotFilePath();

    /**
     * Returns CoursePilot data as a {@link ReadOnlyCoursePilot}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyCoursePilot> readCoursePilot() throws DataLoadingException;

    /**
     * @see #getCoursePilotFilePath()
     */
    Optional<ReadOnlyCoursePilot> readCoursePilot(Path filePath) throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyCoursePilot} to the storage.
     * @param coursePilot cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveCoursePilot(ReadOnlyCoursePilot coursePilot) throws IOException;

    /**
     * @see #saveCoursePilot(ReadOnlyCoursePilot)
     */
    void saveCoursePilot(ReadOnlyCoursePilot coursePilot, Path filePath) throws IOException;

}
