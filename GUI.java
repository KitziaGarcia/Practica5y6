import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.ArrayList;

public class GUI extends JFrame {
    private JPanel keyboardPanel;
    private JPanel keyboardContainer;
    private JLabel wordLabel;
    private String wordToGuess;
    private StringBuilder currentWord;
    private Hangman game;
    private JPanel categoryPanel;
    private JLabel categoryLabel;
    private JPanel wordLabelPanel;
    private boolean letterIsPresent;
    boolean thereIsAWinner;
    private ArrayList<JButton> letterButtons;
    private int numberOfPLayers;
    private int totalOfPoints;
    private JLabel usedLettersLabel;
    private JPanel usedLettersPanel;
    private JLabel addedPointsLabel;
    private JPanel labelsPanel;

    /**
     * Constructor.
     */
    public GUI() {
        setNumberOfPlayers();
    }

    /**
     * This method creates a text field to input the total number of players, checks if it's between a
     * valid range and initializes the GUI.
     */
    public void setNumberOfPlayers() {
        JFrame inputFrame = new JFrame("Ingrese cantidad de jugadores");
        inputFrame.setSize(350, 130);
        inputFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inputFrame.setLayout(new FlowLayout());

        JLabel numberOfPlayersLabel = new JLabel("Ingrese cantidad de jugadores:");
        JTextField numberOfPlayersInput = new JTextField(10);

        JLabel totalOfPointsLabel = new JLabel("Ingrese puntos necesarios para ganar:");
        JTextField totalOfPointsInput = new JTextField(10);

        JTextField input = new JTextField(10);
        JButton enter = new JButton("OK");

        inputFrame.add(numberOfPlayersLabel);
        inputFrame.add(numberOfPlayersInput);
        inputFrame.add(totalOfPointsLabel);
        inputFrame.add(totalOfPointsInput);
        inputFrame.add(enter);

        enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    numberOfPLayers = Integer.parseInt(numberOfPlayersInput.getText());
                    totalOfPoints = Integer.parseInt(totalOfPointsInput.getText());

                    if (numberOfPLayers >= 2 && numberOfPLayers <= 4) {
                        inputFrame.dispose();
                        game = new Hangman(numberOfPLayers, totalOfPoints);
                        initializeGUI();
                        play();
                    } else {
                        JOptionPane.showMessageDialog(inputFrame, "Por favor, ingresa un número de jugadores entre 2 y 4.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(inputFrame, "Por favor, ingrese números válidos en ambos campos.");
                }
            }
        });

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    /**
     * Sets all the initial elements of the GUI and it initializes the game.
     */
    public void initializeGUI() {
        this.thereIsAWinner = false;
        setTitle("Hangman Game");
        setSize(400, 400); // Increase window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the current word with underscores
        game.initializeFirstRound();
        this.wordToGuess = game.getSentenceToGuess();

        // Container for the keyboard in the lower half of the screen
        keyboardContainer = new JPanel();
        keyboardContainer.setLayout(new BorderLayout());
        keyboardContainer.setPreferredSize(new Dimension(400, 200));

        // Panel and label for the used letters.
        usedLettersLabel = new JLabel();
        usedLettersPanel = new JPanel();
        usedLettersPanel.setLayout(new BorderLayout());
        usedLettersPanel.add(usedLettersLabel);
        letterButtons = new ArrayList<>();

        // Panel for the keyboard buttons.
        keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 1, 5, 5));

        // Creation of the first row panel with the corresponding letters.
        JPanel firstRow = new JPanel(new GridLayout(1, 10, 5, 5));
        String firstRowLetters = "QWERTYUIOP";
        for (char letter : firstRowLetters.toCharArray()) {
            JButton letterButton = new JButton(String.valueOf(letter));
            letterButtons.add(letterButton);
            letterButton.setFont(new Font("Arial", Font.BOLD, 16));
            firstRow.add(letterButton);

            letterButton.addActionListener(e -> {
                String selectedLetter = letterButton.getText();
                processLetter(selectedLetter.charAt(0));
            });
        }

        // Creation of the second row panel with the corresponding letters.
        JPanel secondRow = new JPanel(new GridLayout(1, 9, 5, 5));
        String secondRowLetters = "ASDFGHJKL";
        for (char letter : secondRowLetters.toCharArray()) {
            JButton letterButton = new JButton(String.valueOf(letter));
            letterButtons.add(letterButton);
            letterButton.setFont(new Font("Arial", Font.BOLD, 16));
            secondRow.add(letterButton);

            letterButton.addActionListener(e -> {
                String selectedLetter = letterButton.getText();
                processLetter(selectedLetter.charAt(0));
            });
        }

        // Creation of the third row panel with the corresponding letters.
        JPanel thirdRow = new JPanel(new GridLayout(1, 7, 5, 5));
        String thirdRowLetters = "ZXCVBNM";
        for (char letter : thirdRowLetters.toCharArray()) {
            JButton letterButton = new JButton(String.valueOf(letter));
            letterButtons.add(letterButton);
            letterButton.setFont(new Font("Arial", Font.BOLD, 16));
            thirdRow.add(letterButton);

            letterButton.addActionListener(e -> {
                String selectedLetter = letterButton.getText();
                processLetter(selectedLetter.charAt(0));
            });
        }

        keyboardPanel.add(firstRow);
        keyboardPanel.add(secondRow);
        keyboardPanel.add(thirdRow);

        keyboardContainer.add(usedLettersPanel,  BorderLayout.NORTH);
        keyboardContainer.add(keyboardPanel,  BorderLayout.CENTER);
        add(keyboardContainer, BorderLayout.SOUTH);

        // Panel for the information of the round.
        labelsPanel = new JPanel(new GridLayout(2, 1));
        categoryLabel = new JLabel();
        categoryLabel.setHorizontalAlignment(SwingConstants.LEFT);
        categoryLabel.setForeground(Color.BLACK);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        labelsPanel.add(categoryLabel);

        addedPointsLabel = new JLabel();
        labelsPanel.add(addedPointsLabel);

        add(labelsPanel, BorderLayout.NORTH);

        // Panel and label for the word to guess.
        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setLayout(new BorderLayout());
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));

        wordLabelPanel = new JPanel();
        wordLabelPanel.setLayout(new BorderLayout());
        wordLabelPanel.add(wordLabel, BorderLayout.CENTER);
        add(wordLabelPanel, BorderLayout.CENTER);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Method that sets every round.
     */
    public void play() {
        currentWord = new StringBuilder();
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 18));
        categoryLabel.setText("Categoria: Dichos populares.                  " + "Turno Jugador " + game.getTurn() + ".");
        System.out.println("TO GUESS: " + this.wordToGuess);
        showAddedPoints();
        showUsedLetters();
        showGuessedLetters();
    }

    /**
     * Method that shows the guessed letters and underscores for the letters to guess.
     */
    public void showGuessedLetters() {
        currentWord.setLength(0);

        for (int i = 0; i < this.wordToGuess.length(); i++) {
            if ((wordToGuess.charAt(i) != ' ') && !game.getUsedLetters().contains(wordToGuess.charAt(i))) {
                currentWord.append("__ ");
            } else if ((wordToGuess.charAt(i) != ' ') && game.getGuessedLetters().contains(wordToGuess.charAt(i))) {
                currentWord.append(" ").append(wordToGuess.charAt(i)).append(" ");
            } else {
                currentWord.append("    ");
            }
        }

        wordLabel.setText(currentWord.toString());
        wordLabelPanel.revalidate();
        wordLabelPanel.repaint();
    }

    /**
     * Method that process the used letter and sets a new turn/round.
     * @param usedLetter
     */
    public void processLetter(Character usedLetter) {
        String winnerKey = game.getWinnerKey();
        int turn = game.getTurn();

        game.processLetter(usedLetter);

        // Check if the letter is in the sentence.
        this.letterIsPresent = game.isLetterPresent(usedLetter);
        showGuessedLetters();

        if (!this.thereIsAWinner) {
            this.thereIsAWinner = game.isThereAWinner();
        }

        if ((game.getPlayersPoints().get(game.getWinnerKey()) != null) && game.getPlayersPoints().get(game.getWinnerKey()) < game.getTotalPoints()) {
            this.thereIsAWinner = game.isThereAWinner();
        }

        showAddedPoints();

        // If the player guessed a letter it's their turn again.
        if (!game.isRoundOver() && letterIsPresent && !game.getIsTurnOver()) {
            play();

        // If the didn't guess a letter, update the turn for the next player.
        } else if ((!game.isRoundOver() && !letterIsPresent && game.getIsTurnOver()) || (!game.isRoundOver() && game.getUsedLetters().contains(usedLetter))) {
            game.setNextTurn();
            game.setIsTurnOver(false);
            play();

        // Get new points in case the round is over.
        } else if (game.isRoundOver() && !this.thereIsAWinner) {
            game.setNewPoints(game.getNewPoints() + 5);
            game.setPlayersPoints(game.getNewPoints(), game.getTurn());

            if (game.isThereAWinner()) {
                showAddedPoints();
                JOptionPane.showMessageDialog(null, "Ha ganado el " + game.getWinnerKey());
                showUsedLetters();
                System.exit(0);
            } else {
                showAddedPoints();
                JOptionPane.showMessageDialog(null, "Has ganado " + game.getNewPoints() + " puntos por adivinar la ultima letra y la palabra.");
                game.setNewPoints(0);
                //game.setTurn(1);
                game.initializeNewRound();
                game.generateSentenceToGuess();
                this.wordToGuess = game.getSentenceToGuess();
                play();
            }
        } else if (game.isRoundOver() && this.thereIsAWinner) {
            showGuessedLetters();

            if (Objects.equals(game.getPlayersPoints().get(winnerKey), game.getPlayersPoints().get("Jugador " + turn))) {
                game.setNewPoints(game.getNewPoints() + 5);
                game.setPlayersPoints(game.getNewPoints(), game.getTurn());
                showAddedPoints();
            }

            showAddedPoints();
            showUsedLetters();
            JOptionPane.showMessageDialog(null, "Ha ganado el " + game.getWinnerKey());
            System.exit(0);
            return;
        }
    }

    /**
     * Method that shows the used letters in a label.
     */
    public void showUsedLetters() {
        usedLettersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usedLettersLabel.setText("LETRAS USADAS: " + game.getUsedLetters().toString());
        keyboardContainer.revalidate();
        keyboardContainer.repaint();
    }

    /**
     * Method that shows the players points in a label.
     */
    public void showAddedPoints() {
        addedPointsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        addedPointsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        addedPointsLabel.setForeground(Color.BLACK);
        addedPointsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        labelsPanel.add(addedPointsLabel);

        // Show a message according to the points added.
        switch(game.getNewPoints()) {
            case 0:
                addedPointsLabel.setText(game.pointsToString());
                break;
            case -1:
                addedPointsLabel.setText(game.pointsToString() + "        Has perdido 1 punto porque la letra no se encuentra.");
                break;
            case -3:
                addedPointsLabel.setText(game.pointsToString() + "        Has perdido 3 puntos porque la letra " + game.getUsedLetter() + " ya habia sido utilizada.");
                break;
            case 5:
                addedPointsLabel.setText(game.pointsToString() + "        Has ganado 5 puntos porque adivinaste la palabra.");
            default:
                addedPointsLabel.setText(game.pointsToString() + "        Has ganado " + game.getNewPoints() + " puntos.");
                break;
        }
    }
}