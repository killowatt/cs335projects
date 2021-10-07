import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.Timer;
import java.util.*;

// Assumptions / Extras
// *    This does implement the flag feature with a counter
// *    The spec says we only use square grids, but this supports rectangular grids
// *    This also has a cheat mode that reveals all traps once a move has been made
// *    This has an extra "about" screen under the help menu bar sub-menu

// Known issues
// *    Sometimes button clicks can seem unresponsive, but this is because of subtle dragging of the mouse.
//      I am considering this a feature however since it can help the player avoid unwanted clicks.
// *    Extremely rarely as the timer increments the layout of the board can get stretched due to resizing.
//      This doesn't stop gameplay and the layout can be returned to normal by resizing the window.
// *    Although the first guess is always guaranteed to be a safe one, you can still be put into a situation
//      where you immediately have to make another random guess anyway. I assume this is OK since that goes beyond
//      what the spec says is necessary
// *    When a flagged cell is revealed and it isn't a mine, the traps remaining counter is not updated

// Main game class responsible for running game logic
class CalderaWeasel extends JFrame {
    // Tracks the game over state
    boolean gameOver = false;

    // The current grid width & height in cells
    int gridWidth;
    int gridHeight;

    // The current total number of traps that should be on the board
    int totalTraps;

    // The game difficulty set by the player; above values are set to the internal values of this on reset
    // Set to intermediate by default
    GameDifficulty difficulty = GameDifficulty.Intermediate;

    // Our array of buttons we use for easy access to the game board, one dimensional
    // They belong to the gamePanel which we sometimes need to clear of old buttons
    ArrayList<GameButton> gameButtons;
    JPanel gamePanel;

    // Ongoing values for the current time and traps remaining based on flags
    int time = 0;
    int trapsRemaining = 0;

    // Labels for the game time and traps remaining
    JLabel timeLabel;
    JLabel trapsLabel;

    // The timer we use to increment the time, and it's corresponding task
    Timer gameTimer;
    TimerTask timerTick;

    // Tracks whether a move has been made this game, to generate traps once for the first move
    boolean moveMade = false;

