import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

class CalderaWeasel extends JFrame {
    ArrayList<GameButton> gameButtons;

    boolean gameOver = false;

    int gridSize = 8;

    boolean isGridTrap(int x, int y) {
        int index = x * gridSize + y;
        if (index >= gameButtons.size() || index < 0)
            return false;

        return gameButtons.get(index).isTrap;
    }

    CalderaWeasel() {
        super("Caldera Weasel");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        gameButtons = new ArrayList<>();

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(gridSize, gridSize));

        for (int i = 0; i < gridSize * gridSize; i++) {
            GameButton button = new GameButton();

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameOver) return;

                    if (SwingUtilities.isLeftMouseButton(e))
                        onCellClicked(button);
                    else if (SwingUtilities.isRightMouseButton(e))
                        onCellFlagged(button);
                }
            });

            gameButtons.add(button);
            gamePanel.add(button);
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

    void onCellClicked(GameButton gameButton) {
        if (gameButton.isTrap) {
            System.out.println("Game over");

            gameOver = true;
            for (GameButton b : gameButtons) {
                b.setEnabled(false);
            }
        }

        gameButton.reveal();
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
        int numTraps = 24;
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
