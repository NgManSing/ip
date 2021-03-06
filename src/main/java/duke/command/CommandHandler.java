package duke.command;

import duke.input.InputDataHandler;
import duke.input.InputType;
import duke.record.Record;
import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Todo;

import java.util.Scanner;

/**
 * Represents a {@code CommandHandler} object. It receives and handles command provided by a user via CLI.
 */
public class CommandHandler {
    private final Record record;
    private final Scanner scan = new Scanner(System.in);

    /**
     * Constructor of CommandHandler<br>
     * Initializes the {@code CommandHandler} object by the given {@code Record} object.
     *
     * @param record A {@code Record} object that stores user's tasks
     */
    public CommandHandler(Record record) {
        this.record = record;
    }

    /**
     * Receiving a user input string, process it and perform the related command accordingly. It returns {@code ture}
     * normally so that the program continues to process user inputs unless the command "bye" is inputted.
     * (i.e. {@code false} is returned when the user wants to exit the program) Also, If the command is invalid,
     * exception {@code DukeException} will be thrown.
     *
     * @return boolean value of whether to continue processing user command
     * @throws DukeException if the command inputted is not valid
     */
    public boolean handleCommand() throws DukeException {
        boolean isLoop = true;
        InputDataHandler userInput = getUserInput();
        switch (userInput.getFirstArgument()) {
        case "todo":
            record.addRecord(userInput.getOtherArguments(), Todo.TASK_TYPE);
            break;
        case "deadline":
            record.addRecord(userInput.getOtherArguments(), Deadline.TASK_TYPE);
            break;
        case "event":
            record.addRecord(userInput.getOtherArguments(), Event.TASK_TYPE);
            break;
        case "list":
            showList(userInput.getOtherArguments());
            break;
        case "done":
            processCommand(userInput.getOtherArguments(), CommandType.done);
            break;
        case "delete":
            processCommand(userInput.getOtherArguments(), CommandType.delete);
            break;
        case "find":
            findRecords(userInput.getOtherArguments());
            break;
        case "search":
            searchDate(userInput.getOtherArguments());
            break;
        case "bye":
            isLoop = isEndProgram(userInput.getOtherArguments());
            break;
        default:
            throw new DukeException();
        }
        return isLoop;
    }


    private void findRecords(String[] arguments) {
        if (arguments.length != 1) {
            System.out.println("Command \"find\" requires 1 argument as keyword. Please try again!");
            return;
        }
        record.findRecords(arguments[0]);
    }

    private void searchDate(String[] arguments) {
        if (arguments.length != 1) {
            System.out.printf("Command \"%s\" requires a date argument. Please try again!\n", "search");
            return;
        }
        record.searchDate(arguments[0]);
    }

    private void processCommand(String[] arguments, CommandType commandType) {
        if (arguments.length != 1) {
            System.out.printf("Command \"%s\" requires an integer argument. Please try again!\n", commandType);
            return;
        }
        int targetRecordIndex = -1;
        boolean isArgumentInteger = true;
        try {
            targetRecordIndex = Integer.parseInt(arguments[0]) - 1;
        } catch (NumberFormatException e) {
            isArgumentInteger = false;
        }

        if (isArgumentInteger) {
            switch (commandType) {
            case delete:
                record.deleteRecord(targetRecordIndex);
                break;
            case done:
                record.markAsDone(targetRecordIndex);
                break;
            default:
                throw new IllegalArgumentException("Invalid commandType! Program terminated.");
            }
        } else {
            System.out.printf("Command \"%s\" only requires an integer argument. Please try again!\n", commandType);
        }
    }

    private void showList(String[] arguments) {
        if (arguments.length != 0) {
            System.out.println("Command \"list\" requires no argument. Please try again!");
            return;
        }
        record.showList();
    }

    private InputDataHandler getUserInput() {
        String userInput = "dummy";
        if (scan.hasNextLine()) {
            userInput = scan.nextLine();
        }
        System.out.println("Command entered: " + userInput);
        return new InputDataHandler(userInput, InputType.userInput);
    }

    private boolean isEndProgram(String[] arguments) {
        if (arguments.length != 0) {
            System.out.println("Command \"bye\" requires no argument. Please try again!");
            return true;
        }

        quitProgram();
        return false;
    }

    private void quitProgram() {
        System.out.println("Bye. Hope to see you again soon!");
    }
}
