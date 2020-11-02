package athena;

import athena.exceptions.TaskNotFoundException;
import athena.task.Task;
import athena.task.Time;
import athena.task.taskfilter.FlexibleTimeFilter;
import athena.task.taskfilter.ForecastFilter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class TimeAllocator {
    TaskList flexibleTaskList;
    TaskList fixedTaskList;
    TaskList taskList;
    private static final int DATE_TIME_FORMAT = 5;


    /**
     * This is run automatically to generate a timetable.
     * option to auto assign at every input, reassignment at the end is possible
     * need to maintain the flexibility of the tasks to allow for multiple reruns
     *
     * @param tasks tasks in the current TaskList
     */
    public TimeAllocator(TaskList tasks) {
        this.flexibleTaskList = tasks.getFilteredList(new FlexibleTimeFilter(true));
        this.fixedTaskList = tasks.getFilteredList(new FlexibleTimeFilter(false));
        this.taskList = tasks;

    }

    /**
     * Populates a record of fixed tasks.
     * Finds vacant slots in the timetable
     * Calls the best arrangement for slot
     */
    public void runAllocate() {
        LocalDate currDay = LocalDate.now();
        ForecastFilter forecast = new ForecastFilter(Forecast.DAY);
        ArrayList<Task> undefinedTimeTasks = getSortedFlexibleTasks(this.flexibleTaskList);
        for (int day = 0; day < 31; day++) {
            ArrayList<Integer> dayLog = new ArrayList<Integer>(Collections.nCopies(24, -1));
            ArrayList<Task> predefinedTimeTasks = getSortedFixedTasks(getFixedDayTasks(currDay));
            for (Task currTask : predefinedTimeTasks) {
                int tag = currTask.getNumber();
                Time timeInfo = currTask.getTimeInfo();
                for (int i = 0; i < timeInfo.getDuration(); i++) {
                    dayLog.set(timeInfo.getStartTime().getHour() + i, tag);
                }
            }
            ArrayList<Task> carryOverTasks = new ArrayList<Task>();

            int start = 8;
            int sleep = 24;
            boolean done = false;
            while (!done) {
                int pos = start;
                pos = nextVacantSlotStart(dayLog, pos, sleep);
                int end = pos;
                end = nextVacantSlotEnd(dayLog, end, sleep);
                carryOverTasks = new ArrayList<Task>();
                ArrayList<Integer> bestLog = getBestLog(pos, end, undefinedTimeTasks, carryOverTasks);
                populateDayLog(pos, dayLog, bestLog);
                assignTime(bestLog, pos, currDay);
                undefinedTimeTasks = carryOverTasks;
                start = end;
                if (end == sleep) {
                    done = true;
                }
            }
            for (Task currTask : carryOverTasks) {
                try {
                    Time timeInfo = taskList.getTaskFromNumber(currTask.getNumber()).getTimeInfo();
                    timeInfo.setStartTime(null);
                } catch (TaskNotFoundException e) {
                    //do nothing
                }
            }
            currDay = currDay.plusDays(1);
        }
    }

    /**
     * Assigns the best time to the Tasks.
     *
     * @param bestLog list of taskNumbers in the index corresponding to the hour they are assigned to
     * @param pos     starting position of the log
     * @param currDay
     * @throws TaskNotFoundException if task is not in the taskList
     */
    private void assignTime(ArrayList<Integer> bestLog, int pos, LocalDate currDay) {
        int count = 0;
        ArrayList<Integer> assignedNumbers = new ArrayList<>();
        for (int taskNumber : bestLog) {
            if (!assignedNumbers.contains(taskNumber)) {
                try {
                    Time timeInfo = this.taskList.getTaskFromNumber(taskNumber)
                            .getTimeInfo();
                    timeInfo.setStartTime(LocalTime.of(pos + count, 0));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
                    timeInfo.resetRecurrence();
                    String test = currDay.format(formatter);
                    timeInfo.setRecurrence(test);
                    assignedNumbers.add(taskNumber);
                    this.flexibleTaskList.deleteTask(taskNumber);
                } catch (TaskNotFoundException e) {
                    //do nothing
                }
            }
            count++;
        }
    }

    /**
     * Creates the arrangement that reduces the breaks in the timetable.
     * Iterates through the undefinedTimeTasks
     * Modifies the excess tasks left over
     *
     * @param pos                staring position
     * @param end                ending position
     * @param undefinedTimeTasks Tasks with undefined times
     * @param carryOverTasks     store tasks that did not get assigned yet
     * @return
     */
    private ArrayList<Integer> getBestLog(int pos, int end, ArrayList<Task> undefinedTimeTasks,
                                          ArrayList<Task> carryOverTasks) {
        ArrayList<Integer> currentLog = new ArrayList<Integer>();
        ArrayList<Integer> bestLog = new ArrayList<Integer>();
        boolean hasUsableVacancy = true;
        while (hasUsableVacancy) {
            currentLog = getLog(undefinedTimeTasks, pos, end);
            hasUsableVacancy = currentLog.contains(-1);
            if ((bestLog.indexOf(-1) < currentLog.indexOf(-1)) | !hasUsableVacancy) {
                bestLog = currentLog;
            }
            if (!undefinedTimeTasks.isEmpty()) {
                carryOverTasks.add(undefinedTimeTasks.get(0));
                undefinedTimeTasks.remove(0);

            } else {
                break;
            }

        }
        for (int taskNumber : bestLog) {
            try {
                Task taskRemoved = taskList.getTaskFromNumber(taskNumber);
                undefinedTimeTasks.remove(taskRemoved);
                carryOverTasks.remove(taskRemoved);
            } catch (TaskNotFoundException e) {
                //do nothing
            }
        }
        carryOverTasks.addAll(undefinedTimeTasks);
        return bestLog;
    }

    /**
     * Gets a filled vacant slots given undefinedTimeTasks.
     *
     * @param undefinedTimeTasks Tasks to be slotted into the timetable
     * @param pos                starting position
     * @param end                ending position
     * @return ArrayList containing taskNumbers
     */
    private ArrayList<Integer> getLog(ArrayList<Task> undefinedTimeTasks, int pos, int end) {
        int space = end - pos;
        ArrayList<Integer> log = new ArrayList<Integer>(Collections.nCopies(space, -1));
        for (Task currTask : undefinedTimeTasks) {
            int span = currTask.getTimeInfo().getDuration();
            if (span <= space) {
                int taskNumber = currTask.getNumber();
                for (int i = 0; i < span; i++) {
                    int relativePos = end - space - pos;
                    log.set(i + relativePos, taskNumber);
                }
                space -= span;
            }
            if (space == 0) {
                break;
            }
        }
        return log;
    }


    /**
     * Finds end of vacant time slot.
     *
     * @param dayLog log of the day's tasks
     * @param end    ending position
     * @param sleep  sleep time
     * @return valid end value
     */
    private int nextVacantSlotEnd(ArrayList<Integer> dayLog, int end, int sleep) {
        for (; end < sleep; end++) {
            if (dayLog.get(end) != -1) {
                break;
            }
        }
        return end;
    }

    /**
     * Finds start of vacant time slot.
     *
     * @param dayLog log of the day's tasks
     * @param pos    starting position
     * @param sleep  sleep time
     * @return valid pos value
     */
    private int nextVacantSlotStart(ArrayList<Integer> dayLog, int pos, int sleep) {
        for (; pos < sleep; pos++) {
            if (dayLog.get(pos) == -1) {
                break;
            }
        }
        return pos;
    }

    private ArrayList<Task> getSortedFixedTasks(TaskList taskList) {
        TaskList fixedDayTasks = taskList;
        ArrayList<Task> sortedTimeTasks = fixedDayTasks.getTasks();
        sortedTimeTasks.sort(new TaskTimeComparator());
        sortedTimeTasks.sort(new TaskImportanceComparator());
        return sortedTimeTasks;
    }

    private ArrayList<Task> getSortedFlexibleTasks(TaskList taskList) {
        TaskList flexibleDayTasks = taskList;
        ArrayList<Task> sortedTimeTasks = flexibleDayTasks.getTasks();
        sortedTimeTasks.sort(new TaskTimeComparator());
        sortedTimeTasks.sort(new TaskImportanceComparator());
        return sortedTimeTasks;
    }


    private void populateDayLog(int pos, ArrayList<Integer> dayLog, ArrayList<Integer> bestLog) {
        int count = 0;
        for (int taskNumber : bestLog) {
            dayLog.set(count + pos, taskNumber);
            count++;
        }
    }


    private TaskList getFixedDayTasks(LocalDate date) {
        ForecastFilter forecast = new ForecastFilter(date);
        TaskList fixedDayTask = this.fixedTaskList.getFilteredList(forecast);
        return fixedDayTask;
    }

    private TaskList getFlexibleDayTasks(LocalDate date) {
        ForecastFilter forecast = new ForecastFilter(date);
        TaskList flexibleDayTask = this.flexibleTaskList.getFilteredList(forecast);
        return flexibleDayTask;
    }

    // Psuedocode for allocation runs might refactor into a command class
    //    public void runAllocate() {
    //        Collections.sort(fixedTask, earlier());
    //        Collections.sort(flexibleTask,priority());
    //        //priority sorts according to importance(high to low) then duration(high to low)
    //        //might need toposort
    //
    //        for (int i = 0; i < fixedTask.size() - 1; i++) {
    //            currentTime = fixedTask.get(i).getStartTime() + fixedTask.get(i).getDuration();
    //            nextTime = fixedTask.get(i + 1).getStartTime();
    //            duration = nextTime-currentTime;
    //
    //            // select task with best fit into the time
    //            // select important task then long tasks then check for space
    //            // if there is no time left then continue
    //            // if not then choose another task that can go in
    //            // ?? not sure if want to go through the entire list to get 0 time wastage
    //            // failure cases will be unable to meet deadline
    //            // set time for flexible tasks
    //            // repeat until all flexible tasks are assigned
    //
    //
    //        }
    //    }


}
