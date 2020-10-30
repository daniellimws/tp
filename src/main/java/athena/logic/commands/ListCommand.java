package athena.logic.commands;

import athena.Forecast;
import athena.Importance;
import athena.TaskList;
import athena.exceptions.EmptyTaskListException;
import athena.timetable.Timetable;
import athena.ui.AthenaUi;

import java.util.Objects;

/**
 * Handles the list command.
 */
public class ListCommand extends Command {
    private Importance taskImportance;
    private Forecast taskForecast;

    /**
     * Initializes the object with the parameters.
     *
     * @param importance Importance representing importance of task.
     * @param forecast   Forecast representing forecast of task.
     */
    public ListCommand(Importance importance, Forecast forecast) {
        taskImportance = importance;
        taskForecast = forecast;
    }

    /**
     * Calls TaskList to filter the list based on importance and
     * calls Ui to print the list of tasks.
     *
     * @param taskList Tasks list
     * @param athenaUi       Ui
     * @throws EmptyTaskListException Exception thrown when the task list is empty
     */
    @Override
    public void execute(TaskList taskList, AthenaUi athenaUi)
            throws EmptyTaskListException {
        if (taskList.getTasks().isEmpty()) {
            throw new EmptyTaskListException();
        }
        Timetable timetable = new Timetable(taskList, taskImportance, taskForecast);
        athenaUi.printTimetable(timetable);
    }

    /**
     * Determines if two objects have the same attributes.
     * @param o object
     * @return true if the two objects have the same attributes
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListCommand)) {
            return false;
        }
        ListCommand that = (ListCommand) o;
        return taskImportance == that.taskImportance && taskForecast == that.taskForecast;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskImportance, taskForecast);
    }
}
