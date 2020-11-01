package athena.ui;

import athena.Importance;
import athena.task.Task;
import athena.timetable.Timetable;
import org.fusesource.jansi.AnsiConsole;

/**
 * Represents all of the text that the user sees on the command line.
 */
public class AthenaUi implements Ui {
    private ColorText colorText;

    public AthenaUi() {
        AnsiConsole.systemInstall();
        colorText = new ColorText();
    }

    public void printAthenaLogo() {
        System.out.println(colorText.toPurple("     ___   .__________.  __    __   _______  .__   __.      ___      \n"
                + "    /   \\  |          | |  |  |  | |   ____| |  \\ |  |     /   \\     \n"
                + "   /  ^  \\ `---|  |---` |  |__|  | |  |__    |   \\|  |    /  ^  \\    \n"
                + "  /  /_\\  \\    |  |     |   __   | |   __|   |  . `  |   /  /_\\  \\   \n"
                + " /  _____  \\   |  |     |  |  |  | |  |____  |  |\\   |  /  _____  \\  \n"
                + "/__/     \\__\\  |__|     |__|  |__| |_______| |__| \\__| /__/     \\__\\ \n"));
    }

    /**
     * Prints the first message that the user sees upon launching the application.
     */
    public void printWelcomeMessage() {
        System.out.println(colorText.toPurple("Hello! I'm the Goddess of Wisdom and War, the mighty ATHENA!\n" + "...\n"
                + "Okay okay I'm not a Goddess but I am your Automated Timetable Helper Encourager n' Assistant!\n"
                + "What can I do? Are you challenging me to a duel?\n" + "...\n"
                + "Oh you mean in terms of tasks? Just type \"help\" to witness my mighty repertoire!\n"
                + "So, what would you like to do today?\n"));
    }

    /**
     * Prints out a message verifying which task the user has added to their list.
     *
     * @param taskName       The name of the task
     * @param taskStartTime  When the task is scheduled to start
     * @param taskDuration   How long the task will last for
     * @param taskDeadline   When the task is due
     * @param taskRecurrence When will the task repeat
     * @param taskImportance What is the importance of the task
     * @param taskNotes      Any additional notes the user has added to the task
     */
    public void printTaskAdded(String taskName, String taskStartTime, String taskDuration, String taskDeadline,
                               String taskRecurrence, String taskImportance, String taskNotes) {
        System.out.print("\nYou've successfully added " + colorText.toBlue(taskName) + " to your list!\n"
                + "It will start at " + colorText.toBlue(taskStartTime));

        if (taskDeadline.toLowerCase().equals("no deadline")) {
            System.out.println(" and has no deadline.\n");
        } else {
            System.out.println(" and finish on " + colorText.toBlue(taskDeadline) + ".\n");
        }

        System.out.println("You should spend a total of " + colorText.toBlue(taskDuration) + " hour(s) on it.");

        if (taskRecurrence.toLowerCase().equals("today")) {
            System.out.print("It is set to happen " + colorText.toBlue(taskRecurrence));
        } else if (taskRecurrence.contains("-")) {
            System.out.print("It is set to happen on " + colorText.toBlue(taskRecurrence));
        } else {
            System.out.print("It is set to happen every " + colorText.toBlue(taskRecurrence));
        }

        System.out.println(" and has an importance of " + colorText.toBlue(taskImportance) + ".\n"
                + "Additionally, you've also added these notes!\n" + colorText.toBlue(taskNotes) + ".\n"
                + "Looks like another mission to complete! Let's do it!\n");
    }

    /**
     * Prints out a message verifying that the task the user specified has been marked as done.
     *
     * @param task The task that the user marked as done
     */
    public void printTaskDone(Task task) {
        System.out.println("\nA job well done! I've slayed the-- I mean, marked the task "
                + colorText.toBlue(task.getName()) + " as complete!\n");
    }

