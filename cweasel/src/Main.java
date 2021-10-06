import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.Queue;
import java.util.Timer;
import java.util.*;

class CalderaWeasel extends JFrame {
    boolean gameOver = false;

    int gridWidth = 8;
    int gridHeight = 8;
    int totalTraps = 14;

    GameDifficulty difficulty;

    ArrayList<GameButton> gameButtons;
    JPanel gamePanel;

    int time = 0;
    int trapsRemaining = 0;

    Timer gameTimer;
    TimerTask timerTick;

    boolean moveMade = false;

    JLabel timeLabel;
    JLabel trapsLabel;

    CalderaWeasel() {
        super("Caldera Weasel");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        difficulty = GameDifficulty.Intermediate;

        gameTimer = new Timer();
        timerTick = null;

        gameButtons = new ArrayList<>();

        JPanel scorePanel = new JPanel();
        timeLabel = new JLabel("");
        trapsLabel = new JLabel("");
        scorePanel.add(timeLabel);
        scorePanel.add(trapsLabel);

        updateTimerLabel();

        gamePanel = new JPanel();

        reset();

        Container contentPane = getContentPane();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 0.0f;
        constraints.gridx = 0;
        contentPane.setLayout(new GridBagLayout());

        getContentPane().add(scorePanel, constraints);

        constraints.weighty = 1.0f;
        getContentPane().add(gamePanel, constraints);

        GameMenu.setupGameMenu(this);

        ImageIcon frameIcon = new ImageIcon("images/flag.png");
        setIconImage(frameIcon.getImage());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    int getGridIndex(int x, int y) {
        return y * gridWidth + x;
    }

    GameButton getTile(int x, int y) {
        if (x >= gridWidth || x < 0 || y >= gridHeight || y < 0)
            return null;

        int index = getGridIndex(x, y);
        if (index >= gameButtons.size() || index < 0)
            return null;

        return gameButtons.get(index);
    }

    boolean isGridTrap(int x, int y) {
        GameButton button = getTile(x, y);
        if (button == null) return false;

        return button.isTrap;
    }

    boolean evaluateWin() {
        for (GameButton b : gameButtons) {
            if (!b.isRevealed && !b.isTrap)
                return false;
        }
        return true;
    }

    void setupBoard() {
        gamePanel.removeAll();
        gameButtons.clear();

        gamePanel.setLayout(new GridLayout(gridHeight, gridWidth));

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                GameButton button = new GameButton();

                int finalX = x;
                int finalY = y;
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver) return;

                        if (timerTick == null) {
                            time = -1; // hack
                            timerTick = new TimerTask() {
                                @Override
                                public void run() {
                                    time++;
                                    updateTimerLabel();
                                }
                            };
                            gameTimer.schedule(timerTick, 0, 1000);
                        }

                        if (SwingUtilities.isLeftMouseButton(e))
                            onCellClicked(button, finalX, finalY);
                        else if (SwingUtilities.isRightMouseButton(e))
                            onCellFlagged(button);
                    }
                });

                gameButtons.add(button);
                gamePanel.add(button);
            }
        }
    }

    void setupTraps(int avoidX, int avoidY) {
        int gridArea = gridWidth * gridHeight;
        if (totalTraps >= gridArea)
            totalTraps = gridArea - 1;

        int remainingTraps = totalTraps;

        int avoidIndex = getGridIndex(avoidX, avoidY);

        Random random = new Random();
        while (remainingTraps > 0) {
            int index = random.nextInt(gameButtons.size());

            if (index == avoidIndex)
                continue;

            if (!gameButtons.get(index).isTrap) {
                gameButtons.get(index).setTrap(true);
                remainingTraps--;
            }
        }

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int index = getGridIndex(x, y);

                int count = 0;
                if (isGridTrap(x - 1, y - 1)) count++;
                if (isGridTrap(x, y - 1)) count++;
                if (isGridTrap(x + 1, y - 1)) count++;

                if (isGridTrap(x - 1, y)) count++;
                if (isGridTrap(x + 1, y)) count++;

                if (isGridTrap(x - 1, y + 1)) count++;
                if (isGridTrap(x, y + 1)) count++;
                if (isGridTrap(x + 1, y + 1)) count++;

                gameButtons.get(index).neighborTraps = count;
            }
        }
    }

    void reset() {
        gameOver = false;
        moveMade = false;

        gridWidth = difficulty.gridWidth;
        gridHeight = difficulty.gridHeight;
        totalTraps = difficulty.totalTraps;

        if (timerTick != null) {
            timerTick.cancel();
            timerTick = null;
        }
        time = 0;
        updateTimerLabel();

        trapsRemaining = totalTraps;
        updateTrapsLabel();

        setupBoard();

        pack();
    }

    void onCellClicked(GameButton gameButton, int x, int y) {
        if (gameButton.isFlagged)
            return;

        if (!moveMade) {
            setupTraps(x, y);
            moveMade = true;
        }

        if (gameButton.isTrap) {
            if (timerTick != null) {
                timerTick.cancel();
                timerTick = null;
            }

            JOptionPane.showInternalMessageDialog(null, "You lose :(", "Game Over",
                    JOptionPane.ERROR_MESSAGE);

            gameOver = true;
            for (GameButton b : gameButtons) {
                b.reveal();
                b.setEnabled(false);
            }
            gameButton.setBackground(Color.red);

            return; // Don't win after all things are revealed!
        }

        tryReveal(x, y);
        gameButton.reveal();

        if (evaluateWin()) {
            if (timerTick != null) {
                timerTick.cancel();
                timerTick = null;
            }

            JOptionPane.showInternalMessageDialog(null, "You win!", "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);

            for (GameButton b : gameButtons) {
                b.reveal();
                b.setEnabled(false);
            }
        }
    }

    void onCellFlagged(GameButton gameButton) {
        int flagged = gameButton.toggleFlag();

        trapsRemaining += flagged;
        updateTrapsLabel();
    }

    void tryReveal(int x, int y) {
        GameButton button = getTile(x, y);
        if (button == null)
            return;

        if (button.isRevealed)
            return;

        Queue<GameButton> queue = new LinkedList<>();
        boolean[] visited = new boolean[gridWidth * gridHeight];

        tryAddToRevealQueue(queue, visited, x, y);

        while (!queue.isEmpty()) {
            GameButton b = queue.remove();
            b.reveal();
        }
    }

    void tryAddToRevealQueue(Queue<GameButton> queue, boolean[] visited, int x, int y) {
        GameButton button = getTile(x, y);
        if (button == null)
            return;

        int index = getGridIndex(x, y);
        if (visited[index])
            return;

        visited[index] = true;
        queue.add(button);

        if (button.neighborTraps > 0) {
            return;
        }

        tryAddToRevealQueue(queue, visited, x - 1, y - 1);
        tryAddToRevealQueue(queue, visited, x, y - 1);
        tryAddToRevealQueue(queue, visited, x + 1, y - 1);

        tryAddToRevealQueue(queue, visited, x - 1, y);
        tryAddToRevealQueue(queue, visited, x + 1, y);

        tryAddToRevealQueue(queue, visited, x - 1, y + 1);
        tryAddToRevealQueue(queue, visited, x, y + 1);
        tryAddToRevealQueue(queue, visited, x + 1, y + 1);
    }

    void updateTrapsLabel() {
        trapsLabel.setText("Traps remaining: " + trapsRemaining);
    }

    void updateTimerLabel() {
        timeLabel.setText("Time: " + time);
    }
}

public class Main {
    public static void main(String[] args) {
        CalderaWeasel calderaWeasel = new CalderaWeasel();
    }
}
