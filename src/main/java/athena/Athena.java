package athena;

import athena.commands.Command;

public class Athena {
    private static Ui ui;

    public Athena() {
        ui = new Ui();
    }

    public static void main(String[] args) {
        Athena athena = new Athena();
        athena.runProgram();
        Storage storage = new Storage("save.csv", ui);
        TaskList taskList = new TaskList();
        taskList = storage.loadTaskListData();
        Parser parser = new Parser();
        Command command = parser.parse("add n/Assignment2 t/1100 d/16-09 D/2 r/Monday i/high a/Refer to lecture notes");
        command.execute(taskList, athena.ui);
        for (int i = 0; i < 10; i++) {
            command = parser.parse("add n/Assignment2 t/1100 d/16-09 D/2 r/Monday i/high a/Refer to lecture notes");
            command.execute(taskList, athena.ui);
        }
        command = parser.parse("list");
        command.execute(taskList, athena.ui);
        command = parser.parse("help");
        command.execute(taskList, athena.ui);
        storage.saveTaskListData(taskList);
        command = parser.parse("delete 2");
        command.execute(taskList, athena.ui);
        storage.saveTaskListData(taskList);

    }

    public void runProgram() {


    }

}
// TODO: pass task as argument
// ui.printTaskAdded();
// ui.printTaskDeleted();
// ui.printTaskDone();
// ui.printTaskEdited();