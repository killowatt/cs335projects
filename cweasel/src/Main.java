import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class CalderaWeasel extends JFrame {
    ArrayList<GameButton> gameButtons;

    boolean gameOver = false;

    int gridSize = 8;

    CalderaWeasel() {
        super("Caldera Weasel");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        gameButtons = new ArrayList<>();

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

                        System.out.println(finalX + " " + finalY);

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

        getContentPane().add(gamePanel);

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
        menuBar.add(menu);
        menuBar.add(new JLabel("Timer:"));
        setJMenuBar(menuBar);

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

    boolean isGridTrap(int x, int y) {
        if (x >= gridSize || x < 0 || y >= gridSize || y < 0)
            return false;

        int index = x * gridSize + y;
        if (index >= gameButtons.size() || index < 0)
            return false;

        return gameButtons.get(index).isTrap;
    }

    void tryAdd(Queue<GameButton> queue, boolean[] visited, int x, int y) {
        if (x >= gridSize || x < 0 || y >= gridSize || y < 0)
            return;

        System.out.println("TEST: " + x + " " + y);

        int index = x * gridSize + y;
        if (index >= gameButtons.size() || index < 0)
            return;

        if (visited[index])
            return;

        GameButton button = gameButtons.get(index);

        visited[index] = true;
        queue.add(button);

        if (button.neighborTraps > 0) {
            return;

        }

        tryAdd(queue, visited,x - 1, y - 1);
        tryAdd(queue, visited,x, y - 1);
        tryAdd(queue, visited,x + 1, y - 1);

        tryAdd(queue, visited,x - 1, y);
        tryAdd(queue, visited,x + 1, y);

        tryAdd(queue, visited,x - 1, y + 1);
        tryAdd(queue, visited,x, y + 1);
        tryAdd(queue, visited,x + 1, y + 1);
    }

    void tryReveal(int x, int y) {
        if (x >= gridSize || x < 0 || y >= gridSize || y < 0)
            return;

        int index = x * gridSize + y;
        if (index >= gameButtons.size() || index < 0)
            return;

        GameButton button = gameButtons.get(index);
        if (button.isRevealed)
            return;

        //button.reveal();
        button.setBackground(Color.cyan);

        Queue<GameButton> buttons = new LinkedList<>();
        boolean[] visited = new boolean[gridSize * gridSize];

        buttons.add(button);


        if (button.neighborTraps > 0) {
            button.reveal();
            return;
        }

        //while (buttons.isEmpty()) {
            tryAdd(buttons, visited,x - 1, y - 1);
            tryAdd(buttons, visited,x, y - 1);
            tryAdd(buttons, visited,x + 1, y - 1);

            tryAdd(buttons, visited,x - 1, y);
            tryAdd(buttons, visited,x + 1, y);

            tryAdd(buttons, visited,x - 1, y + 1);
            tryAdd(buttons, visited,x, y + 1);
            tryAdd(buttons, visited,x + 1, y + 1);
        //}

        while(!buttons.isEmpty()) {
            GameButton b = buttons.remove();
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
            b.setEnabled(true);
            b.setTrap(false);

            b.setBackground(null);
        }

        setupTraps();
    }

    void setupBoard() {

    }

    void setupTraps() {
        int numTraps = 12;
        Random random = new Random();
        while (numTraps > 0) {
            int index = random.nextInt(gameButtons.size());
            if (!gameButtons.get(index).isTrap) {
                gameButtons.get(index).setTrap(true);
                numTraps--;
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
