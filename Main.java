import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int numberOfPlayers = 0;
        int totalPoints = 0;
        Scanner input = new Scanner(System.in);

        numberOfPlayers = Utilities.isInputValid(input, "Ingrese cantidad de jugadores: ", 2, 4);
        totalPoints = Utilities.isInputValid(input, "Ingrese cantidad de puntos a jugar: ", 1, 100000);


        Hangman hangman = new Hangman(numberOfPlayers, totalPoints);
    }
}