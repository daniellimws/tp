package athena;

import athena.logic.LogicManager;
import athena.exceptions.CommandException;
import athena.ui.AthenaUi;
import athena.exceptions.StorageCorruptedException;
import athena.exceptions.StorageException;
import athena.exceptions.StorageLoadFailException;
import java.io.FileNotFoundException;
import java.io.IOException;
import athena.exceptions.InternalException;
import java.util.Scanner;

/**
 * The main class of the ATHENA program.
 */
public class Athena {
    private AthenaUi athenaUi;
    private Storage storage;
    private TaskList taskList;
    private TimeAllocator allocator;
    private LogicManager logicManager;

    /**
     * Creates an ATHENA object.
     */
    public Athena() {
        athenaUi = new AthenaUi();
        logicManager = new LogicManager();
        storage = new Storage("data.csv");
    }

    public static void main(String[] args) {
        Athena athena = new Athena();
        athena.runProgram();
    }

    /**
     * Runs the main code of the ATHENA program.
     */
    public void runProgram() {
        String inputString;

        athenaUi.printAthenaLogo();
        athenaUi.printWelcomeMessage();

        boolean isExit = false;
        try {
            taskList = storage.loadTaskListData();
        } catch (StorageException e) {
            e.printErrorMessage();
            isExit = true;
        }
        Scanner input = new Scanner(System.in);

        while (!isExit) {
            allocator = new TimeAllocator(taskList);
            allocator.runAllocate();
            inputString = input.nextLine();
            isExit = false; //logicManager.execute(inputString);
        }
    }
}
