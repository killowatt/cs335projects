import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

// Main memory game class, handles all game logic
class MemoryGame extends JFrame {
    // Constants for our game
    static final int NUM_CARDS = 8;
    static final int GRID_SIZE = 4;

    // Current score & guesses made
    int score = 0;
    int guesses = 0;

    // The first button pressed in a guess, if any
    GameButton firstButton = null;

    // Our array of instanced game buttons
    ArrayList<GameButton> gameButtons;

    // The score & guesses made labels
    JLabel scoreLabel;
    JLabel guessesLabel;

    // Timer and task for our card reveal countdown
    Timer timer;
    TimerTask timerTask;

    // Our main memory game constructor
    MemoryGame() {
        // Call the parent JFrame constructor with our window's title
        super("Memory Game");

        // Close the application when the exit button is pressed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create our game timer
        timer = new Timer();

        // Get our JFrame content pane and set it to use a grid bag layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());

        // Now create our JPanel for our score, guesses & reset button
        JPanel scorePanel = new JPanel();

        // Create our game score and guesses labels
        scoreLabel = new JLabel();
        guessesLabel = new JLabel();

        // Now add our labels to the panel
        scorePanel.add(scoreLabel);
        scorePanel.add(guessesLabel);

        // Set up our reset button, all it does is call our reset method
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        scorePanel.add(resetButton);

        // Set up our exit button, simply calls System.exit
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // System.exit should clean up allocated resources automatically...
                System.exit(0);
            }
        });
        scorePanel.add(exitButton);

        // Reset our constraints, no padding and make sure our score panel is vertically oriented in our JFrame
        // Set up a grid bag constraints object
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        contentPane.add(scorePanel, constraints);

        // Set up our game panel and set a grid layout, 4 rows and 4 columns
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        // Create our game button array and create two buttons for each card type
        gameButtons = new ArrayList<>();
        for (int i = 0; i < NUM_CARDS; i++) {
            for (int j = 0; j < 2; j++) {
                // Create our game button and assign the card ID
                GameButton button = new GameButton();
                button.cardIndex = i;

                // Set up our button to call our card selected method when clicked
                button.addActionListener(new ActionListener() {
                    @Override
                        public void actionPerformed(ActionEvent e) {
                            onCardSelected(button);
                        }
                    });

                // Add our button to the array of game buttons
                gameButtons.add(button);
            }
        }

        // Shuffle the grid before we add it to the panel
        Collections.shuffle(gameButtons);

        // Add all of our game buttons to the game panel
        for (GameButton button : gameButtons) {
            gamePanel.add(button);
        }

        // Set up our constraints further for the game grid, fill the space
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0f;
        constraints.weighty = 1.0f;

        // Add our game panel to the JFrame content pane
        contentPane.add(gamePanel, constraints);

        // Set our score & guesses to zero, updating the label
        setScore(0);
        setGuesses(0);

        // Pack our layout, center the JFrame on screen, and make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Set the current game score, updating our score label
    void setScore(int value) {
        score = value;
        scoreLabel.setText("Matches Made: " + score);
    }

    // Set the current guesses made, updating our guesses label
    void setGuesses(int value) {
        guesses = value;
        guessesLabel.setText("Guesses Made: " + guesses);
    }

    // Called when a card is selected, the core of our game logic is performed here
    void onCardSelected(GameButton button) {
        // If we have a timer task, that means we selected two cards before
        // If we've clicked again, we need to hide both cards immediately by running that task
        if (timerTask != null) {
            // We cancel the task first, so it isn't re-run later by the timer
            timerTask.cancel();
            timerTask.run();
        }

        // If this button is the same as our first button, ignore this click
        if (button == firstButton)
            return;

        // If this is our first button click, set it as our current first half of our guess
        if (firstButton == null) {
            firstButton = button;

            // Show the card, and set its background to green so we know its our first click
            button.showCard();
            firstButton.setBackground(Color.green);
        }
        // Otherwise, this must be our second button press. Check if the cards match
        else {
            // If the card IDs match, then reveal them both
            if (firstButton.cardIndex == button.cardIndex) {
                // Reveal both of our buttons since we are correct
                firstButton.revealCorrect();
                button.revealCorrect();

                // Increment both our number of guesses and our score
                setScore(score + 1);
                setGuesses(guesses + 1);

                // If our score reaches the max
                if (score >= NUM_CARDS) {
                    JOptionPane.showMessageDialog(this, "You win!");
                }
            }
            // Otherwise, start the hide timers for both buttons
            else {
                // Make sure we reveal the second card
                button.showCard();

                timerTask = new TimerTask() {
                    // Set up a separate reference to the first button, since firstButton may get overwritten
                    final GameButton first = firstButton;

                    @Override
                    public void run() {
                        // When this timer task runs, hide both cards
                        button.hideCard();
                        first.hideCard();

                        timerTask = null;
                    }
                };
                // Schedule the task for 3000ms (3 seconds) from now
                timer.schedule(timerTask, 3000);

                // Increment our number of guesses
                setGuesses(guesses + 1);
            }

            // Reset our first button's background and set our first button to null for our next guess
            firstButton.setBackground(null);
            firstButton = null;
        }
    }

    // Called when our reset button is pressed, resets our game state and shuffles the grid
    void reset() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        // Shuffle our grid, this does not update our layout however
        Collections.shuffle(gameButtons);
        for (GameButton button : gameButtons) {
            button.reset();
        }

        // Now iterate and replace each cards index at the front and back of our button array
        // This achieves a random shuffle though this solution is less clear
        for (int i = 0; i < NUM_CARDS; i++) {
            gameButtons.get(i).cardIndex = i;
            gameButtons.get(gameButtons.size() - i - 1).cardIndex = i;
        }

        // Make sure if we reset while having a card selected that we reset it
        if (firstButton != null) {
            firstButton.setBackground(null);
            firstButton = null;
        }

        // Reset our game score & number of guesses
        setScore(0);
        setGuesses(0);
    }
}

public class Main {
    public static void main(String[] args) {
        // Create our MemoryGame JFrame
        MemoryGame memoryGame = new MemoryGame();
    }
}
