package seedu.coursepilot.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.coursepilot.commons.core.LogsCenter;
import seedu.coursepilot.commons.exceptions.DataLoadingException;
import seedu.coursepilot.commons.exceptions.IllegalValueException;
import seedu.coursepilot.commons.util.FileUtil;
import seedu.coursepilot.commons.util.JsonUtil;
import seedu.coursepilot.model.ReadOnlyCoursePilot;

/**
 * A class to access CoursePilot data stored as a json file on the hard disk.
 */
public class JsonCoursePilotStorage implements CoursePilotStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonCoursePilotStorage.class);

    private Path filePath;

    public JsonCoursePilotStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getCoursePilotFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyCoursePilot> readCoursePilot() throws DataLoadingException {
        return readCoursePilot(filePath);
    }

    /**
     * Similar to {@link #readCoursePilot()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyCoursePilot> readCoursePilot(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableCoursePilot> jsonCoursePilot = JsonUtil.readJsonFile(
                filePath, JsonSerializableCoursePilot.class);
        if (!jsonCoursePilot.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonCoursePilot.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveCoursePilot(ReadOnlyCoursePilot coursePilot) throws IOException {
        saveCoursePilot(coursePilot, filePath);
    }

    /**
     * Similar to {@link #saveCoursePilot(ReadOnlyCoursePilot)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveCoursePilot(ReadOnlyCoursePilot coursePilot, Path filePath) throws IOException {
        requireNonNull(coursePilot);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableCoursePilot(coursePilot), filePath);
    }

}
