import java.util.*;

public class Hangman {
    private int numberOfPlayers;
    private int totalPoints;
    private HashSet<String> wordBank;
    private HashSet<Character> usedLetters;
    private HashSet<Character> guessedLetters;
    private HashMap<Character, Integer> sentenceToGuess;
    private HashMap<String, Integer> playersPoints;
    private HashSet<Integer> usedIndex;
    private String selectedSentence;
    private Character letter;
    private boolean letterIsPresent;
    private int turn;
    private boolean thereIsAWinner;
    private String winnerKey;
    private int letterFrequency;
    private int newPoints;
    private boolean isTurnOver;

    /**
     * Constructor.
     */
    public Hangman(int numberOfPlayers, int totalPoints) {
        usedLetters = new HashSet<>();
        sentenceToGuess = new HashMap<>();
        guessedLetters = new HashSet<>();
        wordBank = new HashSet<>();
        usedIndex = new HashSet<>();
        this.numberOfPlayers = numberOfPlayers;
        this.totalPoints = totalPoints;
        this.turn = 1;
        this.thereIsAWinner = false;
        this.isTurnOver = false;
    }

    public void initializeFirstRound() {
        createPlayersScoreBoard();
        initiateWordBank();
        generateSentenceToGuess();
    }

    /**
     * Manages the game flow, including player turns, scoring, and checking for winners.
     */
    public void play() {
        Scanner input = new Scanner(System.in);

        System.out.println("----------------------------");
        System.out.println("TURNO: " + turn);
        System.out.println("LETRAS USADAS: " + usedLetters);
        System.out.println();
        showPoints();
        System.out.println();
        System.out.println("Categoria: Dichos populares.");
        System.out.println();
        showGuessedLetters();
        System.out.println();

        System.out.println("Ingrese una letra: ");
        //this.letter = Character.toUpperCase(input.next().charAt(0));
        //this.letterIsPresent = isLetterPresent(letter);

        //processLetter(letter);
        // Actions in case the letter is present in the sentence or not.
        /*if (letterIsPresent && !usedLetters.contains(letter)) {
            letterFrequency = actionIfLetterIsPresent();
            showGuessedLetters();
            if (letterFrequency > 1) {
                newPoints = 3 * letterFrequency;
                //System.out.println("Obtuviste " + newPoints + " puntos porque la letra aparece " + letterFrequency + " veces.");
            } else {
                newPoints = 3;
                //System.out.println("Obtuviste 3 puntos porque la letra aparece una vez.");
            }

            pressEnterToContinue();

            if (!isRoundOver()) {
                setPlayersPoints(newPoints, this.turn);
                play();
                return;
            }

        } else if (!letterIsPresent && !usedLetters.contains(letter)) {
            //System.out.println("Perdiste -1 puntos porque la letra no se encuentra.");
            actionIfLetterIsNotPresent();
            newPoints = -1;
        } else {
            //System.out.println("Perdiste 3 puntos porque la letra ya fue utilizada.");
            newPoints = -3;
        }

        setPlayersPoints(newPoints, this.turn);*/

        // Check for a winner.
        if (!thereIsAWinner) {
            this.thereIsAWinner = isThereAWinner();
        }

        if ((playersPoints.get(this.winnerKey) != null) && playersPoints.get(this.winnerKey) < totalPoints) {
            this.thereIsAWinner = isThereAWinner();
        }

        // Actions depending on the round status and if there's a game winner.
        if (!isRoundOver()) {
            pressEnterToContinue();
            setNextTurn();
            //play();
        } else if (isRoundOver() && !thereIsAWinner ) {
            System.out.println();
            showGuessedLetters();
            newPoints = 5;
            setPlayersPoints(this.newPoints, this.turn);
            showPoints();
            System.out.println();

            if (thereIsAWinner) {
                System.out.println("Ha ganado el " + winnerKey);
            } else {
                System.out.println("Has ganado 5 puntos porque se ha adivinado la palabra. Presione ENTER para comenzar otra ronda.");
                pressEnterToContinue();
                this.turn = 1;
                initializeNewRound();
                generateSentenceToGuess();
                //play();
            }
        } else if (isRoundOver() && thereIsAWinner) {
            System.out.println();
            showGuessedLetters();

            if (Objects.equals(playersPoints.get(winnerKey), playersPoints.get("Jugador " + this.turn))) {
                newPoints = 5;
                setPlayersPoints(this.newPoints, this.turn);
                System.out.println("Has ganado 5 puntos porque se ha adivinado la palabra.");

                pressEnterToContinue();
            }
            showPoints();
            System.out.println("Ha ganado el " + winnerKey);
            return;
        }
    }

