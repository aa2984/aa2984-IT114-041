package M3;

/*
Challenge 2: Simple Slash Command Handler
-----------------------------------------
- Accept user input as slash commands
  - "/greet <name>" → Prints "Hello, <name>!"
  - "/roll <num>d<sides>" → Roll <num> dice with <sides> and returns a single outcome as "Rolled <num>d<sides> and got <result>!"
  - "/echo <message>" → Prints the message back
  - "/quit" → Exits the program
- Commands are case-insensitive
- Print an error for unrecognized commands
- Print errors for invalid command formats (when applicable)
- Capture 3 variations of each command except "/quit"
*/
import java.util.*;

public class SlashCommandHandler extends BaseClass {
    private static String ucid = "aa2984"; // <-- change to your UCID

    public static void main(String[] args) {
        printHeader(ucid, 2, "Objective: Implement a simple slash command parser.");

        Scanner scanner = new Scanner(System.in);
        Random randomGen1 = new Random();

        //aa2984: 2026-June-18
        //Summary: Solved by gathering user input in a loop and splitting into parts
        //Filter and mach based on case and route to proper handler
        //Check and validate formats for roll command and printed errors for commands which not recognized


        //Continously running until the person types /quit
        while (true) {

            String userInput1 = scanner.nextLine().trim();
            //Split input into command and arguments
            String[] inputParts1 = userInput1.split(" ", 2);
            String command1 = inputParts1[0].toLowerCase();

            //Filter for 'greet'
            if (command1.equals("/greet")) {
                //Process 'greet'
                if (inputParts1.length < 2 || inputParts1[1].trim().isEmpty()) {
                    System.out.println("Invalid format. Usage: /greet <name>");
                } else {
                    String greetName1 = inputParts1[1].trim();
                    System.out.println("Hello, " + greetName1 + "!");
                }

            //Filter if 'roll'
            } else if (command1.equals("/roll")) {
                //Check if format invalid
                if (inputParts1.length < 2 || !inputParts1[1].contains("d")) {
                    System.out.println("Invalid format. Usage: /roll <num>d<sides>");
                } else {
                    //Use roll
                    String[] rollParts1 = inputParts1[1].split("d");
                    try {
                        //[Fill] dice room
                        int numberOfDice1 = Integer.parseInt(rollParts1[0].trim());
                        int numberOfSides1 = Integer.parseInt(rollParts1[1].trim());

                        int rollTotal1 = 0; //initialize total
                        //create for loop iterate
                        for (int rollIndex1 = 0; rollIndex1 < numberOfDice1; rollIndex1++) {
                            rollTotal1 += randomGen1.nextInt(numberOfSides1) + 1;
                        }
                        System.out.println("Rolled " + numberOfDice1 + "d" + numberOfSides1 + " and got " + rollTotal1 + "!");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid format. Usage: /roll <num>d<sides>");
                    }
                }

            } else if (command1.equals("/echo")) {
                if (inputParts1.length < 2 || inputParts1[1].trim().isEmpty()) {
                    System.out.println("invalid format. Usage: /echo <message>");
                } else {
                    String echoMessage1 = inputParts1[1].trim();
                    System.out.println(echoMessage1);
                }

            } else if (command1.equals("/quit")) {
                System.out.println("Goodbye!");
                break;

            } else {
                System.out.println("unrecognized command: " + command1);
            }

        } // closes while

        printFooter(ucid, 2);
        scanner.close();
    }
}


