import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

class CalderaWeasel extends JFrame {
    ArrayList<GameButton> gameButtons;

    boolean gameOver = false;

    int gridSize = 8;
    int totalTraps = 12;

    Timer gameTimer;
    TimerTask timerTick;

    int time = 0;

    JLabel timeLabel;

    CalderaWeasel() {
        super("Caldera Weasel");

        setDefaultCloseOperation(EXIT_ON_CLOSE);


        gameTimer = new Timer();
        timerTick = null;

        gameButtons = new ArrayList<>();

        JPanel scorePanel = new JPanel();
        timeLabel = new JLabel("");
        scorePanel.add(timeLabel);
        scorePanel.add(new JLabel("Traps Remaining:"));

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(gridSize, gridSize));

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
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
        difficultySubmenu.add(new JMenuItem("Easy"));
        difficultySubmenu.add(new JMenuItem("Medium"));
        difficultySubmenu.add(new JMenuItem("Hard"));
        difficultySubmenu.add(new JMenuItem("Custom"));
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
        if (x >= gridSize || x < 0 || y >= gridSize || y < 0)
            return null;

        int index = x * gridSize + y;
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

        int index = x * gridSize + y;
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
        boolean[] visited = new boolean[gridSize * gridSize];

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

        for (GameButton b : gameButtons) {
            b.reset();
        }

        setupTraps();
    }

    void setupBoard() {

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

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                int index = x * gridSize + y;

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
