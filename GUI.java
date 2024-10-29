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
    private JPanel pointsPanel;
    private JLabel pointsLabel;
    private int numberOfPLayers;
    private int totalOfPoints;
    private JLabel usedLettersLabel;
    private JPanel usedLettersPanel;

    public GUI() {
        setNumberOfPlayers();
    }

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

                    // Validación de la cantidad de jugadores.
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

    public void initializeGUI() {
        this.thereIsAWinner = false;
        setTitle("Hangman Game");
        setSize(400, 400); // Increase window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the current word with underscores
        game.initializeFirstRound();
        this.wordToGuess = game.getSentenceToGuess();
        System.out.println("TO GUESS: " + this.wordToGuess);

        // Create a container for the keyboard in the lower half of the screen
        keyboardContainer = new JPanel();
        keyboardContainer.setLayout(new BorderLayout());
        keyboardContainer.setPreferredSize(new Dimension(400, 200));

        usedLettersLabel = new JLabel();
        usedLettersPanel = new JPanel();
        usedLettersPanel.setLayout(new BorderLayout());
        usedLettersPanel.add(usedLettersLabel);
        letterButtons = new ArrayList<>();
/*
        keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 11, 5, 5));

        // Add letter buttons
        String letters = "QWERTYUIOPASDFGHJKLZXCVBNM";
        letterButtons = new ArrayList<>();
        for (char letter : letters.toCharArray()) {
            JButton letterButton = new JButton(String.valueOf(letter));
            letterButtons.add(letterButton);
            letterButton.setFont(new Font("Arial", Font.BOLD, 16));
            keyboardPanel.add(letterButton);

            letterButton.addActionListener(e -> {
                String selectedLetter = letterButton.getText();
                processLetter(selectedLetter.charAt(0));
            });
        }*/

        keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 1, 5, 5));

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

        categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryLabel = new JLabel();
        categoryLabel.setHorizontalAlignment(SwingConstants.LEFT);
        categoryLabel.setPreferredSize(new Dimension(500, 30));
        categoryLabel.setForeground(Color.BLACK); // Color del texto
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        categoryPanel.add(categoryLabel);
        add(categoryPanel, BorderLayout.NORTH);

        categoryLabel.setText("Categoria: Dichos populares.");

        categoryPanel.revalidate();
        categoryPanel.repaint();

        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setLayout(new BorderLayout());
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));

        wordLabelPanel = new JPanel();
        wordLabelPanel.setLayout(new BorderLayout());
        wordLabelPanel.add(wordLabel, BorderLayout.CENTER);
        add(wordLabelPanel, BorderLayout.CENTER);

        pointsLabel = new JLabel();
    }

    public void play() {
        int newPoints = 0;
        currentWord = new StringBuilder();
        System.out.println("------------------------------");
        System.out.println("USED LETTERS: " + game.getUsedLetters());
        System.out.println("SENTENCE TO GUESS: " + game.getSentenceToGuess());
        System.out.println("GUESSED LETTERS: " + game.getGuessedLetters());
        System.out.println("------------------------------");
        showPoints();
        showUsedLetters();
        showGuessedLetters();
    }

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

    public void processLetter(Character usedLetter) {
        int newPoints;
        String winnerKey = game.getWinnerKey();
        int turn = game.getTurn();

        game.processLetter(usedLetter);
        this.letterIsPresent = game.isLetterPresent(usedLetter);
        showGuessedLetters();

        if (!this.thereIsAWinner) {
            this.thereIsAWinner = game.isThereAWinner();
        }

        if ((game.getPlayersPoints().get(game.getWinnerKey()) != null) && game.getPlayersPoints().get(game.getWinnerKey()) < game.getTotalPoints()) {
            this.thereIsAWinner = game.isThereAWinner();
        }

        switch (game.getNewPoints()) {
            case -1:
                JOptionPane.showMessageDialog(null, "Has perdido 1 punto porque la letra no se encuentra.");
                break;
            case -3:
                JOptionPane.showMessageDialog(null, "Has perdido 3 puntos porque la letra ya habia sido utilizada.");
                break;
            default:
                JOptionPane.showMessageDialog(null, "Has ganado " + game.getNewPoints()  + " puntos.");
        }

        if (!game.isRoundOver() && letterIsPresent && !game.getIsTurnOver()) {
            System.out.println("ENTRA 1.");
            play();

        } else if ((!game.isRoundOver() && !letterIsPresent && game.getIsTurnOver()) || (!game.isRoundOver() && game.getUsedLetters().contains(usedLetter))) {
            System.out.println("ENTRA 2.");
            game.setNextTurn();
            game.setIsTurnOver(false);
            play();

        } else if (game.isRoundOver() && !this.thereIsAWinner) {
            newPoints = 5;
            game.setPlayersPoints(newPoints, game.getTurn());

            System.out.println("ENTRA...");
            if (game.isThereAWinner()) {
                showPoints();
                JOptionPane.showMessageDialog(null, "Ha ganado el " + game.getWinnerKey());
                showUsedLetters();
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "Has ganado 5 puntos porque adivinaste la palabra.");
                game.setTurn(1);
                game.initializeNewRound();
                game.generateSentenceToGuess();
                this.wordToGuess = game.getSentenceToGuess();
                play();
            }
        } else if (game.isRoundOver() && this.thereIsAWinner) {
            showGuessedLetters();
            System.out.println("ENTRA");

            if (Objects.equals(game.getPlayersPoints().get(winnerKey), game.getPlayersPoints().get("Jugador " + turn))) {
                newPoints = 5;
                game.setPlayersPoints(newPoints, turn);
                JOptionPane.showMessageDialog(null, "Has ganado 5 puntos porque adivinaste la palabra.");
            }

            showPoints();
            showUsedLetters();
            JOptionPane.showMessageDialog(null, "Ha ganado el " + game.getWinnerKey());
            System.exit(0);
            return;
        }
    }

    public void showPoints() {
        pointsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        pointsLabel.setPreferredSize(new Dimension(700, 30));
        pointsLabel.setForeground(Color.BLACK);
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        categoryPanel.add(pointsLabel);

        pointsLabel.setText(game.pointsToString());
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    public void showUsedLetters() {
        System.out.println("USED: " + game.getUsedLetters());
        usedLettersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usedLettersLabel.setText("LETRAS USADAS: " + game.getUsedLetters().toString());
        keyboardContainer.revalidate();
        keyboardContainer.repaint();
    }
}