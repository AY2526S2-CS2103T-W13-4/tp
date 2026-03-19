package seedu.coursepilot.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import seedu.coursepilot.commons.core.GuiSettings;
import seedu.coursepilot.commons.core.LogsCenter;
import seedu.coursepilot.logic.commands.Command;
import seedu.coursepilot.logic.commands.CommandResult;
import seedu.coursepilot.logic.commands.exceptions.CommandException;
import seedu.coursepilot.logic.parser.CoursePilotParser;
import seedu.coursepilot.logic.parser.exceptions.ParseException;
import seedu.coursepilot.model.Model;
import seedu.coursepilot.model.ReadOnlyCoursePilot;
import seedu.coursepilot.model.student.Student;
import seedu.coursepilot.model.tutorial.Tutorial;
import seedu.coursepilot.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final CoursePilotParser coursePilotParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        coursePilotParser = new CoursePilotParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = coursePilotParser.parseCommand(commandText);
        commandResult = command.execute(model);

        try {
            storage.saveCoursePilot(model.getCoursePilot());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyCoursePilot getCoursePilot() {
        return model.getCoursePilot();
    }

    @Override
    public ObservableList<Student> getFilteredStudentList() {
        return model.getFilteredStudentList();
    }

    @Override
    public ObservableList<Tutorial> getFilteredTutorialList() {
        return model.getFilteredTutorialList();
    }

    @Override
    public Optional<Tutorial> getCurrentOperatingTutorial() {
        return model.getCurrentOperatingTutorial();
    }

    @Override
    public ObjectProperty<Tutorial> getCurrentOperatingTutorialProperty() {
        return model.getCurrentOperatingTutorialProperty();
    }



    @Override
    public Path getCoursePilotFilePath() {
        return model.getCoursePilotFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public String getGeminiApiKey() {
        return model.getGeminiApiKey();
    }

    @Override
    public void setGeminiApiKey(String apiKey) {
        model.setGeminiApiKey(apiKey);
    }
}
