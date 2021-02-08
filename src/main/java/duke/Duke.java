package duke;

import duke.exception.DukeException;
import duke.command.UserInput;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class is to build a personal assistant chatbot called "Kaman"
 * (Customized from Duke)
 *
 * @author NgManSing
 */
public class Duke {
    private static final ArrayList<Task> records = new ArrayList<>();
    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        boolean isLoop = true;

        printWelcomeMsg();
        while (isLoop) {
            try {
                isLoop = receiveCommand();
            } catch (DukeException e) {
                promptUserInputInvalid();
            }
        }
    }

    private static boolean receiveCommand() throws DukeException {
        boolean isLoop = true;
        UserInput userInput = getUserInput();
        switch (userInput.getCommand()) {
        case "todo":
            addRecord(userInput.getArguments(), Todo.TASK_TYPE);
            break;
        case "deadline":
            addRecord(userInput.getArguments(), Deadline.TASK_TYPE);
            break;
        case "event":
            addRecord(userInput.getArguments(), Event.TASK_TYPE);
            break;
        case "list":
            showList(userInput.getArguments());
            break;
        case "done":
            executeCommandDone(userInput.getArguments());
            break;
        case "bye":
            isLoop = isEndProgram(userInput.getArguments());
            break;
        default:
            throw new DukeException();
        }
        return isLoop;
    }

    private static void promptUserInputInvalid() {
        System.out.println("I don't understand your input! Please try again!");
    }

    private static boolean isEndProgram(String[] arguments) {
        if (arguments.length != 0) {
            System.out.println("Command \"bye\" requires no argument. Please try again!");
            return true;
        }

        quitProgram();
        return false;
    }

    private static void executeCommandDone(String[] arguments) {
        if (arguments.length == 0) {
            System.out.println("Command \"done\" requires an integer argument. Please try again!");
            return;
        }
        int targetRecordIndex = -1;
        boolean isArgumentInteger = true;
        try {
            targetRecordIndex = Integer.parseInt(arguments[0]) - 1;
        } catch (NumberFormatException e) {
            isArgumentInteger = false;
        }

        if (arguments.length == 1 && isArgumentInteger) {
            markAsDone(targetRecordIndex);
        } else {
            System.out.println("Command \"done\" only requires an integer argument. Please try again!");
        }
    }

    private static UserInput getUserInput() {
        String userInput = "dummy";
        if (scan.hasNextLine()) {
            userInput = scan.nextLine();
        }
        System.out.println("Command entered: " + userInput);
        return new UserInput(userInput);
    }

    private static void printWelcomeMsg() {
        System.out.println("Hello! I am Kaman :)");
        System.out.println("What can I do for you?");
    }

    private static void markAsDone(int index) {
        if (index < 0 || index >= records.size()) {
            System.out.println("Invalid input! (Index cannot be out of bounds)");
            return;
        }
        records.get(index).setAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("\t" + records.get(index));
    }

    private static void addRecord(String[] detailFragments, String taskType) {
        String[] details;
        String taskName;
        String date;
        boolean isAdded = false;

        switch (taskType) {
        case Todo.TASK_TYPE:
            if (detailFragments.length > 0) {
                taskName = String.join(" ", detailFragments);
                records.add(new Todo(taskName));
                isAdded = true;
            } else {
                showInvalidEmptyDescription();
            }
            break;
        case Deadline.TASK_TYPE:
            details = getTaskNameAndDate(detailFragments, taskType);
            if (details != null) {
                taskName = details[0];
                date = details[1];
                records.add(new Deadline(taskName, date));
                isAdded = true;
            }
            break;
        case Event.TASK_TYPE:
            details = getTaskNameAndDate(detailFragments, taskType);
            if (details != null) {
                taskName = details[0];
                date = details[1];
                records.add(new Event(taskName, date));
                isAdded = true;
            }
            break;
        default:
            throw new IllegalArgumentException("A non-taskType is passed to addRecord.");
        }

        if (isAdded) {
            int numberOfRecords = records.size();
            System.out.println("Got it. I've added this task:");
            System.out.println("\t" + records.get(numberOfRecords - 1));
            System.out.printf("Now you have %d tasks in the list.\n", numberOfRecords);
        }
    }

    private static void showInvalidEmptyDescription() {
        System.out.println("The description of a task cannot be empty.");
    }

    private static String[] getTaskNameAndDate(String[] detailFragments, String taskType) {
        int keywordIndex = Arrays.asList(detailFragments).indexOf(taskType.equals(Deadline.TASK_TYPE) ? "/by" : "/at");
        if (keywordIndex < 0 || keywordIndex == detailFragments.length - 1) {
            System.out.println("Invalid argument! It may be resulted from:");
            System.out.println("1. No date/time provided");
            System.out.println("2. keywords not matching");
            return null;
        }
        if (keywordIndex == 0) {
            showInvalidEmptyDescription();
            return null;
        }
        StringBuilder taskName = new StringBuilder();
        StringBuilder dueDay = new StringBuilder();
        taskName.append(detailFragments[0]);
        for (int i = 1; i < keywordIndex; i++) {
            taskName.append(" ").append(detailFragments[i]);
        }
        dueDay.append(detailFragments[keywordIndex + 1]);
        for (int i = keywordIndex + 2; i < detailFragments.length; i++) {
            dueDay.append(" ").append(detailFragments[i]);
        }
        return new String[]{taskName.toString(), dueDay.toString()};
    }

    private static void showList(String[] arguments) {
        if (arguments.length != 0) {
            System.out.println("Command \"list\" requires no argument. Please try again!");
            return;
        }
        System.out.println("Here is your task List:");
        for (int i = 0; i < records.size(); i++) {
            System.out.println((i + 1) + ". " + records.get(i));
        }
    }

    private static void quitProgram() {
        System.out.println("Bye. Hope to see you again soon!");
    }
}