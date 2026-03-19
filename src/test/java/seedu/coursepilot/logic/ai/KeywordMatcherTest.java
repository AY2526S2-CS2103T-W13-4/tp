package seedu.coursepilot.logic.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.coursepilot.logic.commands.AddCommand;
import seedu.coursepilot.logic.commands.DeleteCommand;
import seedu.coursepilot.logic.commands.EditCommand;
import seedu.coursepilot.logic.commands.FindCommand;
import seedu.coursepilot.logic.commands.ListCommand;

/**
 * Unit tests for {@link KeywordMatcher}.
 */
public class KeywordMatcherTest {

    private final KeywordMatcher matcher = new KeywordMatcher();

    @Test
    public void match_addKeyword_returnsAddUsage() {
        Optional<String> result = matcher.match("how do I add a student?");
        assertTrue(result.isPresent());
        assertEquals(AddCommand.MESSAGE_USAGE, result.get());
    }

    @Test
    public void match_deleteKeyword_returnsDeleteUsage() {
        Optional<String> result = matcher.match("how to delete someone");
        assertTrue(result.isPresent());
        assertEquals(DeleteCommand.MESSAGE_USAGE, result.get());
    }

    @Test
    public void match_editKeyword_returnsEditUsage() {
        Optional<String> result = matcher.match("how to edit a student");
        assertTrue(result.isPresent());
        assertEquals(EditCommand.MESSAGE_USAGE, result.get());
    }

    @Test
    public void match_findKeyword_returnsFindUsage() {
        Optional<String> result = matcher.match("how to search for someone");
        assertTrue(result.isPresent());
        assertEquals(FindCommand.MESSAGE_USAGE, result.get());
    }

    @Test
    public void match_listKeyword_returnsListUsage() {
        Optional<String> result = matcher.match("show me all students");
        assertTrue(result.isPresent());
        assertEquals(ListCommand.MESSAGE_USAGE, result.get());
    }

    @Test
    public void match_getStarted_returnsGettingStarted() {
        Optional<String> result = matcher.match("how can I get started?");
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("Welcome to CoursePilot"));
    }

    @Test
    public void match_noMatch_returnsEmpty() {
        Optional<String> result = matcher.match("what is the meaning of life");
        assertTrue(result.isEmpty());
    }

    @Test
    public void match_caseInsensitive_works() {
        Optional<String> result = matcher.match("How Do I ADD a student");
        assertTrue(result.isPresent());
        assertEquals(AddCommand.MESSAGE_USAGE, result.get());
    }
}
