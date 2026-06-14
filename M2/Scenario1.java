Scenario1.java
package M2;
// copilot: disable
// @ts-nocheck

public class Scenario1 extends BaseClass {
    private static int[] array1 = {0,1,2,3,4,5,6,7,8,9};
    private static int[] array2 = {9,8,7,6,5,4,3,2,1,0};
    private static int[] array3 = {0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9};
    private static int[] array4 = {9,9,8,8,7,7,6,6,5,5,4,4,3,3,2,2,1,1,0,0}; 
    private static void printOdds(int[] arr, int arrayNumber){
        // Only make edits between the designated "Start" and "End" comments
        printScenario1ArrayInfo(arr, arrayNumber);
        // This should be solved without Copilot auto-completion, to toggle it, click the Copilot chat bubble at the top of the editor.
        //  Configure inline suggestions to "Disabled Inline Suggestions" (or similar) when writing code for this problem.

        // Challenge 1: From each passed in array, print odd values only in a single line separated by commas and a space after each comma (should not have leading or trailing commas)
        // Step 1: sketch out plan using comments (include ucid and date)
        // Step 2: Add/commit your outline of comments (required for full credit)
        // Step 3: Add code to solve the problem (add/commit as needed)
        // Start Solution Edits

        //aa2984: 2026-June-13

        //Summary: Solved by properly iterating through the data with said loop to filter out odd numbers
        //Objective is to print values only in which are odd
        //Moreso using a boolean by checking if divisible by 2.

        boolean isFirstItem1 = true;
        //Checks if printing first odd number
        
        for (int value1 : arr) {
            //Essential part of the code that loops through every number in array
            if (value1 % 2 != 0) {
                if (!isFirstItem1) {
                    System.out.print(", ");
                    //Print only odd results
                }
                System.out.print(value1);
                isFirstItem1 = false;

            }

        }

    


        // End Solution Edits
        System.out.println("");
        System.out.println("__");
    }
    public static void main(String[] args) {
        final String ucid = "aa2984"; // <-- change to your UCID
        // no edits below this line
        printHeader(ucid, 1);
        printOdds(array1,1);
        printOdds(array2,2);
        printOdds(array3,3);
        printOdds(array4,4);
        printFooter(ucid, 1);
    }
}
        
