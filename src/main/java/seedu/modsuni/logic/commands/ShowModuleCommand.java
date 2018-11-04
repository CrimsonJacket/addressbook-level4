package seedu.modsuni.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.modsuni.commons.core.EventsCenter;
import seedu.modsuni.commons.core.index.Index;
import seedu.modsuni.commons.events.ui.JumpToDatabaseListRequestEvent;
import seedu.modsuni.commons.events.ui.ShowDatabaseTabRequestEvent;
import seedu.modsuni.logic.CommandHistory;
import seedu.modsuni.logic.commands.exceptions.CommandException;
import seedu.modsuni.model.Model;
import seedu.modsuni.model.module.Code;
import seedu.modsuni.model.module.Module;

/**
 * Display the information of a module
 */
public class ShowModuleCommand extends Command {

    public static final String COMMAND_WORD = "showModule";
    public static final String MESSAGE_SUCCESS = "Module has been showed: %1$s";
    public static final String MESSAGE_MODULE_NOT_EXISTS_IN_DATABASE =
            "This module can not be showed because it does not exist in our database";

    private final Code toSearch;
    private Module toShow;

    public ShowModuleCommand(Code code) {
        requireNonNull(code);
        toSearch = code;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        Optional<Module> optionalModule = model.searchCodeInDatabase(toSearch);

        if (optionalModule.isPresent()) {
            toShow = optionalModule.get();
        } else {
            throw new CommandException(MESSAGE_MODULE_NOT_EXISTS_IN_DATABASE);
        }

        Index index = model.searchForIndexInDatabase(toShow);

        EventsCenter.getInstance().post(new ShowDatabaseTabRequestEvent());
        EventsCenter.getInstance().post(new JumpToDatabaseListRequestEvent(index));
        return new CommandResult(String.format(MESSAGE_SUCCESS, toSearch));
    }
}