    /**
     * Initializes the players' scoreboard with a score of zero for each player.
     */
    public void createPlayersScoreBoard() {
        playersPoints = new LinkedHashMap<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            String playerName = "Jugador " + i;
            playersPoints.put(playerName, 0);
        }
    }

    /**
     * Creates the word bank with popular sayings for the game.
     */
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

    /**
     * Selects a random sentence from the word bank that hasn't been used yet.
     */
    public void generateSentenceToGuess() {
        int value = 0;
        int randomIndex;

        if (usedIndex.size() >= wordBank.size()) {
            System.out.println("Ya se han usado todas las frases del banco de palabras.");
        }

        // Generate a randmom index and validate it hasn't been used before.
        do {
            randomIndex = new Random().nextInt(wordBank.size());
        } while (usedIndex.contains(randomIndex));

        usedIndex.add(randomIndex);
        this.selectedSentence = wordBank.toArray(new String[0])[randomIndex];

        for (char letter : selectedSentence.toCharArray()) {
            sentenceToGuess.put(letter, sentenceToGuess.getOrDefault(letter, value) + 1);
        }

        sentenceToGuess.remove(' ');
    }

    /**
     * Displays the current points of all players.
     */
    public void showPoints() {
        System.out.println("Puntos: ");
        for (int i = 0; i < numberOfPlayers; i++) {
            String player = "Jugador " + (i + 1);
            System.out.print(player + ": " + playersPoints.get(player) + ".  ");
        }
    }

    /**
     * Shows the letters that have been guessed so far and blanks for unguessed letters.
     */
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

    /**
     * Checks if the guessed letter is present in the sentence to guess.
     *
     * @return true if the letter is present, false otherwise.
     */
    public boolean isLetterPresent(Character letter) {
        return sentenceToGuess.containsKey(letter);
    }

    /**
     * Processes the action when the guessed letter is present in the sentence.
     *
     * @return The frequency of the letter in the sentence.
     */
    public int actionIfLetterIsPresent() {
        guessedLetters.add(letter);
        usedLetters.add(letter);
        return sentenceToGuess.get(letter);
    }

    /**
     * Processes the action when the guessed letter is not present in the sentence.
     */
    public void actionIfLetterIsNotPresent() {
        usedLetters.add(letter);
    }

    /**
     * Updates the player's score based on the new points earned during their turn.
     *
     * @param newPoints The points to be added or subtracted.
     * @param turn The current turn.
     */
    public void setPlayersPoints(int newPoints, int turn) {
        String player = "Jugador " + (turn);
        int currentPoints = playersPoints.get(player);
        System.out.println("NEW POINTS IN METHOD: " + (currentPoints + newPoints));
        playersPoints.put(player, currentPoints + newPoints);
    }

    public void processLetter(Character letter) {
        int newPoints = 0;
        boolean letterIsPresent = isLetterPresent(letter);
        this.letter = letter;

        System.out.println("LETTER: " + letter);
        if (letterIsPresent && !usedLetters.contains(letter)) {
            letterFrequency = actionIfLetterIsPresent();
            System.out.println("FREQUENCY: " + letterFrequency);
            //showGuessedLetters();
            if (letterFrequency > 1) {
                this.newPoints = 3 * letterFrequency;
                System.out.println("NEW POINTS: " + this.newPoints);
                //System.out.println("Obtuviste " + newPoints + " puntos porque la letra aparece " + letterFrequency + " veces.");
            } else {
                this.newPoints = 3;
                //System.out.println("Obtuviste 3 puntos porque la letra aparece una vez.");
            }

            if (!isRoundOver()) {
                setPlayersPoints(this.newPoints, this.turn);
                System.out.println("POINTS: " + getPlayersPoints());
                //play();
                return;
            }

        } else if (!letterIsPresent && !usedLetters.contains(letter)) {
            //System.out.println("Perdiste -1 puntos porque la letra no se encuentra.");
            actionIfLetterIsNotPresent();
            this.newPoints = -1;
            this.isTurnOver = true;
        } else {
            //System.out.println("Perdiste 3 puntos porque la letra ya fue utilizada.");
            this.newPoints = -3;
            this.isTurnOver = true;
        }

        setPlayersPoints(this.newPoints, this.turn);
        System.out.println("PUNTOS SWITCH: " + this.newPoints);
        System.out.println("POINTS: " + getPlayersPoints());
    }

    /**
     * Advances to the next player's turn, cycling back to the first player if necessary.
     */
    public void setNextTurn() {
        this.turn++;

        if (this.turn > numberOfPlayers) {
            this.turn = 1;
        }
    }

    /**
     * Checks if the round is over by determining if all letters in the sentence have been guessed.
     *
     * @return true if the round is over, false otherwise.
     */
    public boolean isRoundOver() {
        for (Character letter : selectedSentence.toCharArray()) {
            if (letter != ' ' && !guessedLetters.contains(letter)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if there is a winner by checking if any player's score meets the total points required.
     *
     * @return true if a winner is found, false otherwise.
     */
    public boolean isThereAWinner() {
        Optional<String> winner = playersPoints.entrySet().stream().filter(player -> player.getValue() >= this.totalPoints).map(Map.Entry::getKey).findAny();
        winner.ifPresent(key -> this.winnerKey = key);

        return winner.isPresent();
    }

    /**
     * Prepares for a new round by clearing used letters, guessed letters and the sentence to guess.
     */
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

    public String getSentenceToGuess() {
        return this.selectedSentence;
    }

    public HashSet<Character> getUsedLetters() {
        return this.usedLetters;
    }

    public HashSet<Character> getGuessedLetters() {
        return this.guessedLetters;
    }

    public HashMap<String, Integer> getPlayersPoints() {
        return this.playersPoints;
    }

    public HashSet<Integer> getUsedIndex() {
        return this.usedIndex;
    }

    public String getWinnerKey() {
        return this.winnerKey;
    }

    public int getTotalPoints() {
        return this.totalPoints;
    }

    public int getTurn() {
        return this.turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
    public boolean getIsTurnOver() {
        return this.isTurnOver;
    }

    public void setIsTurnOver(boolean isTurnOver) {
        this.isTurnOver = isTurnOver;
    }

    public int getNewPoints()
    {
        return this.newPoints;
    }

    public int getNumberOfPlayers() {
        return this.numberOfPlayers;
    }

    public String pointsToString() {
        StringBuilder text = new StringBuilder();
        String textToString;
        System.out.println("cantidad: " + numberOfPlayers);

        for (Map.Entry<String, Integer> entry : playersPoints.entrySet()) {
            String player = entry.getKey();
            Integer points = entry.getValue();
            text.append(player).append(": ").append(points).append(" puntos.  ");
        }

        return textToString = text.toString();
    }
}