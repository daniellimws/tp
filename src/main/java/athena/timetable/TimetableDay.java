package athena.timetable;

import athena.TaskList;
import athena.task.Task;
import athena.ui.ColorText;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents a day in the timetable. Contains the date and the list of tasks,
 * which is a subset of all the tasks that the user registered into the program.
 */
public class TimetableDay {
    private LocalDate date;
    private TaskList taskList;

    /**
     * Initializes the object with the date and an empty task list.
     *
     * @param date Date represented by this object.
     */
    public TimetableDay(LocalDate date) {
        assert date != null;
        this.date = date;
        this.taskList = new TaskList();
    }

    /**
     * Getter for date.
     *
     * @return date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Getter for taskList.
     *
     * @return taskList.
     */
    public TaskList getTaskList() {
        return taskList;
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to be added.
     */
    public void addTask(Task task) {
        taskList.addTask(task);
    }

    /**
     * Generates a string containing the date and list of tasks that is to be
     * printed to the user.
     *
     * @return A string containing the date and list of tasks.
     */
    @Override
    public String toString() {
        String message = "";
        if (taskList.getTasks().isEmpty()) {

        } else {
            message = date.toString() + ":\n";
            ColorText colorText = new ColorText();
            message = colorText.toPurple(message);
            for (Task task : taskList.getTasks()) {
                message += task + "\n";
            }
        }
        return message;
    }
}
