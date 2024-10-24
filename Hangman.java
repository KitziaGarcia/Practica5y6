import java.util.*;

public class Hangman {
    private int numberOfPlayers;
    private int totalPoints;
    private int newPoints;
    private HashSet<String> wordBank;
    private HashSet<Character> usedLetters;
    private HashSet<Character> guessedLetters;
    private HashMap<Character, Integer> sentenceToGuess;
    private HashMap<String, Integer> playersPoints;
    private String selectedSentence;
    private Scanner input;
    private Character letter;
    private boolean letterIsPresent;
    private int turn;
    private boolean thereIsAWinner;
    private String winnerKey;
    private int letterFrequency;

    public Hangman(int numberOfPlayers, int totalPoints) {
        usedLetters = new HashSet<>();
        sentenceToGuess = new HashMap<>();
        guessedLetters = new HashSet<>();
        wordBank = new HashSet<>();
        this.numberOfPlayers = numberOfPlayers;
        this.totalPoints = totalPoints;
        this.turn = 1;
        this.thereIsAWinner = false;
        createPlayersScoreBoard();
        initiateWordBank();
        getSentenceToGuess();
        game();
    }


    public void game() {
        int newPoints = 0;
        Scanner input = new Scanner(System.in);
        showGuessedLetters();

        System.out.println("TURNO: " + turn);
        System.out.println("LETRAS USADAS: " + usedLetters);
        System.out.println();
        showPoints();
        System.out.println();

        System.out.println("Ingrese una letra: ");
        this.letter = Character.toUpperCase(input.next().charAt(0));
        this.letterIsPresent = isLetterPresent();

        if (letterIsPresent && !usedLetters.contains(letter)) {
            letterFrequency = actionIfLetterIsPresent();
            showGuessedLetters();
            if (letterFrequency > 1) {
                newPoints = 3 * letterFrequency;
                System.out.println("Obtuviste " + newPoints + " puntos porque la letra aparece " + letterFrequency + " veces.");
            } else {
                newPoints = 3;
                System.out.println("Obtuviste 3 puntos porque la letra aparece una vez.");
            }
        } else if (!letterIsPresent && !usedLetters.contains(letter)) {
            System.out.println("Perdiste -1 puntos porque la letra no se encuentra.");
            actionIfLetterIsNotPresent();
            newPoints = -1;
        } else {
            System.out.println("Perdiste 3 puntos porque la letra ya fue utilizada.");
            newPoints = -3;
        }

        setPlayersPoints(newPoints, this.turn);

        if (!thereIsAWinner) {
            this.thereIsAWinner = isThereAWinner();
        }

        if ((playersPoints.get(this.winnerKey) != null) && playersPoints.get(this.winnerKey) < totalPoints) {
            this.thereIsAWinner = isThereAWinner();
        }

        if (!isRoundOver()) {
            pressEnterToContinue();
            setNextTurn();
            game();
        } else if (isRoundOver() && !thereIsAWinner ) {
            System.out.println();
            showGuessedLetters();
            newPoints = 5;
            setPlayersPoints(newPoints, this.turn);
            showPoints();
            System.out.println();

            if (isThereAWinner()) {
                System.out.println("Ha ganado el " + winnerKey);
            } else {
                System.out.println("Has ganado 5 puntos porque se ha adivinado la palabra. Presione ENTER para comenzar otra ronda.");
                pressEnterToContinue();
                this.turn = 1;
                initializeNewRound();
                getSentenceToGuess();
                game();
            }
        } else if (isRoundOver() && thereIsAWinner) {
            System.out.println();
            showGuessedLetters();

            if (Objects.equals(playersPoints.get(winnerKey), playersPoints.get("Jugador " + this.turn))) {
                newPoints = 5;
                setPlayersPoints(newPoints, this.turn);
                System.out.println("Has ganado 5 puntos porque se ha adivinado la palabra.");
                pressEnterToContinue();
            }
            showPoints();
            System.out.println("Ha ganado el " + winnerKey);
        }
    }

