import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

class GameDifficulty {
    public int gridSizeX;
    public int gridSizeY;
    public int totalTraps;

    GameDifficulty(int gridSizeX, int gridSizeY, int totalTraps) {
        this.gridSizeX = gridSizeX;
        this.gridSizeY = gridSizeY;
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

    Timer gameTimer;
    TimerTask timerTick;

    JLabel timeLabel;

    CalderaWeasel() {
        super("Caldera Weasel");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        difficulty = GameDifficulty.Intermediate;

        gameTimer = new Timer();
        timerTick = null;

        gameButtons = new ArrayList<>();

        JPanel scorePanel = new JPanel();
        timeLabel = new JLabel("");
        scorePanel.add(timeLabel);
        scorePanel.add(new JLabel("Traps Remaining:"));

        gamePanel = new JPanel();

        setupBoard();

        setupTraps();

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
        JMenuItem menuItem = new JMenuItem("Reset");
        menuItem.addActionListener(new ActionListener() {
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
                //
            }
        });
        difficultySubmenu.add(customItem);


        menu.add(difficultySubmenu);

        menu.addSeparator();

        JMenuItem exitButton = new JMenuItem("Exit");
        menu.add(exitButton);

        menuBar.add(menu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        ImageIcon frameIcon = new ImageIcon("images/flag.png");
        setIconImage(frameIcon.getImage());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    boolean evaluateWin() {
        for (GameButton b : gameButtons) {
            if (!b.isRevealed && !b.isTrap)
                return false;
        }
        return true;
    }

    void onCellClicked(GameButton gameButton, int x, int y) {
        if (gameButton.isTrap) {
            System.out.println("Game over");

            gameButton.setBackground(Color.red);

            gameOver = true;
            for (GameButton b : gameButtons) {
                b.setEnabled(false);
            }
        }

        tryReveal(x, y);
        gameButton.reveal();

        if (evaluateWin()) {
            System.out.println("You win!!");
            for (GameButton b : gameButtons) {
                b.setEnabled(false);
            }
        }
    }

    GameButton getTile(int x, int y) {
        if (x >= gridWidth || x < 0 || y >= gridHeight || y < 0)
            return null;

        int index = x * gridWidth + y;
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

        int index = x * gridWidth + y;
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
        System.out.println("hey");
        gameButton.toggleFlag();
    }

    void reset() {
        gameOver = false;

        gridWidth = difficulty.gridSizeX;
        gridHeight = difficulty.gridSizeY;
        totalTraps = difficulty.totalTraps;

        setupBoard();
        setupTraps();

        pack();
    }

    void setupBoard() {
        gamePanel.removeAll();
        gameButtons.clear();

        gamePanel.setLayout(new GridLayout(gridWidth, gridHeight));

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                GameButton button = new GameButton();

                int finalX = x;
                int finalY = y;
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver) return;

                        if (timerTick == null) {
                            timerTick = new TimerTask() {
                                @Override
                                public void run() {
                                    time++;
                                    timeLabel.setText("Time: " + time);
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

    void setupTraps() {
        int remainingTraps = totalTraps;

        Random random = new Random();
        while (remainingTraps > 0) {
            int index = random.nextInt(gameButtons.size());

            if (!gameButtons.get(index).isTrap) {
                gameButtons.get(index).setTrap(true);
                remainingTraps--;
            }
        }

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int index = x * gridWidth + y;

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