    // Constructor for our game, sets up the layout and initial game state
    CalderaWeasel() {
        // Set the title of our game window
        super("Caldera Weasel");

        // The software should close when the close button is clicked
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create our timer, but no starting task
        gameTimer = new Timer();
        timerTick = null;

        // Create our array of buttons we use all over the place
        gameButtons = new ArrayList<>();

        // Create our score panel which contains our time & remaining traps labels
        JPanel scorePanel = new JPanel();
        timeLabel = new JLabel("");
        trapsLabel = new JLabel("");
        scorePanel.add(timeLabel);
        scorePanel.add(trapsLabel);

        // Create our main game panel which contains our grid of buttons
        gamePanel = new JPanel();

        // "Reset" the game, which will automatically set up our remaining state
        reset();

        // Set up our game layout using a grid bag layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());

        // Starting constraints for the score panel
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 0.0f;
        constraints.gridx = 0;

        // Add our score panel
        contentPane.add(scorePanel, constraints);

        // Ensure our game panel takes up remaining vertical space, and add it
        constraints.weighty = 1.0f;
        getContentPane().add(gamePanel, constraints);

        // Set up our menu bar, kept outside this class for cleanliness
        GameMenu.setupGameMenu(this);

        // Set this frame's icon to our flag sprite
        ImageIcon frameIcon = new ImageIcon("images/flag.png");
        setIconImage(frameIcon.getImage());

        // Pack our window contents, center it on screen, then make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Gets a one dimensional index into our buttons array given a two-dimensional point
    int getGridIndex(int x, int y) {
        return y * gridWidth + x;
    }

    // Gets a tile from our array of game buttons given its two-dimensional coordinate
    GameButton getTile(int x, int y) {
        if (x >= gridWidth || x < 0 || y >= gridHeight || y < 0)
            return null;

        int index = getGridIndex(x, y);
        if (index >= gameButtons.size() || index < 0)
            return null;

        return gameButtons.get(index);
    }

    // Helper method to check if a specific cell is a trap
    boolean isCellTrap(int x, int y) {
        GameButton button = getTile(x, y);
        if (button == null)
            return false;

        return button.isTrap;
    }

    // Helper method to evaluate whether we have reached winning game state
    boolean evaluateWin() {
        for (GameButton b : gameButtons) {
            if (!b.isRevealed && !b.isTrap)
                return false;
        }
        return true;
    }

    // Helper method to set up our game board of buttons
    void setupBoard() {
        // Clear our panel and array of any existing buttons
        gamePanel.removeAll();
        gameButtons.clear();

        // Update the layout just in case the grid size has changed
        gamePanel.setLayout(new GridLayout(gridHeight, gridWidth));

        // Going row first, create all of our game buttons and add them to our game panel
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                GameButton button = new GameButton();

                // Necessary for our anonymous class
                int finalX = x;
                int finalY = y;

                // Set up our button click event
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Ignore clicks when the game is over
                        if (gameOver)
                            return;

                        // If we haven't started a timer, start one!
                        if (timerTick == null) {
                            time = -1; // Hack to ensure the first second from zero to one runs
                            timerTick = new TimerTask() {
                                @Override
                                public void run() {
                                    // Each time this task runs time is incremented by one
                                    // We also update the corresponding label
                                    time++;
                                    updateTimerLabel();
                                }
                            };
                            // Schedule this task for every 1000ms, one second
                            gameTimer.schedule(timerTick, 0, 1000);
                        }

                        // If this was a left click, evaluate it. If it was a right click, try to flag
                        if (SwingUtilities.isLeftMouseButton(e))
                            onCellClicked(button, finalX, finalY);
                        else if (SwingUtilities.isRightMouseButton(e))
                            onCellFlagged(button);
                    }
                });

                // Add this new button to our game panel as well as our array of buttons
                gameButtons.add(button);
                gamePanel.add(button);
            }
        }
    }

    // Helper method to set up traps on the game board
    void setupTraps(int avoidX, int avoidY) {
        // Make sure our total traps doesn't exceed the possible total game area
        int gridArea = gridWidth * gridHeight;
        if (totalTraps >= gridArea)
            totalTraps = gridArea - 1;

        // Remaining traps we have left to place
        int remainingTraps = totalTraps;

        // The index of the cell we just clicked, and should avoid placing a trap on
        int avoidIndex = getGridIndex(avoidX, avoidY);

        // Create a new random object and start trying to place traps
        Random random = new Random();
        while (remainingTraps > 0) {
            // Get an integer in the range of our game buttons, if it matches our avoid index
            // just skip it
            int index = random.nextInt(gameButtons.size());
            if (index == avoidIndex)
                continue;

            // If this button isn't a trap already, make it a trap and decrement our remaining traps needed
            if (!gameButtons.get(index).isTrap) {
                gameButtons.get(index).setTrap(true);
                remainingTraps--;
            }
        }

        // Calculate for each cell the number of neighbors that are traps
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                // If this tile is a trap, we can just ignore it
                if (getTile(x, y).isTrap)
                    continue;

                // For each neighboring cell, if it is a trap, increment our total neighboring trap count
                int count = 0;
                if (isCellTrap(x - 1, y - 1)) count++;
                if (isCellTrap(x, y - 1)) count++;
                if (isCellTrap(x + 1, y - 1)) count++;

                if (isCellTrap(x - 1, y)) count++;
                if (isCellTrap(x + 1, y)) count++;

                if (isCellTrap(x - 1, y + 1)) count++;
                if (isCellTrap(x, y + 1)) count++;
                if (isCellTrap(x + 1, y + 1)) count++;

                // Set the tile's trap count
                getTile(x, y).neighborTraps = count;
            }
        }
    }

    // Helper method to reset the game
    void reset() {
        // Reset our game over & first move states
        gameOver = false;
        moveMade = false;

        // Replace our grid width, height and trap count with that currently set by difficulty settings
        gridWidth = difficulty.gridWidth;
        gridHeight = difficulty.gridHeight;
        totalTraps = difficulty.totalTraps;

        // If a timer is running, stop it
        if (timerTick != null) {
            timerTick.cancel();
            timerTick = null;
        }

        // Reset our game timer and its label
        time = 0;
        updateTimerLabel();

        // Reset our traps remaining count and its label
        trapsRemaining = totalTraps;
        updateTrapsLabel();

        // Set up the game board for a new game
        setupBoard();

        // Automatically resize the JFrame
        pack();
    }

    // Called whenever a cell is left-clicked, evaluates what should happen
    void onCellClicked(GameButton gameButton, int x, int y) {
        // If this button is flagged, just ignore the click
        if (gameButton.isFlagged)
            return;

        // Has a move been made yet? If not, set up traps everywhere but where we just clicked
        if (!moveMade) {
            setupTraps(x, y);
            moveMade = true;
        }

        // If this button was a trap, start the game over sequence
        if (gameButton.isTrap) {
            // First we must end the running timer
            if (timerTick != null) {
                timerTick.cancel();
                timerTick = null;
            }

            // Next we notify the user that the game is over, which will halt execution until they click OK
            JOptionPane.showInternalMessageDialog(null, "You lose :(", "Game Over",
                    JOptionPane.ERROR_MESSAGE);

            // Set the game over state to true and reveal the board, coloring the problem cell red
            gameOver = true;
            for (GameButton b : gameButtons) {
                b.reveal();
                b.setEnabled(false);
            }
            gameButton.setBackground(Color.red);

            return; // Don't win after all things are revealed!
        }

        // If it wasn't a trap, reveal that cell and its neighbors where possible
        tryReveal(x, y);

        // After this move is complete, check if the game has been won
        if (evaluateWin()) {
            // If the game was won, stop the game timer
            if (timerTick != null) {
                timerTick.cancel();
                timerTick = null;
            }

            // Then, notify the user that they have won
            JOptionPane.showInternalMessageDialog(null, "You win!", "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);

            // Finally, reveal the entire board and disable it
            for (GameButton b : gameButtons) {
                b.reveal();
                b.setEnabled(false);
            }
        }
    }

    // Called when a cell is right-clicked, we want to flag this cell
    void onCellFlagged(GameButton gameButton) {
        // GameButton returns 1 if we add a flag, -1 if we remove, and 0 if we can't toggle the state
        int flagged = gameButton.toggleFlag();

        // Based on this value we update the traps remaining counter and its label
        trapsRemaining += flagged;
        updateTrapsLabel();
    }

    // Try to reveal the neighbors of the cell at specific x y coordinates
    void tryReveal(int x, int y) {
        // Try to get the button at x and y, if we get null then we must be out of bounds
        GameButton button = getTile(x, y);
        if (button == null)
            return;

        // If this button is already revealed, skip
        if (button.isRevealed)
            return;

        // Create a queue of buttons we need to check as well as an array of visited cells
        Queue<GameButton> queue = new LinkedList<>();
        boolean[] visited = new boolean[gridWidth * gridHeight];

        // Try to add this cell to the queue using our recursive method
        tryAddToRevealQueue(queue, visited, x, y);

        // Once recursion is complete, run through the queue
        while (!queue.isEmpty()) {
            GameButton b = queue.remove();
            b.reveal();
        }
    }

    // Recursive method that checks a cell's neighbors to see if they should be revealed, as well as the neighbor's
    // neighbors, etc.
    void tryAddToRevealQueue(Queue<GameButton> queue, boolean[] visited, int x, int y) {
        // Get the game button at the specified x and y coordinates if possible
        GameButton button = getTile(x, y);
        if (button == null)
            return;

        // Get the 1d index of the same button
        int index = getGridIndex(x, y);
        if (visited[index])
            return;

        // Make sure we mark this index as visited and add it to the queue
        visited[index] = true;
        queue.add(button);

        // If this button has neighboring traps, then we do not add its neighbors
        if (button.neighborTraps > 0) {
            return;
        }

        // If this button doesn't have neighboring traps, we add all of its neighbors
        tryAddToRevealQueue(queue, visited, x - 1, y - 1);
        tryAddToRevealQueue(queue, visited, x, y - 1);
        tryAddToRevealQueue(queue, visited, x + 1, y - 1);

        tryAddToRevealQueue(queue, visited, x - 1, y);
        tryAddToRevealQueue(queue, visited, x + 1, y);

        tryAddToRevealQueue(queue, visited, x - 1, y + 1);
        tryAddToRevealQueue(queue, visited, x, y + 1);
        tryAddToRevealQueue(queue, visited, x + 1, y + 1);
    }

    // Helper method that updates the timer label in a consistent way
    void updateTimerLabel() {
        timeLabel.setText("Time: " + time);
    }

    // Helper method that updates the traps remaining label in a consistent way
    void updateTrapsLabel() {
        trapsLabel.setText("Traps remaining: " + trapsRemaining);
    }
}

public class Main {
    public static void main(String[] args) {
        // Create an instance of our game, automatically sets up game state and shows on screen
        CalderaWeasel calderaWeasel = new CalderaWeasel();
    }
}
