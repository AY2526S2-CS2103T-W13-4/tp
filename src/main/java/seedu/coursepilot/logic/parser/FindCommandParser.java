package seedu.coursepilot.logic.parser;

import static seedu.coursepilot.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.coursepilot.logic.commands.FindCommand;
import seedu.coursepilot.logic.parser.exceptions.ParseException;
import seedu.coursepilot.model.person.EmailContainsKeywordsPredicate;
import seedu.coursepilot.model.person.MatricNumberStartsWithKeywordsPredicate;
import seedu.coursepilot.model.person.NameContainsKeywordsPredicate;
import seedu.coursepilot.model.person.PhoneStartsWithKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        switch (nameKeywords[0]) {
        case "/phone":
            return new FindCommand(new PhoneStartsWithKeywordsPredicate(Arrays.asList(nameKeywords)));
        case "/email":
            return new FindCommand(new EmailContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        case "/matric":
            return new FindCommand(new MatricNumberStartsWithKeywordsPredicate(Arrays.asList(nameKeywords)));
        default:
            return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        }
    }
}
