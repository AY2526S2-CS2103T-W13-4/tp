package seedu.coursepilot.logic.parser;

import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_CAPACITY;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_MATRICNUMBER;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_TIMESLOT;
import static seedu.coursepilot.logic.parser.CliSyntax.PREFIX_TUTORIALCODE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
 * Provides context-aware autocomplete suggestions for the command box.
 * Suggestions are based on the current input text and follow the command syntax.
 */
public class CommandAutoCompleter {

    private static final List<String> COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, FindCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, SelectCommand.COMMAND_WORD);

    private static final List<String> TYPE_FLAGS = List.of("-student", "-tutorial");

    private static final List<String> FIND_FLAGS = Arrays.stream(FindCommand.Flag.values())
            .map(FindCommand.Flag::getValue)
            .collect(Collectors.toUnmodifiableList());

    private static final List<String> STUDENT_PREFIXES = List.of(
            PREFIX_NAME.toString(), PREFIX_PHONE.toString(), PREFIX_EMAIL.toString(),
            PREFIX_MATRICNUMBER.toString(), PREFIX_TAG.toString());
    private static final List<String> TUTORIAL_PREFIXES = List.of(
            PREFIX_TUTORIALCODE.toString(), PREFIX_DAY.toString(),
            PREFIX_TIMESLOT.toString(), PREFIX_CAPACITY.toString());
    private static final List<String> EDIT_PREFIXES = List.of(
            PREFIX_NAME.toString(), PREFIX_PHONE.toString(), PREFIX_EMAIL.toString(),
            PREFIX_MATRICNUMBER.toString(), PREFIX_TAG.toString());

    /**
     * Returns autocomplete suggestions based on the current input text.
     *
     * @param text The current text in the command box.
     * @return A list of suggestion strings to display, may be empty.
     */
    public List<String> getSuggestions(String text) {
        if (text == null || text.isEmpty()) {
            return COMMAND_WORDS;
        }

        String trimmed = text.stripLeading();
        String[] parts = trimmed.split("\\s+");
        assert parts.length >= 1 : "split on non-empty string should produce at least one part";

        String commandWord = parts[0].toLowerCase();

        // State 1: Typing the command word (no space after yet)
        if (!trimmed.contains(" ")) {
            return filterByPrefix(COMMAND_WORDS, commandWord);
        }

        // State 2+: Command word is complete
        if (!COMMAND_WORDS.contains(commandWord)) {
            return Collections.emptyList();
        }

        return getSuggestionsForCommand(commandWord, trimmed, parts);
    }

    /**
     * Returns suggestions for a specific command based on what has been typed after the command word.
     */
    private List<String> getSuggestionsForCommand(String command, String fullText, String[] parts) {
        switch (command) {
        case AddCommand.COMMAND_WORD:
            return getAddSuggestions(fullText, parts);
        case DeleteCommand.COMMAND_WORD:
            return getFlagSuggestions(TYPE_FLAGS, parts);
        case ListCommand.COMMAND_WORD:
            return getFlagSuggestions(TYPE_FLAGS, parts);
        case FindCommand.COMMAND_WORD:
            return getFlagSuggestions(FIND_FLAGS, parts);
        case EditCommand.COMMAND_WORD:
            return getEditSuggestions(fullText, parts);
        case ClearCommand.COMMAND_WORD:
            return Collections.emptyList();
        case HelpCommand.COMMAND_WORD:
            return Collections.emptyList();
        case ExitCommand.COMMAND_WORD:
            return Collections.emptyList();
        case SelectCommand.COMMAND_WORD:
            return Collections.emptyList();
        default:
            throw new AssertionError("Unknown command should not reach here: " + command);
        }
    }

    /**
     * Returns suggestions for the add command, which has sub-flags and then prefixes.
     */
    private List<String> getAddSuggestions(String fullText, String[] parts) {
        if (parts.length < 2) {
            return TYPE_FLAGS;
        }

        String flag = parts[1];

        // Still typing the flag
        if (parts.length == 2 && !fullText.endsWith(" ")) {
            return filterByPrefix(TYPE_FLAGS, flag);
        }

        // Flag is complete, suggest prefixes
        if ("-student".equals(flag)) {
            return getUnusedPrefixes(STUDENT_PREFIXES, fullText);
        } else if ("-tutorial".equals(flag)) {
            return getUnusedPrefixes(TUTORIAL_PREFIXES, fullText);
        }

        return Collections.emptyList();
    }

    /**
     * Returns suggestions for the edit command (index + prefixes).
     */
    private List<String> getEditSuggestions(String fullText, String[] parts) {
        // Need at least the index before suggesting prefixes
        if (parts.length < 2) {
            return Collections.emptyList();
        }
        return getUnusedPrefixes(EDIT_PREFIXES, fullText);
    }

    /**
     * Returns flag suggestions, filtering by what the user has started typing.
     */
    private List<String> getFlagSuggestions(List<String> flags, String[] parts) {
        if (parts.length == 1) {
            return flags;
        }
        if (parts.length == 2) {
            return filterByPrefix(flags, parts[1]);
        }
        return Collections.emptyList();
    }

    /**
     * Returns prefixes that have not yet been used in the current input.
     * The /tag prefix is always suggested since it can be used multiple times.
     */
    private List<String> getUnusedPrefixes(List<String> allPrefixes, String fullText) {
        if (fullText.endsWith(" ")) {
            return Collections.emptyList();
        }

        String[] parts = fullText.split("\\s+");
        String lastToken = parts[parts.length - 1];

        if (!lastToken.startsWith("/")) {
            return Collections.emptyList();
        }

        Set<String> usedPrefixes = Arrays.stream(parts)
                .filter(word -> word.startsWith("/"))
                .collect(Collectors.toSet());

        return allPrefixes.stream()
                .filter(prefix -> PREFIX_TAG.toString().equals(prefix) || !usedPrefixes.contains(prefix))
                .filter(prefix -> prefix.startsWith(lastToken) && !prefix.equals(lastToken))
                .collect(Collectors.toList());
    }

    /**
     * Filters a list of candidates to those that start with the given prefix.
     */
    private List<String> filterByPrefix(List<String> candidates, String prefix) {
        if (prefix.isEmpty()) {
            return candidates;
        }
        return candidates.stream()
                .filter(c -> c.startsWith(prefix) && !c.equals(prefix))
                .collect(Collectors.toList());
    }
}
