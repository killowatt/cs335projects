import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.Queue;
import java.util.Timer;
import java.util.*;

class HowToPlay extends JPanel {
    HowToPlay() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Rules"));
        add(new JLabel("The goal of the Caldera Weasel game is to avoid thermal traps."));
        add(new JLabel("You do this by looking at the numbers in each cell."));
        add(new JLabel("This number represents the number of surrounding cells that are traps."));
        add(new JLabel("You win by avoiding all of the traps and revealing every non-trap space!"));
        add(new JLabel(" "));
        add(new JLabel("Reset"));
        add(new JLabel("After a game over, you can start a new game by going to the menu bar and selecting"));
        add(new JLabel("Game > Reset."));
        add(new JLabel(" "));
        add(new JLabel("Cheat Mode"));
        add(new JLabel("Cheat mode allows you to see which spaces are traps by revealing their icons."));
        add(new JLabel("Your first move is always guaranteed to be safe, so they will not appear until after"));
        add(new JLabel("you've made your first move!"));
    }
}

class DifficultyDialog extends JPanel {
    JSpinner widthSpinner;
    JSpinner heightSpinner;
    JSpinner trapsSpinner;

    DifficultyDialog() {
        setLayout(new GridLayout(3, 2));

        SpinnerNumberModel widthModel = new SpinnerNumberModel(8, 4, 32, 1);
        widthSpinner = new JSpinner(widthModel);

        SpinnerNumberModel heightModel = new SpinnerNumberModel(8, 4, 32, 1);
        heightSpinner = new JSpinner(heightModel);

        SpinnerNumberModel trapsModel = new SpinnerNumberModel(14, 1, 32, 1);
        trapsSpinner = new JSpinner(trapsModel);

        add(new JLabel("Grid Width:"));
        add(widthSpinner);

        add(new JLabel("Grid Height:"));
        add(heightSpinner);

        add(new JLabel("Total Traps:"));
        add(trapsSpinner);
    }

    GameDifficulty getDifficulty() {
        try {
            widthSpinner.commitEdit();
            heightSpinner.commitEdit();
            trapsSpinner.commitEdit();
        } catch (ParseException e) {
            System.out.println("Bad difficulty input");
            return GameDifficulty.Intermediate;
        }

        return new GameDifficulty((int) widthSpinner.getValue(),
                (int) heightSpinner.getValue(),
                (int) trapsSpinner.getValue());
    }
}

class GameDifficulty {
    public int gridWidth;
    public int gridHeight;
    public int totalTraps;

    GameDifficulty(int gridWidth, int gridHeight, int totalTraps) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.totalTraps = totalTraps;
    }

    static final GameDifficulty Beginner = new GameDifficulty(4, 4, 5);
    static final GameDifficulty Intermediate = new GameDifficulty(8, 8, 14);
    static final GameDifficulty Expert = new GameDifficulty(15, 15, 60);
}

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

    static final Object[] aboutDialog = {
            "Caldera Weasel project for CS335",
            "Created by William Yates"
    };

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

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Game");
        JMenuItem menuItem = new JMenuItem(new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        menu.add(menuItem);

        JMenu difficultySubmenu = new JMenu("Difficulty");
        difficultySubmenu.setMnemonic(KeyEvent.VK_D);

        JMenuItem beginnerItem = new JMenuItem(new AbstractAction("Beginner") {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = GameDifficulty.Beginner;
            }
        });
        difficultySubmenu.add(beginnerItem);

        JMenuItem intermediateItem = new JMenuItem(new AbstractAction("Intermediate") {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = GameDifficulty.Intermediate;
            }
        });
        difficultySubmenu.add(intermediateItem);

        JMenuItem expertItem = new JMenuItem(new AbstractAction("Expert") {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = GameDifficulty.Expert;
            }
        });
        difficultySubmenu.add(expertItem);

        JMenuItem customItem = new JMenuItem(new AbstractAction("Custom") {
            @Override
            public void actionPerformed(ActionEvent e) {
                DifficultyDialog difficultyDialog = new DifficultyDialog();
                JOptionPane.showInternalMessageDialog(null, difficultyDialog, "Difficulty", JOptionPane.PLAIN_MESSAGE);
                difficulty = difficultyDialog.getDifficulty();
            }
        });
        difficultySubmenu.add(customItem);


        menu.add(difficultySubmenu);

        JCheckBoxMenuItem cheatMenuItem = new JCheckBoxMenuItem("Cheat Mode");
        cheatMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameButton.cheatMode = cheatMenuItem.getState();
                for (GameButton button : gameButtons) {
                    button.checkCheats();
                }
            }
        });
        menu.add(cheatMenuItem);

        menu.addSeparator();

        JMenuItem exitButton = new JMenuItem("Exit");
        menu.add(exitButton);

        menuBar.add(menu);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem howToItem = new JMenuItem(new AbstractAction("How to play") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInternalMessageDialog(null, new HowToPlay(), "How to Play", JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpMenu.add(howToItem);

        JMenuItem aboutItem = new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInternalMessageDialog(null, aboutDialog, "About", JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        ImageIcon frameIcon = new ImageIcon("images/flag.png");
        setIconImage(frameIcon.getImage());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    int getGridIndex(int x, int y) {
        return y * gridWidth + x;
    }

    boolean evaluateWin() {
        for (GameButton b : gameButtons) {
            if (!b.isRevealed && !b.isTrap)
                return false;
        }
        return true;
    }

    void onCellClicked(GameButton gameButton, int x, int y) {
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
                    JOptionPane.PLAIN_MESSAGE);

            gameButton.setBackground(new Color(255, 0, 0, 255)); // ?? not work

            gameOver = true;
            for (GameButton b : gameButtons) {
                b.setEnabled(false);
            }
        }

        tryReveal(x, y);
        gameButton.reveal();

        if (evaluateWin()) {
            if (timerTick != null) {
                timerTick.cancel();
                timerTick = null;
            }

            JOptionPane.showInternalMessageDialog(null, "You win!", "Game Over",
                    JOptionPane.PLAIN_MESSAGE);

            for (GameButton b : gameButtons) {
                b.setEnabled(false);
            }
        }
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

    void onCellFlagged(GameButton gameButton) {
        int flagged = gameButton.toggleFlag();

        trapsRemaining += flagged;
        updateTrapsLabel();
    }

    void updateTrapsLabel() {
        trapsLabel.setText("Traps remaining: " + trapsRemaining);
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
        //setupTraps();

        pack();
    }

    void updateTimerLabel() {
        timeLabel.setText("Time: " + time);
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
                //button.setBackground(new Color(x * (255 / gridWidth), y * (255 / gridHeight), 0, 255));
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
}

public class Main {
    public static void main(String[] args) {
        CalderaWeasel calderaWeasel = new CalderaWeasel();
    }
}
