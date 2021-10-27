import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

class MemoryGame extends JFrame {
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

    // The icons for our buttons, stored here to stop unnecessary file reload during reset
    ImageIcon[] icons;
    ImageIcon defaultIcon;

    Timer timer;
    TimerTask timerTask;

    // Our main memory game constructor
    MemoryGame() {
        // Call the parent JFrame constructor with our window's title
        super("Memory Game");

        // Close the application when the exit button is pressed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        timer = new Timer();

        // Load our default icon
        defaultIcon = new ImageIcon("images/default.png");

        // Load all of our card icons zero through seven
        icons = new ImageIcon[8];
        for (int i = 0; i < 8; i++) {
            icons[i] = new ImageIcon("images/" + i + ".png");
        }

        // Get our JFrame content pane and set it to use a grid bag layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());

        // Now create our JPanel for our score, guesses & reset button
        JPanel scorePanel = new JPanel();

        scoreLabel = new JLabel();
        scorePanel.add(scoreLabel);

        guessesLabel = new JLabel();
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
        gamePanel.setLayout(new GridLayout(4, 4));

        // Create our game button array and create two buttons for each card type
        gameButtons = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            gameButtons.add(new GameButton(this, i));
            gameButtons.add(new GameButton(this, i));
        }
        // Shuffle the grid before we add it to the panel
        Collections.shuffle(gameButtons);

        // Add all of our game buttons to the game panel
        for (int i = 0; i < (4 * 4); i++) {
            gamePanel.add(gameButtons.get(i));
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

    // Returns a reference to all of our icons
    public ImageIcon[] getIcons() {
        return icons;
    }

    // Returns a reference to our default unrevealed icon
    public ImageIcon getDefaultIcon() {
        return defaultIcon;
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
        if (timerTask != null) {
            timerTask.cancel();
            timerTask.run();
            timerTask = null;
        }

        // If this button is the same as our first button, ignore this click
        if (button == firstButton)
            return;

        // If this is our first button click, set it as our current first half of our guess
        if (firstButton == null) {
            firstButton = button;
            // We set the background to green so it's easier to see which card you are currently
            // guessing with
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

                if (score >= 8) {
                    JOptionPane.showMessageDialog(this, "You win!");
                }
            }
            // Otherwise, start the hide timers for both buttons
            else {
                timerTask = new TimerTask() {

                    final GameButton fb = firstButton;

                    @Override
                    public void run() {
                        button.hideCard();
                        fb.hideCard();

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
        // Shuffle our grid, this does not update our layout however
        Collections.shuffle(gameButtons);
        for (GameButton button : gameButtons) {
            button.reset();
        }

        // Now iterate and replace each cards index at the front and back of our button array
        // This achieves a random shuffle though this solution is less clear
        for (int i = 0; i < 8; i++) {
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
