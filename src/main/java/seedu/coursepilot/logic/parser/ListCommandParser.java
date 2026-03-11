package seedu.coursepilot.logic.parser;

import static seedu.coursepilot.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.coursepilot.logic.commands.ListCommand;
import seedu.coursepilot.logic.commands.ListCommand.ListTarget;
import seedu.coursepilot.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {


    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        if ("-student".equals(trimmedArgs)) {
            return new ListCommand(ListTarget.STUDENT);
        }

        if ("-tutorial".equals(trimmedArgs)) {
            return new ListCommand(ListTarget.TUTORIAL);
        }

        throw new ParseException(String.format(
            MESSAGE_INVALID_COMMAND_FORMAT,
            ListCommand.MESSAGE_USAGE));
    }
}
