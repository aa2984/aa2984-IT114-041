package M3;

/*
Challenge 1: Command-Line Calculator
------------------------------------
- Accept two numbers and an operator as command-line arguments
- Supports addition (+) and subtraction (-)
- Allow integer and floating-point numbers
- Ensures correct decimal places in output based on input (e.g., 0.1 + 0.2 → 1 decimal place)
- Display an error for invalid inputs or unsupported operators
- Capture 5 variations of tests
*/

public class CommandLineCalculator extends BaseClass {
    private static String ucid = "aa2984"; // <-- change to your ucid



    //aa2984: 2026-June-18
    //Summary: Completed task by taking the three command line arguments as strings
    //Then validated whether or not operator was valid, counted decimal places, and parsed numbers to double
    //Resulted in addition or subtraction and formatte result to longest decimal place
    public static void main(String[] args) {
        printHeader(ucid, 1, "Objective: Implement a calculator using command-line arguments.");

        if (args.length != 3) {
            System.out.println("Usage: java M3.CommandLineCalculator <num1> <operator> <num2>");
            printFooter(ucid, 1);
            return; //stop here
        }

        try {
            System.out.println("Calculating result...");
            // extract the equation (format is <num1> <operator> <num2>)

            //pulling the three arguments from command line
            String firstNumber1 = args[0];
            String operator1 = args[1];
            String secondNumber1 = args[2];

            if (!operator1.equals("+") && !operator1.equals("-")) {
                System.out.println("Unsupported operator. Only '+' and '-' are allowed.");
                printFooter(ucid, 1);
                return; //stop here 
            }

            //Create operation to count how many decimal places each number has
            int firstDecimalPlaces1 = firstNumber1.contains(".")
                ? firstNumber1.length() - firstNumber1.indexOf(".") - 1
                : 0;
            int secondDecimalPlaces1 = secondNumber1.contains(".") 
            ? secondNumber1.length() - secondNumber1.indexOf(".") - 1
            : 0; // The number that has more decimal places sets the output format

        
            int longestDecimalPlaces1 = Math.max(firstDecimalPlaces1, secondDecimalPlaces1);
            //Convert both strings into actual numbers we can do math with
            double parsedFirstNumber1 = Double.parseDouble(firstNumber1);
            double parsedSecondNumber1 = Double.parseDouble(secondNumber1);

            //Ensure to reject anything that isn't plus or -
            double calculationResult1 = 0;
            if (operator1.equals("+")) {
                calculationResult1 = parsedFirstNumber1 + parsedSecondNumber1;
            }   else if (operator1.equals("-")) {
                calculationResult1 = parsedFirstNumber1 - parsedSecondNumber1;
            } //Counting how many digits are after said decimal point in each number

            if (longestDecimalPlaces1 == 0) {
                System.out.println("Result: " + (int) calculationResult1);
            } else {
               

                String decimalFormat1 = "%." + longestDecimalPlaces1 + "f";
                System.out.println("Result: " + String.format(decimalFormat1, calculationResult1));


            }
    
            // check if operator is addition or subtraction
            
            // check the type of each number and choose appropriate parsing

            // generate the equation result (Important: ensure decimals display as the
            // longest decimal passed)
            // i.e., 0.1 + 0.2 would show as one decimal place (0.3), 0.11 + 0.2 would shows
            // as two (0.31), etc

        } catch (Exception e) {
            System.out.println("Invalid input. Please ensure correct format and valid numbers.");
        }

        printFooter(ucid, 1);
    }

}