package athena;


import athena.exceptions.ClashInTaskException;
import athena.exceptions.InvalidTimeFormatException;
import athena.exceptions.StorageCorruptedException;
import athena.exceptions.StorageException;
import athena.exceptions.StorageLoadFailException;
import athena.exceptions.TaskDuringSleepTimeException;
import athena.exceptions.TaskNotFoundException;
import athena.task.Task;
import athena.task.Time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Converts TaskLists to .csv files and back
 */

public class Storage {
    private String filePath;
    private TaskList tasks;

    private int size;

    /**
     * Initialises Storage object.
     *
     * @param filepath Location of the save file
     */
    public Storage(String filepath) {
        this.filePath = filepath;
    }

    private String replaceCommas(String info) {
        return info.replace(",", "]c}");
    }

    /**
     * Takes a TaskList and converts it to a .csv file.
     *
     * @param tasks tasks to be saved as strings
     */
    public void saveTaskListData(TaskList tasks) {
        this.tasks = tasks;
        String taskString = null;
        try {
            FileWriter csvWriter = new FileWriter(filePath);
            for (Task task : tasks.getTasks()) {
                Time timeInfo = task.getTimeInfo();
                taskString = replaceCommas(task.getName()) + ","
                        + replaceCommas(timeInfo.getStartTimeString()) + ","
                        + replaceCommas(timeInfo.getDurationString()) + ","
                        + replaceCommas(timeInfo.getDeadline()) + ","
                        + replaceCommas(timeInfo.getRecurrence()) + "," + task.getImportance() + ","
                        + replaceCommas(task.getNotes()) + "," + task.getNumber();
                if (task.isDone()) {
                    taskString = taskString + "," + "true";
                } else {
                    taskString = taskString + "," + "false";
                }
                if (task.isFlexible()) {
                    taskString = taskString + "," + "true";
                } else {
                    taskString = taskString + "," + "false";
                }
                csvWriter.append(taskString + "\n");
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves Tasklist from .csv file
     *
     * @return TaskList object equivalent of save file
     */

    //TODO: add compatibility for more task attributes
    public TaskList loadTaskListData() throws StorageException {
        File csvFile = new File(filePath);
        TaskList loadedTaskList = new TaskList();
        if (csvFile.isFile()) {
            String row;
            String[] data = null;
            BufferedReader csvReader = null;
            try {
                csvReader = new BufferedReader(new FileReader(filePath));
                while ((row = csvReader.readLine()) != null) {
                    data = row.split(",");
                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].replaceAll("]c}", ",");
                    }
                    loadedTaskList.addTask(Integer.parseInt(data[7]), data[0], data[1], data[2], data[3], data[4],
                            Importance.valueOf(data[5].toUpperCase()), data[6], Boolean.parseBoolean(data[9]));
                    if (data[8].equals("true")) {
                        Task currTask = loadedTaskList.markTaskAsDone(Integer.parseInt(data[7]));
                    }
                }
                csvReader.close();
            } catch (IOException e) {
                throw new StorageLoadFailException();
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new StorageCorruptedException(data);
            } catch (ClashInTaskException e) {
                throw new StorageCorruptedException(data);
            } catch (TaskNotFoundException e) {
                throw new StorageLoadFailException();
            } catch (TaskDuringSleepTimeException e) {
                throw new StorageCorruptedException(data);
            } catch (InvalidTimeFormatException e) {
                throw new StorageCorruptedException(data);
            }
        }
        return loadedTaskList;
    }
}