    /**
     * Prints out a message verifying that the task the user specified has been edited, and shows the user the
     * new task details.
     *
     * @param taskIndex      The index of the task on the task list
     * @param taskName       The name of the task
     * @param taskStartTime  When the task is scheduled to start
     * @param taskDuration   How long the task will last for
     * @param taskDeadline   When the task is due
     * @param taskRecurrence When will the task repeat
     * @param taskImportance What is the importance of the task
     * @param taskNotes      Any additional notes the user has added to the task
     */
    public void printTaskEdited(int taskIndex, String taskName, String taskStartTime, String taskDuration,
                                String taskDeadline, String taskRecurrence, Importance taskImportance,
                                String taskNotes) {
        System.out.println("\nYou've changed the details of task number "
                + colorText.toBlue(Integer.toString(taskIndex)) + ": " + colorText.toBlue(taskName) + "!\n"
                + "Here are the new details of your task!\n"
                + "Start Time: " + colorText.toBlue(taskStartTime) + "\n"
                + "Duration: " + colorText.toBlue(taskDuration) + "\n"
                + "Due Date: " + colorText.toBlue(taskDeadline) + "\n"
                + "Recurrence: " + colorText.toBlue(taskRecurrence) + "\n"
                + "Importance: " + colorText.toBlue(taskImportance.name()) + "\n"
                + "Additional Notes: " + colorText.toBlue(taskNotes) + "\n"
                + "The mistakes of the past have been vanquished!\n");
    }

    /**
     * Prints out a message verifying that the task the user specified has been deleted.
     *
     * @param task The task that the user has deleted.
     */
    public void printTaskDeleted(Task task, String taskRestore) {
        System.out.println("\nAre we pretending the task " + colorText.toBlue(task.getName())
                + " never existed? Very well...\n");
        System.out.println("We both know the human propensity to make mistakes, if you want it back just type:\n"
                + colorText.toBlue(taskRestore) + "\nYou can thank me later!\n");
    }

    /**
     * Prints out the task details that the user specified to view.
     *
     * @param taskDetails The task that the user wants to view.
     */
    public void printTaskDetails(String taskDetails) {
        System.out.println("\nHere are the details of your task: " + colorText.toBlue(taskDetails));
    }

    /**
     * Prints out a help menu of all available tasks that ATHENA is able to do.
     */
    public void printHelp() {
        System.out.println("\nNot sure of what I'm capable of doing? "
                + "Well here's a list just for you!\n\n"
                + "To " + colorText.toPurple("add a task")
                + " (parameters in square brackets are optional fields):\n"
                + colorText.toYellow("add n/NAME [t/TIME] [D/DEADLINE] [d/DURATION] [r/RECURRENCE] "
                + "[i/IMPORTANCE] [a/ADDITIONAL-NOTES]\n")
                + "e.g.  add n/Assignment1 t/1100 D/16-09-2020 d/2 r/Today i/high a/Refer to lecture notes\n\n"
                + "To " + colorText.toPurple("list your current tasks:\n")
                + " (parameters in square brackets are optional fields):\n"
                + colorText.toYellow("list [f/FORECAST] [i/IMPORTANCE]\n")
                + "e.g. list f/WEEK i/medium\n\n"
                + "To " + colorText.toPurple("mark a task as done:\n")
                + colorText.toYellow("done INDEX\n")
                + "e.g. done 2\n\n"
                + "To " + colorText.toPurple("edit a task")
                + " (parameters in square brackets are optional fields):\n"
                + colorText.toYellow("edit INDEX [n/NAME] [t/TIME] [D/DEADLINE] [d/DURATION] "
                + "[r/RECURRENCE] [i/IMPORTANCE] [a/ADDITIONAL-NOTES]\n")
                + "e.g.  edit 1 n/Assignment1 t/1100 D/16-09-2020 d/2 r/today i/high a/Refer to lecture notes\n\n"
                + "To " + colorText.toPurple("delete a task:\n")
                + colorText.toYellow("delete INDEX\n")
                + "e.g. delete 2\n\n"
                + "To " + colorText.toPurple("view a task:\n")
                + colorText.toYellow("view INDEX\n")
                + "e.g. view 2\n\n"
                + "To " + colorText.toPurple("say farewell to me, ATHENA:\n")
                + colorText.toYellow("exit\n")
                + "But why would you want to leave me anyways? Hmph.\n");
    }

