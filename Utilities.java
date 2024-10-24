import java.util.Scanner;

/**
 *
 * This class contains a method, isInputValid, that validates user input from the console.
 */
public class Utilities {
    public static int isInputValid(Scanner input, String message, int min, int max) {
        int input2 = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println(message);

            try {
                input2 = Integer.parseInt(input.nextLine());

                if (input2 >= min && input2 <= max) {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida.");
                System.out.println();
            }
        }
        return input2;
    }
}