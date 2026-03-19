package seedu.coursepilot.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.coursepilot.commons.exceptions.DataLoadingException;
import seedu.coursepilot.model.ReadOnlyCoursePilot;
import seedu.coursepilot.model.ReadOnlyUserPrefs;
import seedu.coursepilot.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage extends CoursePilotStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getCoursePilotFilePath();

    @Override
    Optional<ReadOnlyCoursePilot> readCoursePilot() throws DataLoadingException;

    @Override
    void saveCoursePilot(ReadOnlyCoursePilot coursePilot) throws IOException;

}
