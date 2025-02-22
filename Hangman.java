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
    private int turn;
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
        this.isTurnOver = false;
    }

    public void initializeFirstRound() {
        createPlayersScoreBoard();
        initiateWordBank();
        generateSentenceToGuess();
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
     * Checks if the guessed letter is present in the sentence to guess.
     * @return true if the letter is present, false otherwise.
     */
    public boolean isLetterPresent(Character letter) {
        return sentenceToGuess.containsKey(letter);
    }

    /**
     * Processes the action when the guessed letter is present in the sentence.
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
     * @param newPoints The points to be added or subtracted.
     * @param turn The current turn.
     */
    public void setPlayersPoints(int newPoints, int turn) {
        String player = "Jugador " + (turn);
        int currentPoints = playersPoints.get(player);
        playersPoints.put(player, currentPoints + this.newPoints);
    }

    /**
     * Process the used letter to get the points to be added.
     * @param letter
     */
    public void processLetter(Character letter) {
        this.newPoints = 0;
        boolean letterIsPresent = isLetterPresent(letter);
        this.letter = letter;

        if (letterIsPresent && !usedLetters.contains(letter)) {
            letterFrequency = actionIfLetterIsPresent();
            if (letterFrequency > 1) {
                this.newPoints = 3 * letterFrequency;
            } else {
                this.newPoints = 3;
            }

            if (!isRoundOver()) {
                setPlayersPoints(this.newPoints, this.turn);
                return;
            }

        } else if (!letterIsPresent && !usedLetters.contains(letter)) {
            actionIfLetterIsNotPresent();
            this.newPoints = -1;
            this.isTurnOver = true;
        } else {
            this.newPoints = -3;
            this.isTurnOver = true;
        }

        setPlayersPoints(this.newPoints, this.turn);
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
     * Getter for the selected sentence.
     * @return the sentence to guess.
     */
    public String getSentenceToGuess() {
        return this.selectedSentence;
    }

    /**
     * Getter for the used letters.
     * @return HashSet with the used letters.
     */
    public HashSet<Character> getUsedLetters() {
        return this.usedLetters;
    }

    /**
     * Getter for the guessed letters.
     * @return HashSet with the guessed letters.
     */
    public HashSet<Character> getGuessedLetters() {
        return this.guessedLetters;
    }

    /**
     * Getter for the current points of the players.
     * @return HashMap with all the players points.
     */
    public HashMap<String, Integer> getPlayersPoints() {
        return this.playersPoints;
    }

    /**
     * Getter for the winner key.
     * @return the winner key of the HashMap.
     */
    public String getWinnerKey() {
        return this.winnerKey;
    }

    /**
     * Getter for the total of points to play.
     * @return the total of points.
     */
    public int getTotalPoints() {
        return this.totalPoints;
    }

    /**
     * Getter for the used letter.
     * @return the used letter.
     */
    public Character getUsedLetter() {
        return this.letter;
    }

    /**
     * Getter for the current turn.
     * @return the current turn.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Setter for the turn.
     * @param turn
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * Getter for isTurnOver.
     * @return true if the turn is over.
     */
    public boolean getIsTurnOver() {
        return this.isTurnOver;
    }

    /**
     * Setter for isTurnOver.
     * @param isTurnOver
     */
    public void setIsTurnOver(boolean isTurnOver) {
        this.isTurnOver = isTurnOver;
    }

    /**
     * Getter for the new points added to the player in turn.
     * @return the points added.
     */
    public int getNewPoints()
    {
        return this.newPoints;
    }

    /**
     * Setter for the new points to be added to the player in turn
     * @param newPoints
     */
    public void setNewPoints(int newPoints) {
        this.newPoints = newPoints;
    }

    /**
     * Getter for the pointsToString.
     * @return the points in text form.
     */
    public String pointsToString() {
        StringBuilder text = new StringBuilder();
        String textToString;

        for (Map.Entry<String, Integer> entry : playersPoints.entrySet()) {
            String player = entry.getKey();
            Integer points = entry.getValue();
            text.append(player).append(": ").append(points).append(" puntos.  ");
        }

        return textToString = text.toString();
    }
}