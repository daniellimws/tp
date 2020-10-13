package athena.exceptions;

import athena.Ui;

public class EmptyTaskListException extends CommandException {
    public EmptyTaskListException() {

    }

    /**
     * Prints an error message when the user tries to list out their tasks, but does not have any tasks in the list.
     */
    @Override
    public void printErrorMessage() {
        Ui ui = new Ui();
        ui.printEmptyTaskListException();
    }
}