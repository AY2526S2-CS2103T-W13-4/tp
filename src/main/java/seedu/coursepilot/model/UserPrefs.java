package seedu.coursepilot.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import seedu.coursepilot.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private Path coursePilotFilePath = Paths.get("data" , "addressbook.json");
    private String geminiApiKey = "";

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        requireNonNull(newUserPrefs);
        setGuiSettings(newUserPrefs.getGuiSettings());
        setCoursePilotFilePath(newUserPrefs.getCoursePilotFilePath());
        setGeminiApiKey(newUserPrefs.getGeminiApiKey());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    public Path getCoursePilotFilePath() {
        return coursePilotFilePath;
    }

    public void setCoursePilotFilePath(Path coursePilotFilePath) {
        requireNonNull(coursePilotFilePath);
        this.coursePilotFilePath = coursePilotFilePath;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey == null ? "" : geminiApiKey;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs)) {
            return false;
        }

        UserPrefs otherUserPrefs = (UserPrefs) other;
        return guiSettings.equals(otherUserPrefs.guiSettings)
                && coursePilotFilePath.equals(otherUserPrefs.coursePilotFilePath)
                && Objects.equals(geminiApiKey, otherUserPrefs.geminiApiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, coursePilotFilePath, geminiApiKey);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings);
        sb.append("\nLocal data file location : " + coursePilotFilePath);
        sb.append("\nGemini API key : " + (geminiApiKey.isEmpty() ? "not set" : "configured"));
        return sb.toString();
    }

}
