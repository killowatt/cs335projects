import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

class CalderaWeasel extends JFrame {
    ArrayList<GameButton> gameButtons;

    CalderaWeasel() {
        super("Caldera Weasel");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        gameButtons = new ArrayList<>();

        int gridSize = 8;

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(gridSize, gridSize));

        for (int i = 0; i < gridSize * gridSize; i++) {
            GameButton button = new GameButton();

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onButtonClick(button);
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

    void onButtonClick(GameButton gameButton) {
        if (gameButton.isTrap) {
            System.out.println("Game over");
            for (GameButton b : gameButtons) {
                b.setEnabled(false);
            }
        }
    }

    void reset() {
        for (GameButton b : gameButtons) {
            b.setEnabled(true);
            b.setTrap(false);
        }

        setupTraps();
    }

    void setupBoard() {

    }

    void setupTraps() {
        int numTraps = 6;
        Random random = new Random();
        while (numTraps > 0) {
            int index = random.nextInt(gameButtons.size());
            if (!gameButtons.get(index).isTrap) {
                gameButtons.get(index).setTrap(true);
                numTraps--;
            }
        }
    }

}

public class Main {
    public static void main(String[] args) {
        CalderaWeasel calderaWeasel = new CalderaWeasel();
    }
}
