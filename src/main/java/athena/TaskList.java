package athena;

import athena.exceptions.ClashInTaskException;
import athena.exceptions.InvalidTimeFormatException;
import athena.exceptions.TaskDuringSleepTimeException;
import athena.exceptions.TaskNotFoundException;
import athena.task.Task;
import athena.task.taskfilter.ForecastFilter;
import athena.task.taskfilter.TaskFilter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The list that stores the user's tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;
    private int maxNumber = -1;

    /**
     * Creates a new TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a new TaskList using an existing ArrayList.
     *
     * @param tasks ArrayList to convert to TaskList
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>();
        this.tasks.addAll(tasks);

        for (Task task : tasks) {
            maxNumber = Math.max(maxNumber, task.getNumber());
        }
    }

    /**
     * Gets an ArrayList of the tasks.
     *
     * @return The ArrayList of the tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Creates a new task.
     *
     * @param number     Task number
     * @param name       Task name
     * @param startTime  Start time of the task
     * @param duration   Duration of the task
     * @param deadline   Due date of the task
     * @param recurrence When the task occurs again
     * @param importance Importance of the task
     * @param notes      Additional notes
     * @return Task as Task object
     */
    private Task createTask(int number, String name, String startTime, String duration, String deadline,
                            String recurrence, Importance importance, String notes, Boolean isFlexible)
            throws TaskDuringSleepTimeException {
        Task task = new Task(name, startTime, duration, deadline, recurrence, importance, notes, number, isFlexible);
        return task;
    }

    /**
     * Returns size of the task list.
     *
     * @return Size of the task list.
     */
    public int getTaskListSize() {
        return tasks.size();
    }

    /**
     * Adds a task to the task list.
     *
     * @param task Task to be added.
     */
    public void addTask(Task task) {
        updateMaxNumber(task.getNumber());
        tasks.add(task);
    }

    /**
     * Adds a task to the task list.
     *
     * @param number     Number assigned to the task
     * @param name       Name of task
     * @param startTime  Start time of task
     * @param duration   Duration of task
     * @param deadline   Deadline of task
     * @param recurrence Recurrence of task
     * @param importance Importance of task
     * @param notes      Additional notes of task
     */

    public void addTask(int number, String name, String startTime, String duration,
                        String deadline, String recurrence,
                        Importance importance, String notes, boolean isFlexible)
            throws ClashInTaskException, TaskDuringSleepTimeException, InvalidTimeFormatException {
        try {
            Task task = createTask(number, name, startTime,
                    duration, deadline, recurrence, importance, notes, isFlexible);
            decrementMaxNumber();
            checkClash(task);
            updateMaxNumber(number);
            tasks.add(task);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeFormatException();
        }
    }

    /**
     * Adds a task to the task list.
     *
     * @param name       Name of task
     * @param startTime  Start time of task
     * @param duration   Duration of task
     * @param deadline   Deadline of task
     * @param recurrence Recurrence of task
     * @param importance Importance of task
     * @param notes      Additional notes of task
     */
    public void addTask(String name, String startTime, String duration,
                        String deadline, String recurrence,
                        Importance importance, String notes, Boolean isFlexible)
            throws ClashInTaskException, TaskDuringSleepTimeException, InvalidTimeFormatException {
        maxNumber++;
        addTask(maxNumber, name, startTime, duration, deadline, recurrence, importance, notes, isFlexible);
    }

    private void decrementMaxNumber() {
        maxNumber--;
    }

    private void checkClash(Task taskToCompare) throws ClashInTaskException {
        for (Task task : tasks) {
            if (isTimeClash(taskToCompare, task)) {
                checkRecurrenceClash(taskToCompare, task);
            }
        }
    }

    private void checkRecurrenceClash(Task taskToCompare, Task task) throws ClashInTaskException {
        LocalDate dateToCompare = taskToCompare.getTimeInfo().getRecurrenceDates().get(0);
        for (LocalDate date : task.getTimeInfo().getRecurrenceDates()) {
            if (dateToCompare.equals(date) && taskToCompare.getNumber() != task.getNumber()
                    && !task.isFlexible() && !taskToCompare.isFlexible()) {
                throw new ClashInTaskException();
            }
        }
    }

    private boolean isTimeClash(Task taskToCompare, Task task) {
        LocalTime taskStartTime = taskToCompare.getTimeInfo().getStartTime();
        if (taskStartTime == null) {
            return false;
        }
        LocalTime taskEndTime = taskToCompare.getTimeInfo().getEndTime();

        LocalTime existingTaskStartTime = task.getTimeInfo().getStartTime();
        if (existingTaskStartTime == null) {
            return false;
        }
        LocalTime existingTaskEndTime = task.getTimeInfo().getEndTime();
        if (isIndividualTimeClash(taskStartTime, taskEndTime, existingTaskStartTime, existingTaskEndTime)) {
            return true;
        }
        return false;
    }


    private boolean isIndividualTimeClash(LocalTime taskStartTime, LocalTime taskEndTime,
                                          LocalTime existingTaskStartTime, LocalTime existingTaskEndTime) {
        boolean isTimeClash = !(taskEndTime.compareTo(existingTaskStartTime) <= 0
                || (taskStartTime.compareTo(existingTaskEndTime) >= 0
                && existingTaskEndTime.compareTo(existingTaskStartTime) > 0));
        boolean isMidnightClash = taskEndTime.compareTo(taskStartTime) < 0
                && existingTaskEndTime.compareTo(existingTaskStartTime) < 0;
        return isTimeClash || isMidnightClash;
    }

    private void updateMaxNumber(int number) {
        maxNumber++;
        if (this.maxNumber < number) {
            this.maxNumber = number;
        }
    }


    /**
     * Returns the task description of the task with the given number.
     *
     * @param taskNumber Task number.
     * @return Task description.
     * @throws TaskNotFoundException Exception thrown when the program is unable to find a task at the index
     *                               specified by the user
     */
    public String getTaskDescription(int taskNumber) throws TaskNotFoundException {
        Task task = getTaskFromNumber(taskNumber);
        return task.getDetailsAsString();
    }


    /**
     * Deletes the task at the specified position in the task list.
     *
     * @param taskNumber Number assigned to the task to be deleted.
     * @return Task that is deleted. Null if not found.
     * @throws TaskNotFoundException thrown when the program is unable to find a task at the index
     *                               specified by the user
     */
    public Task deleteTask(int taskNumber) throws TaskNotFoundException {
        Task task = getTaskFromNumber(taskNumber);
        tasks.remove(task);
        return task;
    }

    /**
     * Edits a task in the task list with the given number, if present.
     *
     * @param taskNumber Task number
     * @param name       Name of task
     * @param startTime  Start time of task
     * @param duration   Duration of task
     * @param deadline   Deadline of task
     * @param recurrence Recurrence of task
     * @param importance Importance of task
     * @param notes      Additional notes of task
     * @throws TaskNotFoundException thrown when the program is unable to find a task at the index
     *                               specified by the user
     */
    public void editTask(int taskNumber, String name, String startTime, String duration,
                         String deadline, String recurrence, Importance importance,
                         String notes)
            throws TaskNotFoundException, ClashInTaskException, TaskDuringSleepTimeException {
        Task task = getTaskFromNumber(taskNumber);
        Task possibleEditedTask = createTask(taskNumber, name, startTime,
                duration, deadline, recurrence, importance, notes, task.isFlexible());
        checkClash(possibleEditedTask);
        task.edit(name, startTime, duration, deadline, recurrence, importance, notes);

    }

    /**
     * Marks specified task as done.
     *
     * @param taskNumber Task number.
     * @return Task marked as done.
     * @throws TaskNotFoundException thrown when the program is unable to find a task at the index
     *                               specified by the user
     */
    public Task markTaskAsDone(int taskNumber) throws TaskNotFoundException {
        Task task = getTaskFromNumber(taskNumber);
        task.setDone();
        return task;
    }

    /**
     * Gets a task based on the number assigned to it.
     *
     * @param taskNumber number assigned to the task.
     * @return The task with the given number. Null if not found.
     * @throws TaskNotFoundException thrown when the program is unable to find a task at the index
     *                               specified by the user
     */
    public Task getTaskFromNumber(int taskNumber) throws TaskNotFoundException {
        for (Task t : tasks) {
            if (t.getNumber() == taskNumber) {
                return t;
            }
        }
        throw new TaskNotFoundException(taskNumber);
    }

    /**
     * Returns a filtered task list.
     *
     * @param taskFilter The filter that decides which tasks are printed
     * @return Filtered task list
     */
    public TaskList getFilteredList(TaskFilter taskFilter) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (taskFilter.isTaskIncluded(task)) {
                if (taskFilter instanceof ForecastFilter) {
                    assert taskFilter instanceof ForecastFilter;
                    Task filteredTask = ((ForecastFilter) taskFilter).removeExcludedDates(task);
                    filteredTasks.add(filteredTask);
                } else {
                    filteredTasks.add(task);

                }
            }
        }
        return new TaskList(filteredTasks);
    }


    public ArrayList<Task> makeDeepCopyTasks(ArrayList<Task> oldTasks) {
        ArrayList<Task> tasksCopy = new ArrayList<Task>();
        for (Task task : oldTasks) {
            tasksCopy.add(task.getClone());
        }
        return tasksCopy;
    }

    /**
     * Gets the max index.
     *
     * @return Max index
     */
    public int getMaxNumber() {
        return maxNumber;
    }

    /**
     * Sets the max index.
     *
     * @param maxIndex Max index
     */
    public void setMaxNumber(int maxIndex) {
        this.maxNumber = maxIndex;
    }

    /**
     * Determines if two objects have the same attributes.
     *
     * @param o object
     * @return true if the two objects have the same attributes
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskList)) {
            return false;
        }
        TaskList taskList = (TaskList) o;

        return maxNumber == taskList.maxNumber && getTasks().equals(taskList.getTasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTasks(), maxNumber);
    }
}
