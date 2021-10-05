import javax.swing.*;
import java.awt.*;

public class GameButton extends JButton {
    public boolean isTrap = false;
    boolean isFlagged = false;
    boolean isRevealed = false;

    public int neighborTraps = -1;

    static ImageIcon trapIcon = new ImageIcon("images/trap.png");
    static ImageIcon flagIcon = new ImageIcon("images/flag.png");

    static ImageIcon[] numberIcons = new ImageIcon[]{
            new ImageIcon("images/1.png"),
            new ImageIcon("images/2.png"),
            new ImageIcon("images/3.png"),
            new ImageIcon("images/4.png"),
            new ImageIcon("images/5.png"),
            new ImageIcon("images/6.png"),
            new ImageIcon("images/7.png"),
            new ImageIcon("images/8.png")
    };

    GameButton() {
        super("");

        setPreferredSize(new Dimension(24, 24));
    }

    public void reset() {
        isTrap = false;
        isRevealed = false;
        isFlagged = false;

        setIcon(null);
        setBackground(null);
        setEnabled(true);
    }

    public void reveal() {
        isRevealed = true;
        setBackground(new Color(63, 63, 63, 255));

        if (isTrap)
            setIcon(trapIcon);
        else if (neighborTraps - 1 >= 0)
            setIcon(numberIcons[neighborTraps - 1]);
    }

    public void setTrap(boolean value) {
        isTrap = value;

        if (isTrap)
            setIcon(trapIcon);
        else
            setIcon(null);
    }

    public void toggleFlag() {
        if (isRevealed) return;

        isFlagged = !isFlagged;
        if (isFlagged)
            setIcon(flagIcon);
        else
            setIcon(null);
    }
}