import java.util.Arrays;


public class Parser {
    private String commandName;
    private String[] args;

    public Parser() {
        commandName = null;
        args = new String[0]; // Initialize the args array to an empty string array.
    }

    public boolean parse(String input) {
        // If the user enters an input separated by " " we use "\\s+", if it is separated by "," we use "," and so on
        String[] parts = input.trim().split("\\s+"); // removes any leading or trailing spaces from the input string and splits them into substrings

        if (parts.length == 0) { // checks whether the parts array is empty
            return false;
        } else {
            commandName = parts[0]; // assigns the first element of the parts array to the commandName variable
            args = Arrays.copyOfRange(parts, 1, parts.length); // copy the elements of the parts array from index 1 to the end of the array into the class-level args array
            return true;
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}