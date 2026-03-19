package seedu.coursepilot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.coursepilot.testutil.TypicalStudents.getTypicalCoursePilot;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.coursepilot.commons.core.GuiSettings;
import seedu.coursepilot.model.CoursePilot;
import seedu.coursepilot.model.ReadOnlyCoursePilot;
import seedu.coursepilot.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonCoursePilotStorage coursePilotStorage = new JsonCoursePilotStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(coursePilotStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void coursePilotReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonCoursePilotStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonCoursePilotStorageTest} class.
         */
        CoursePilot original = getTypicalCoursePilot();
        storageManager.saveCoursePilot(original);
        ReadOnlyCoursePilot retrieved = storageManager.readCoursePilot().get();
        assertEquals(original, new CoursePilot(retrieved));
    }

    @Test
    public void getCoursePilotFilePath() {
        assertNotNull(storageManager.getCoursePilotFilePath());
    }

}