    /**
     * Prints a message when there is an error with a task in the storage file.
     */
    public void printInvalidTask() {
        System.out.println(colorText.toRed("\nOne of the tasks in the archives seems to be cursed, "
                + "I have disposed of it!\n"));
    }

    /**
     * Print a message when user enters time in invalid format.
     */
    public void printInvalidTimeFormatException() {
        System.out.println(colorText.toRed("\nPlease enter your time in the format [HHMM]\n"));
    }

    /**
     * Prints a message stating the task is not found.
     *
     * @param taskNumber Task number given by the user
     */
    public void printTaskNotFound(int taskNumber) {
        System.out.println(colorText.toRed("\nThe task with the label ")
                + colorText.toRed(Integer.toString(taskNumber)) + colorText.toRed(" cannot be found.\n"));
    }

    /**
     * Prints a message telling user they did not specify either the name or start time of the task.
     */
    public void printAddMissingRequiredParametersException() {
        System.out.println(colorText.toRed("\nYou haven't specified the name of the task! "
                + "How can we go on a conquest if we don't know what to slay?\n")
                + "In case you've forgotten, this is how you add a task:\n"
                + colorText.toYellow("add n/NAME [t/TIME] [d/DURATION] [D/DEADLINE] [r/RECURRENCE] "
                + "[i/IMPORTANCE] [a/ADDITIONAL-NOTES]\n"));
    }

    public void printAddDateWrongFormatException() {
        System.out.println(colorText.toRed("\nYou didn't format something in the right way!\n")
                + "In case you've forgotten, this is how you add a task:\n"
                + colorText.toYellow("add n/NAME [t/TIME] [d/DURATION] [D/DEADLINE] [r/RECURRENCE] "
                + "[i/IMPORTANCE] [a/ADDITIONAL-NOTES]\n"));
    }

    /**
     * Prints a message telling user they did not provide a valid index for the delete command.
     */
    public void printDeleteInvalidIndexException() {
        System.out.println(colorText.toRed("\nHmm...not sure what you're trying to delete, "
                + "but there is no task at that index.\n")
                + "In case you've forgotten your list of tasks and their indexes, type:\n"
                + colorText.toYellow("list [f/FORECAST] [i/IMPORTANCE]\n")
                + "This is how you delete a task, provided there is a task at the index you specify:\n"
                + colorText.toYellow("delete INDEX\n"));
    }

    /**
     * Prints a message telling user they did not provide an index for the delete command.
     */
    public void printDeleteNoIndexException() {
        System.out.println(colorText.toRed("\nSo you're trying to delete a task...but which one exactly? "
                + "You haven't specified.\n")
                + "In case you've forgotten your list of tasks and their indexes, type:\n"
                + colorText.toYellow("list [f/FORECAST] [i/IMPORTANCE]\n")
                + "Also just as a reminder, this is how you delete a task:\n"
                + colorText.toYellow("delete INDEX\n"));
    }

    /**
     * Prints a message telling user they did not provide a valid index for the done command.
     */
    public void printDoneInvalidIndexException() {
        System.out.println(colorText.toRed("\nI'm not sure if you're trying to smoke me or smoke yourself, "
                + "but there is no task at that index.\n")
                + "To see your list of tasks and their indexes, type this below:\n"
                + colorText.toYellow("list [f/FORECAST] [i/IMPORTANCE]\n")
                + "This is how you mark a task as done, provided that is a task at the index you specify:\n"
                + colorText.toYellow("done INDEX\n"));
    }

    /**
     * Prints a message telling user they did not provide an index for the done command.
     */
    public void printDoneNoIndexException() {
        System.out.println(colorText.toRed("\nI know you're really eager, but you need to specify "
                + "the index of a task to mark that particular task as done!\n")
                + "To see your list of tasks and their indexes, type:\n"
                + colorText.toYellow("list [f/FORECAST] [i/IMPORTANCE]\n")
                + "In the rare chance that you've forgotten how to mark a task as done, here is my tip for you:\n"
                + colorText.toYellow("done INDEX\n"));
    }

