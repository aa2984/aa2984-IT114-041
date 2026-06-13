package M2;
// copilot: disable
// @ts-nocheck

public class Scenario3 extends BaseClass {
    private static Integer[] array1 = {42, -17, 89, -256, 1024, -4096, 50000, -123456};
    private static Double[] array2 = {3.14159265358979, -2.718281828459, 1.61803398875, -0.5772156649, 0.0000001, -1000000.0};
    private static Float[] array3 = {1.1f, -2.2f, 3.3f, -4.4f, 5.5f, -6.6f, 7.7f, -8.8f};
    private static String[] array4 = {"123", "-456", "789.01", "-234.56", "0.00001", "-99999999"};
    private static Object[] array5 = {-1, 1, 2.0f, -2.0d, "3", "-3.0"};
    private static void bePositive(Object[] arr, int arrayNumber) {
        // Only make edits between the designated "Start" and "End" comments
        printScenario3ArrayInfo(arr, arrayNumber);
        // This should be solved without Copilot auto-completion, to toggle it, click the Copilot chat bubble at the top of the editor.
        //  Configure inline suggestions to "Disabled Inline Suggestions" (or similar) when writing code for this problem.

        // Challenge 1: Make each value positive
        // Challenge 2: Convert the values back to their original data type and assign it to the proper slot in the `output` array
        // Step 1: sketch out plan using comments (include ucid and date)
        // Step 2: Add/commit your outline of comments (required for full credit)
        // Step 3: Add code to solve the problem (add/commit as needed)
        Object[] output = new Object[arr.length];
        // Start Solution Edits

      //aa2984: 2026-June-13
      //Summary: Solved the problem by looping through each value in array
      //Value checked by the original types
      //Objective resovled in which numbers convered to positive ,while keeping each value in its original data type

      //For loop iterates through each value in the array
      for (int index1 = 0; index1 < arr.length; index1++) {
        //Object stores the current value
        Object element1 = arr[index1];

        //Check if the value is empty, if so continue
        if (element1 == null) {
          continue;
        }

        //Convert Integer values to positive numbers only
        if (element1 instanceof Integer) {
          output[index1] = Math.abs((Integer) element1);
        }
        //Otherwise convert these double values to positive
        else if (element1 instanceof Double) {
          output[index1] = Math.abs((Double) element1);
        }
        //Otherwise convert these float values to positive
        else if (element1 instanceof Float) {
          output[index1] = Math.abs((Float) element1);
        }  
        //Check if said value is a string
        else if (element1 instanceof String) {
        //Store this string value
          String strVal1 = (String) element1;
        //Remove the "-" 
        if (strVal1.startsWith("-")) {
          output[index1] = strVal1.substring(1);
        } else {
          //Leave string value as is 
            output[index1] = strVal1;
        }
      }
      }

        // End Solution Edits
        printOutputWithType(output, true);
    }

    public static void main(String[] args) {
        final String ucid = "aa2984"; // <-- change to your UCID
        // no edits below this line
        printHeader(ucid, 3);
        bePositive(array1, 1);
        bePositive(array2, 2);
        bePositive(array3, 3);
        bePositive(array4, 4);
        bePositive(array5, 5);
        printFooter(ucid, 3);

    }
}