    public void createPlayersScoreBoard() {
        playersPoints = new HashMap<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            String playerName = "Jugador " + i;
            playersPoints.put(playerName, 0);
        }
    }

    public void initiateWordBank() {
        wordBank.add("EL QUE NO ARRIESGA NO GANA");
        wordBank.add("A QUIEN MADRUGA DIOS LE AYUDA");
        wordBank.add("EN BOCA CERRADA NO ENTRAN MOSCAS");
        wordBank.add("MAS VALE TARDE QUE NUNCA");
        wordBank.add("EL QUE MUCHO ABARCA POCO APRIETA");
        wordBank.add("CAMARON QUE SE DUERME SE LO LLEVA LA CORRIENTE");
        wordBank.add("A CABALLO REGALADO NO LE MIRES EL DIENTE");
        wordBank.add("NO HAY MAL QUE POR BIEN NO VENGA");
        wordBank.add("QUIEN TIENE UN AMIGO TIENE UN TESORO");
        wordBank.add("LA ESPERANZA ES LO ULTIMO QUE SE PIERDE");

    }

    public void getSentenceToGuess() {
        int value = 0;
        int randomIndex = new Random().nextInt(wordBank.size());
        this.selectedSentence = wordBank.toArray(new String[0])[randomIndex];

        for (char letter : selectedSentence.toCharArray()) {
            sentenceToGuess.put(letter, sentenceToGuess.getOrDefault(letter, value) + 1);
        }

        sentenceToGuess.remove(' ');
        System.out.println("Palabra para adivinar: " + selectedSentence);
        System.out.println(sentenceToGuess);
    }

    public void showPoints() {
        System.out.println("Puntos: ");
        for (int i = 0; i < numberOfPlayers; i++) {
            String player = "Jugador " + (i + 1);
            System.out.print(player + ": " + playersPoints.get(player) + ".  ");
        }
    }

    public void showGuessedLetters() {
        for (int i = 0; i < selectedSentence.length(); i++) {
            if ((selectedSentence.charAt(i) != ' ') && !guessedLetters.contains(selectedSentence.charAt(i))) {
                System.out.print("_ ");
            } else if ((selectedSentence.charAt(i) != ' ') && guessedLetters.contains(selectedSentence.charAt(i)) ) {
                System.out.print(" " + selectedSentence.charAt(i) + " ");
            } else {
                System.out.print("    ");
            }
        }
        System.out.println();
    }

    public boolean isLetterPresent() {
        return sentenceToGuess.containsKey(letter);
    }

    public int actionIfLetterIsPresent() {
        guessedLetters.add(letter);
        usedLetters.add(letter);
        return sentenceToGuess.get(letter);
    }

    public void actionIfLetterIsNotPresent() {
        usedLetters.add(letter);
    }

    public void setPlayersPoints(int newPoints, int turn) {
        String player = "Jugador " + (turn);
        int currentPoints = playersPoints.get(player);
        playersPoints.put(player, currentPoints + newPoints);
    }

    public void setNextTurn() {
        this.turn++;

        if (this.turn > numberOfPlayers) {
            this.turn = 1;
        }
    }

    public boolean isRoundOver() {
        for (Character letter : selectedSentence.toCharArray()) {
            if (!usedLetters.contains(letter)) {
                return false;
            }
        }
          return true;
    }

    public boolean isThereAWinner() {
        Optional<String> winner = playersPoints.entrySet().stream().filter(player -> player.getValue() >= this.totalPoints).map(Map.Entry::getKey).findAny();
        winner.ifPresent(key -> this.winnerKey = key);

        return winner.isPresent();
    }

    public void initializeNewRound() {
        usedLetters.clear();
        sentenceToGuess.clear();
        guessedLetters.clear();
    }

    /**
     * Pauses the program execution until the user presses the Enter key.
     *
     * This method is used to prompt the user to continue, allowing them to read output or make decisions
     * before proceeding with the game.
     */
    public void pressEnterToContinue() {
        Scanner input = new Scanner(System.in);
        input.nextLine();
    }
}