    /**
     * Prints a message telling user they did not provide a valid index for the view command.
     */
    public void printViewInvalidIndexException() {
        System.out.println(colorText.toRed("\nErrrrr I'm not sure what you wanted to do but...."
                + "there is no task at that index. You should enter an index number that contains a task.\n")
                + "To see your list of tasks and their indexes, type:\n"
                + colorText.toYellow("list [f/FORECAST] [i/IMPORTANCE]\n")
                + "Also, in case you forgot how to view the details of a task, type:\n"
                + colorText.toYellow("view INDEX\n"));
    }

    /**
     * Prints a message telling user they did not provide an index for the view command.
     */
    public void printViewNoIndexException() {
        System.out.println(colorText.toRed("\nHold up, hold up, it seems like you haven't told me which "
                + "task you want to view!\n")
                + "If you can't remember which task has which index number, type:\n"
                + colorText.toYellow("list [f/FORECAST] [i/IMPORTANCE]\n")
                + "And if you forgot how to view a task, here you go:\n"
                + colorText.toYellow("view INDEX\n"));
    }

    /**
     * Prints a message telling user they did not provide an index for the edit command.
     */
    public void printEditNoIndexException() {
        System.out.println(colorText.toRed("\nYour edit command instructions aren't quite right...\n")
                + "Remember to edit at least one parameter!\n"
                + "I know the command is quite long, so here's a reminder for you:\n"
                + colorText.toYellow("edit INDEX [n/NAME] [t/TIME] [d/DURATION] [D/DEADLINE] "
                + "[r/RECURRENCE] [i/IMPORTANCE] [a/ADDITIONAL-NOTES]\n"));

    }

    /**
     * Prints a message telling user they did not input a command that is recognized by the program.
     */
    public void printInvalidCommandException() {
        System.out.println(colorText.toRed("\nI know I'm really smart but even I don't know what "
                + "you're requesting. Maybe if you type ") + colorText.toYellow("help")
                + colorText.toRed(", you can see what the valid commands are.\n"));
    }

    /**
     * Prints a message telling user their task list is empty.
     */
    public void printEmptyTaskListException() {
        System.out.println(colorText.toRed("\nYou don't have any tasks in your list! "
                + "You should probably add some in if you want to be productive.\n"));
    }

    /**
     * Prints a message telling user there's a clash with this task.
     */
    public void printClashInTaskException() {
        System.out.println(colorText.toRed("\nThere's a clash in this task, please choose"
                + " a different time or date."));
    }

    /**
     * Prints a message telling user they've entered an invalid importance.
     */
    public void printInvalidImportanceException() {
        System.out.println(colorText.toRed("\nYou have entered an invalid importance. "
                + "Please choose from high, medium, or low."));
    }

    /**
     * Prints a message telling user they've entered an invalid forecast.
     */
    public void printInvalidForecastException() {
        System.out.println(colorText.toRed("\nYou have entered an invalid forecast. "
                + "Please choose from day, week, or all."));
    }

    /**
     * Prints a message when the user chooses to exit the program.
     */
    public void printExitMessage() {
        System.out.println(colorText.toPurple("\nGoing so soon? Well I'll be here the next time you need me. "
                + "Farewell!\n"));
    }


    /**
     * Prints the timetable.
     *
     * @param timetable Timetable to be printed
     */
    public void printTimetable(Timetable timetable) {
        System.out.println(timetable);
    }

    public void printStorageLoadFail() {
        System.out.println(colorText.toRed("\nStorage loading has failed.\n"));
    }

    public void printCorruptedLine(String corruptedLine) {
        System.out.println(colorText.toRed("\nThis task is invalid: " + corruptedLine + ".\n"
                + "\nPlease remove externally to continue.\n"));
    }

    public void printAllocationFailed() {
        System.out.println(colorText.toRed("\nAllocation Failed.\n"));
    }

    public void printSleepTimeNotAllowed() {
        System.out.println(colorText.toRed(
                "You are not allowed to add a task from 12am to 8am. It is time to sleep! \n"));
    }
}
