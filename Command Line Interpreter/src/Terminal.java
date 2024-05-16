import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;


public class Terminal {
    private Parser parser;
    private String currentDirectory;
    private List<String> commandHistory;

    // Constructor for the Terminal class
    public Terminal() {
        parser = new Parser();
        currentDirectory = "/";
        commandHistory = new ArrayList<>(); // Initialize the command history list
    }

    // Method to start the terminal
    public void run() {
        System.out.println("Type 'exit' to quit.");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(currentDirectory + "> ");
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                break; // Exit the loop and close the program
            }

            if (parser.parse(input)) {
                String commandName = parser.getCommandName();
                String[] commandArgs = parser.getArgs();

                //Saves the result
                Object result = chooseCommandAction(commandName, commandArgs);

                if (result != null) {
                    System.out.println(result.toString());
                }
            } else {
                System.out.println("Invalid input. Please enter a command.");
            }
        }
        scanner.close();
    }

    // Method to execute the appropriate action based on the command
    public Object chooseCommandAction(String commandName, String[] args) {
        switch (commandName) {
            case "echo":
                echo(args);//1- "echo" command
                break;
            case "pwd":
                return pwd(); //2- "pwd" command
            case "cd":
                return cd(args); //3- "cd" command
            case "ls":
                return ls(); //4- "ls" command
            case "ls-r":
                return lsReverse(); //5- "ls-r" command
            case "mkdir":
                return mkdir(args); //6- "mkdir" command
            case "rmdir":
                return rmdir(args); //7- "rmdir" command
            case "cat":
                return cat(args); //8- "cat" command
            case "touch":
                return touch(args); //9- "touch" command
            case "cp":
                return cp(args); //10- "cp" command
            case "rm":
                return rm(args); //11- "rm" command
            case "history":
                history(); //12- "history" command
                break;
            default:
                return "Command not found: " + commandName;
        }
        return null;
    }

    // Method to implement the "echo" command
    private void echo(String[] args) {
        if (args.length > 0) {
            String message = String.join(" ", args);
            System.out.println(message);
        } else {
            System.out.println("Usage: echo [message]");
        }
    }

    // Method to implement the "pwd" command
    public String pwd() {
        return currentDirectory; // Return the current working directory
    }

    // Method to implement the "cd" command
    public String cd(String[] args) {
        if (args.length == 0) {
            currentDirectory = "/"; // If no arguments are provided, set the current directory to the root ("/")
        } else if (args.length == 1) {
            String newPath = args[0];

            if (newPath.equals("..")) {
                // If the provided argument is "..",Move to the parent directory
                File parentDirectory = new File(currentDirectory).getParentFile();
                if (parentDirectory != null) {
                    currentDirectory = parentDirectory.getAbsolutePath();
                }
            } else {
                // Normalize the provided path to handle both relative and absolute paths
                File newDir = new File(currentDirectory, newPath).getAbsoluteFile();
                if (newDir.exists() && newDir.isDirectory()) {
                    currentDirectory = newDir.getAbsolutePath();
                } else {
                    System.out.println("Directory not found: " + newPath);
                }
            }
        }
        return currentDirectory;
    }

    // Method to implement the "ls" command
    public String ls() {
        File currentDir = new File(currentDirectory);
        File[] files = currentDir.listFiles();

        if (files != null) {
            Arrays.sort(files); // Sort the files alphabetically

            String listing = "";
            for (File file : files) {
                listing += file.getName() + "\n"; // Build a list of file names
            }
            return listing;
        } else {
            return "The directory is empty.";
        }
    }

    // Method to implement the "ls-r" command
    public String lsReverse() {
        File currentDir = new File(currentDirectory);
        File[] files = currentDir.listFiles();

        if (files != null) {
            // Sort the files in reverse order
            Arrays.sort(files, Collections.reverseOrder());

            String listing = "";
            for (File file : files)// Iterate through the array of files and directories in the current directory.
            {
                listing += file.getName() + "\n";
            }
            return listing;
        } else {
            return "The directory is empty.";
        }
    }

    // Method to implement the "cat" command
    public String cat(String[] args) {
        if (args.length == 1) {
            return readFile(args[0]);
        } else if (args.length == 2) {
            String content1 = readFile(args[0]);
            String content2 = readFile(args[1]);

            if (content1 != null && content2 != null) {
                return content1 + content2; // Concatenate the content of two files
            } else {
                return "Failed to read file(s).";
            }
        } else {
            return "Wrong command structure";
        }
    }

    // Method to read the contents of a file
    private String readFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists())  // Check if the file exists .
            {
                Scanner scanner = new Scanner(file);
                String content = "";
                while (scanner.hasNextLine()) {
                    content += scanner.nextLine() + "\n"; // Read and concatenate file lines
                }
                scanner.close();
                return content;
            } else {
                return "File not found: " + filePath;
            }
        } catch (IOException e) {
            return "Failed to read the file: " + e.getMessage();
        }
    }

    // Method to implement the "mkdir" command
    public String mkdir(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                File directory = new File(arg);

                if (directory.isAbsolute()) {
                    // If the argument is an absolute path, create the directory in the given path
                    if (directory.mkdirs()) {
                        System.out.println("Directory created: " + directory.getAbsolutePath());
                    } else {
                        System.out.println("Failed to create directory: " + directory.getAbsolutePath());
                    }
                } else {
                    // If the argument is a directory name, create it in the current directory
                    directory = new File(currentDirectory, arg);
                    if (directory.mkdir()) {
                        System.out.println("Directory created: " + directory.getAbsolutePath());
                    } else {
                        System.out.println("Failed to create directory: " + directory.getAbsolutePath());
                    }
                }
            }
            return "Directories created.";
        } else {
            return "Wrong command structure";
        }
    }

    // Method to implement the "rmdir" command
    public String rmdir(String[] args) {
        if (args.length != 1) {
            return "Wrong command structure";
        }

        String directoryName = args[0];

        if (directoryName.equals("*")) {
            removeEmptyDirectories(); // Handle removing all empty directories
        } else {
            String result = removeDirectory(directoryName); // Handle removing the specified directory
            if (result != null) {
                return result;
            }
        }
        return "Invalid input. Please provide valid arguments.";
    }

    // Method to remove a directory
    private String removeDirectory(String directoryName) {
        File directory = new File(directoryName);

        if (!directory.exists()) // Check if the directory exists.
        {
            return "Directory not found: " + directory.getAbsolutePath();
        }

        if (!directory.isDirectory())// Check if the specified path is a directory.
        {
            return "Not a directory: " + directory.getAbsolutePath();
        }

        if (directory.list().length > 0) // Check if the directory is not empty by verifying the number of files in it.
        {
            return "Directory is not empty: " + directory.getAbsolutePath();
        }

        if (directory.delete())// If all conditions are met, attempt to delete the directory.
        {
            return "Directory removed: " + directory.getAbsolutePath();
        } else
        {
            return "Failed to remove directory: " + directory.getAbsolutePath();
        }
    }

    // Method to remove empty directories within the current directory
    private void removeEmptyDirectories() {
        File currentDir = new File(currentDirectory);

        if (currentDir.isDirectory()) {
            File[] subDirs = currentDir.listFiles(File::isDirectory);
            if (subDirs != null)// Check if there are subdirectories present in the current directory.
            {
                for (File subDir : subDirs) {
                    if (subDir.list().length == 0) {
                        subDir.delete();
                    }
                }
            }
        }
    }

    // Method to implement the "touch" command
    public String touch(String[] args) {
        if (args.length == 1) {
            File file = new File(args[0]);
            try {
                if (file.createNewFile()) {
                    return "File created: " + file.getAbsolutePath();
                } else {
                    return "File already exists: " + file.getAbsolutePath();
                }
            } catch (Throwable t) {
                return "Failed to create the file: " + t.getMessage();
            }
        } else {
            return "Wrong command structure";
        }
    }

    // Method to implement the "cp" command
    public String cp(String[] args) {
        if (args.length == 2) {
            File sourceFile = new File(args[0]);
            File destinationFile = new File(args[1]);
            try {
                Files.copy(sourceFile.toPath(), destinationFile.toPath());// Copy the source file to the destination file
                return "File copied from " + args[0] + " to " + args[1];
            } catch (IOException e) {
                return "Error copying file: " + e.getMessage();
            }
        } else {
            return "Wrong command structure";
        }
    }

    // Method to implement the "rm" command
    public String rm(String[] args) {
        File file = new File(args[0]);

        if (file.exists()) {
            if (file.delete()) {
                return "File removed: " + file.getAbsolutePath();
            } else {
                return "Failed to remove file: " + file.getAbsolutePath();
            }
        } else {
            return "File does not exist: " + args[0];
        }
    }

    // Method to display command history
    public void history() {
        // Iterate through the command history list to display previously executed commands.
        for (int i = 0; i < commandHistory.size(); i++) {
            // Print the history entry number (1-based) and the corresponding command.
            System.out.println((i + 1) + " " + commandHistory.get(i));
        }
    }

    // Entry point of the program
    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        terminal.run();
    }
}
//Abdelrahman Mahdy 20216062
//Nour Adel 20216111

