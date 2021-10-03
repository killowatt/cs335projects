import javax.swing.*;
import java.awt.*;

public class GameButton extends JButton {
    public boolean isTrap = false;

    GameButton() {
        super("");

        setPreferredSize(new Dimension(20, 20));
    }

    public void reveal() {
        setBackground(new Color(0, 0, 0, 0));
    }

    public void setTrap(boolean value) {
        isTrap = value;
        if (isTrap)
            setIcon(new ImageIcon("images/trap.png"));
        else
            setIcon(null);
    }
}
