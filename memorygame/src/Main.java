import javax.swing.*;
import java.awt.*;

class MemoryGame extends JFrame {


    MemoryGame() {
        super("Memory Game");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0f;
        constraints.gridx = 1;

        JPanel erm = new JPanel();
        erm.add(new JLabel("Matches Made:"));
        erm.add(new JLabel("Guesses Made:"));
        erm.add(new JLabel("Time to Guess:"));
        erm.add(new JButton("Reset"));

        contentPane.add(erm, constraints);

        constraints.weighty = 1.0f;

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(4, 4));

        for (int i = 0; i < (4 * 4); i++) {
            GameButton gb = new GameButton(new ImageIcon("images/" + (i + 1) / 2 + ".png"));

            gamePanel.add(gb);
        }

        contentPane.add(gamePanel, constraints);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        //
        MemoryGame memoryGame = new MemoryGame();
    }
}
