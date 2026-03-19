package seedu.coursepilot.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.coursepilot.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.coursepilot.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.coursepilot.testutil.Assert.assertThrows;
import static seedu.coursepilot.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.coursepilot.logic.commands.AddCommand;
import seedu.coursepilot.logic.commands.ClearCommand;
import seedu.coursepilot.logic.commands.DeleteCommand;
import seedu.coursepilot.logic.commands.EditCommand;
import seedu.coursepilot.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.coursepilot.logic.commands.ExitCommand;
import seedu.coursepilot.logic.commands.FindCommand;
import seedu.coursepilot.logic.commands.HelpCommand;
import seedu.coursepilot.logic.commands.ListCommand;
import seedu.coursepilot.logic.parser.exceptions.ParseException;
import seedu.coursepilot.model.student.NameContainsKeywordsPredicate;
import seedu.coursepilot.model.student.Student;
import seedu.coursepilot.testutil.EditStudentDescriptorBuilder;
import seedu.coursepilot.testutil.StudentBuilder;
import seedu.coursepilot.testutil.StudentUtil;

public class CoursePilotParserTest {

    private final CoursePilotParser parser = new CoursePilotParser();

    @Test
    public void parseCommand_add() throws Exception {
        Student student = new StudentBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(StudentUtil.getAddCommand(student));
        assertEquals(new AddCommand(student), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " -student " + INDEX_FIRST_STUDENT.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_STUDENT, "student"), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Student student = new StudentBuilder().build();
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(student).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_STUDENT.getOneBased() + " " + StudentUtil.getEditStudentDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_STUDENT, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertEquals(new ListCommand(ListCommand.ListTarget.STUDENT),
                parser.parseCommand(ListCommand.COMMAND_WORD + " -student"));
        assertEquals(new ListCommand(ListCommand.ListTarget.TUTORIAL),
                parser.parseCommand(ListCommand.COMMAND_WORD + " -tutorial"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
