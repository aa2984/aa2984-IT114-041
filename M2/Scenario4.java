package M2;
// copilot: disable
// @ts-nocheck

public class Scenario4 extends BaseClass {
    private static String[] array1 = { "hello world!", "java programming", "special@#$%^&characters", "numbers 123 456",
            "mIxEd CaSe InPut!" };
    private static String[] array2 = { "hello world", "java programming", "this is a title case test",
            "capitalize every word", "mixEd CASE input" };
    private static String[] array3 = { "  hello   world  ", "java    programming  ",
            "  extra    spaces  between   words   ",
            "      leading and trailing spaces      ", "multiple      spaces" };
    private static String[] array4 = { "hello world", "java programming", "short", "a", "even" };

    private static void transformText(String[] arr, int arrayNumber) {
        // Only make edits between the designated "Start" and "End" comments
        printScenario4ArrayInfo(arr, arrayNumber);
        // This should be solved without Copilot auto-completion, to toggle it, click the Copilot chat bubble at the top of the editor.
        //  Configure inline suggestions to "Disabled Inline Suggestions" (or similar) when writing code for this problem.

        // Challenge 1: Remove non-alphanumeric characters except spaces
        // Challenge 2: Convert text to Title Case
        // Challenge 3: Remove leading/trailing spaces and remove duplicate spaces between words
        // Result 1-3: Assign final phrase to `placeholderForModifiedPhrase`
        // Challenge 4 (extra credit): Extract up to middle 3 characters (beginning starts at middle of phrase, exclude the first and last character for shorter phrases)
        // Assign result to 'placeholderForMiddleCharacters'
        // If not enough characters in a word, instead assign "Not enough characters" to `placeholderForMiddleCharacters`
 
        // Step 1: sketch out plan using comments (include ucid and date)
        // Step 2: Add/commit your outline of comments (required for full credit)
        // Step 3: Add code to solve the problem (add/commit as needed)
        String placeholderForModifiedPhrase = "";
        String placeholderForMiddleCharacters = "";
        
        for(int i = 0; i <arr.length; i++){
            // Start Solution Edits
            //aa2984: 2026-June-13
            //Summary: Solved the problem by looping through each value in the array and using regular expressions and string replacement to keep strings clean
            //Split up phrases into words in which using a secondary loop for capitilization for Title case and right after making their first letters upper case
            //Using calculations for the middle 3 characters by finding center of string and using string length/2 and taking one character to its left and right 

            
          String rawInput1 = arr[i];

          //First check if string exists using if statement
          if (rawInput1 != null) {
            //Next step to remove symbols
            String cleaned1 = rawInput1.replaceAll("[^a-zA-Z0-9 ]", "");
            String collapsed1 = cleaned1.trim().replaceAll(" +", " ");
            //Another checker to see if any text is remaining
            if (!collapsed1.isEmpty()) {

            //Split text into words
              String[] words1 = collapsed1.split(" ");

            //Create new sentence
              StringBuilder titleBuilder1 = new StringBuilder();
            //Loop through each word
              for (int wordPosition1 = 0; wordPosition1 < words1.length; wordPosition1++) { 

                //Get current word
                String word1 = words1[wordPosition1];

                //Skip words in which they are empty
                if (!word1.isEmpty()) {
                  String titledWord1 = word1.substring(0, 1).toUpperCase() + word1.substring(1).toLowerCase();
                  titleBuilder1.append(titledWord1);

                  if (wordPosition1 < words1.length - 1) {
                    titleBuilder1.append(" ");
                  }
                }
              }
              //Save the finalized text
              placeholderForModifiedPhrase = titleBuilder1.toString();
            } else {
              //Save the empty text
              placeholderForModifiedPhrase = "";
            }

            int len1 = placeholderForModifiedPhrase.length();

            if (len1 < 3) {
              placeholderForMiddleCharacters = "Not enough characters";

            } else {
              //Setting indexes
              int midIndex1 = len1 / 2;
              int start1 = midIndex1 - 1;
              int end1 = midIndex1 + 2;
              placeholderForMiddleCharacters = placeholderForModifiedPhrase.substring(start1, end1);
              
            }
              
          }      
                

          
            // End Solution Edits
            System.out.println(String.format("Index[%d] \"%s\" | Middle: \"%s\"",i, placeholderForModifiedPhrase, placeholderForMiddleCharacters));
        }
        System.out.println("");
        System.out.println("______________________________________");
    }

    public static void main(String[] args) {
        final String ucid = "aa2984"; // <-- change to your UCID
        // No edits below this line
        printHeader(ucid, 4);

        transformText(array1, 1);
        transformText(array2, 2);
        transformText(array3, 3);
        transformText(array4, 4);
        printFooter(ucid, 4);
    }

}
