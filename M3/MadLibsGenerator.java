package M3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


/*
Challenge 3: Mad Libs Generator (Randomized Stories)
-----------------------------------------------------
- Load a **random** story from the "stories" folder
- Extract **each line** into a collection (i.e., ArrayList)
- Prompts user for each placeholder (i.e., <adjective>) 
    - Any word the user types is acceptable, no need to verify if it matches the placeholder type
    - Any placeholder with underscores should display with spaces instead
- Replace placeholders with user input (assign back to original slot in collection)
*/

public class MadLibsGenerator extends BaseClass {
    private static final String STORIES_FOLDER = "M3/stories";
    private static String ucid = "aa2984"; // <-- change to your ucid

    //aa2984: 2026-June-18
    //Summary: Functionality of this file is that it picks a random file from stories folder
    //Reads the placeholders in that file using '<>'
    //Then prompts the user for each one , regardless which story is chosen


    public static void main(String[] args) {
        printHeader(ucid, 3,
                "Objective: Implement a Mad Libs generator that replaces placeholders dynamically.");

        Scanner scanner = new Scanner(System.in);
        File folder = new File(STORIES_FOLDER);

        if (!folder.exists() || !folder.isDirectory() || folder.listFiles().length == 0) {
            System.out.println("Error: No stories found in the 'stories' folder.");
            printFooter(ucid, 3);
            scanner.close();
            return; //stop here
        }
        List<String> lines = new ArrayList<>();
        // Start edits

        // load a random story file
        File[] storyFiles1 = folder.listFiles();
        Random randomPicker1 = new Random();
        File selectedStory1 = storyFiles1[randomPicker1.nextInt(storyFiles1.length)];
        System.out.println("Loaded story: " + selectedStory1.getName());

        // parse the story lines
        try {
            Scanner storyReader1 = new Scanner(selectedStory1);
            while (storyReader1.hasNextLine()) {
                lines.add(storyReader1.nextLine());
            }
            storyReader1.close();
            } catch (Exception e) {
            System.out.println("Error reading story file.");
            printFooter(ucid, 3);
            scanner.close();
            return; //stop here
        }

        for (int lineIndex1 = 0; lineIndex1 < lines.size(); lineIndex1++) {
            String currentLine1 = lines.get(lineIndex1);

            while (currentLine1.contains("<") && currentLine1.contains(">")) {
                int openBracket1 = currentLine1.indexOf("<");
                int closeBracket1 = currentLine1.indexOf(">");
                String placeholder1 = currentLine1.substring(openBracket1 + 1, closeBracket1);

                String displayPrompt1 = placeholder1.replace("_", " "); //replace underscores with spaces
                System.out.print("Enter a " + displayPrompt1 + ": ");
                String userAnswer1 = scanner.nextLine().trim();

                currentLine1 = currentLine1.replace("<" + placeholder1 + ">", userAnswer1);
                //After this will save updated line
                
            }
            lines.set(lineIndex1, currentLine1); 
        }
        // iterate through the lines

        // prompt the user for each placeholder (note: there may be more than one
        // placeholder in a line)

        // apply the update to the same collection slot

        // End edits
        System.out.println("\nYour Completed Mad Libs Story:\n");
        StringBuilder finalStory = new StringBuilder();
        for (String line : lines) {
            finalStory.append(line).append("\n");
        }
        System.out.println(finalStory.toString());

        printFooter(ucid, 3);
        scanner.close();
    }
}