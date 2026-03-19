package seedu.coursepilot.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.coursepilot.commons.core.LogsCenter;
import seedu.coursepilot.commons.exceptions.DataLoadingException;
import seedu.coursepilot.model.ReadOnlyCoursePilot;
import seedu.coursepilot.model.ReadOnlyUserPrefs;
import seedu.coursepilot.model.UserPrefs;

/**
 * Manages storage of CoursePilot data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private CoursePilotStorage coursePilotStorage;
    private UserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code CoursePilotStorage} and {@code UserPrefStorage}.
     */
    public StorageManager(CoursePilotStorage coursePilotStorage, UserPrefsStorage userPrefsStorage) {
        this.coursePilotStorage = coursePilotStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ CoursePilot methods ==============================

    @Override
    public Path getCoursePilotFilePath() {
        return coursePilotStorage.getCoursePilotFilePath();
    }

    @Override
    public Optional<ReadOnlyCoursePilot> readCoursePilot() throws DataLoadingException {
        return readCoursePilot(coursePilotStorage.getCoursePilotFilePath());
    }

    @Override
    public Optional<ReadOnlyCoursePilot> readCoursePilot(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return coursePilotStorage.readCoursePilot(filePath);
    }

    @Override
    public void saveCoursePilot(ReadOnlyCoursePilot coursePilot) throws IOException {
        saveCoursePilot(coursePilot, coursePilotStorage.getCoursePilotFilePath());
    }

    @Override
    public void saveCoursePilot(ReadOnlyCoursePilot coursePilot, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        coursePilotStorage.saveCoursePilot(coursePilot, filePath);
    }

}
