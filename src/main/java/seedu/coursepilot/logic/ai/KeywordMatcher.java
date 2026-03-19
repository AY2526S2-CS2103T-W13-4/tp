package seedu.coursepilot.logic.ai;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import seedu.coursepilot.logic.commands.AddCommand;
import seedu.coursepilot.logic.commands.ClearCommand;
import seedu.coursepilot.logic.commands.DeleteCommand;
import seedu.coursepilot.logic.commands.EditCommand;
import seedu.coursepilot.logic.commands.ExitCommand;
import seedu.coursepilot.logic.commands.FindCommand;
import seedu.coursepilot.logic.commands.HelpCommand;
import seedu.coursepilot.logic.commands.ListCommand;
import seedu.coursepilot.logic.commands.SelectCommand;

/**
 * Matches user input against known keywords and returns hardcoded help responses.
 */
public class KeywordMatcher {

    private static final String GETTING_STARTED_RESPONSE =
            "Welcome to CoursePilot! Here's how to get started:\n"
            + "1. Use 'add -student' to add students\n"
            + "2. Use 'add -tutorial' to add tutorials\n"
            + "3. Use 'list -student' or 'list -tutorial' to view them\n"
            + "4. Use 'select TUTORIAL_CODE' to select a tutorial\n"
            + "5. Use 'find' to search for students\n"
            + "6. Type 'help' to open the user guide";

    private static final Map<String[], String> KEYWORD_RESPONSES = new LinkedHashMap<>();

    static {
        KEYWORD_RESPONSES.put(
                new String[]{"get started", "start", "begin", "new here", "tutorial"},
                GETTING_STARTED_RESPONSE);
        KEYWORD_RESPONSES.put(
                new String[]{"add", "create", "new student", "new tutorial"},
                AddCommand.MESSAGE_USAGE);
        KEYWORD_RESPONSES.put(
                new String[]{"edit", "change", "update", "modify"},
                EditCommand.MESSAGE_USAGE);
        KEYWORD_RESPONSES.put(
                new String[]{"delete", "remove", "drop"},
                DeleteCommand.MESSAGE_USAGE);
        KEYWORD_RESPONSES.put(
                new String[]{"find", "search", "look for"},
                FindCommand.MESSAGE_USAGE);
        KEYWORD_RESPONSES.put(
                new String[]{"list", "show", "display", "all"},
                ListCommand.MESSAGE_USAGE);
        KEYWORD_RESPONSES.put(
                new String[]{"select", "choose", "pick"},
                SelectCommand.MESSAGE_USAGE);
        KEYWORD_RESPONSES.put(
                new String[]{"clear", "reset", "erase"},
                ClearCommand.COMMAND_WORD + ": Clears all entries from CoursePilot.");
        KEYWORD_RESPONSES.put(
                new String[]{"exit", "quit", "close"},
                ExitCommand.COMMAND_WORD + ": Exits the application.");
        KEYWORD_RESPONSES.put(
                new String[]{"help", "guide", "instructions", "commands"},
                HelpCommand.MESSAGE_USAGE + "\n\nAvailable commands: add, edit, delete, find,"
                + " list, select, clear, help, exit");
    }

    /**
     * Attempts to match user input against known keywords and return a helpful response.
     *
     * @param userInput The user's natural language input.
     * @return An Optional containing the matched response, or empty if no match found.
     */
    public Optional<String> match(String userInput) {
        String lower = userInput.strip().toLowerCase();

        for (Map.Entry<String[], String> entry : KEYWORD_RESPONSES.entrySet()) {
            for (String keyword : entry.getKey()) {
                if (lower.contains(keyword)) {
                    return Optional.of(entry.getValue());
                }
            }
        }

        return Optional.empty();
    }
}
