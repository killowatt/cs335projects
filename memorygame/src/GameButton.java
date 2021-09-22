import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameButton extends JButton {
    boolean revealed = false;
    ImageIcon icon;

    GameButton(ImageIcon sicon) {
        super("");

        icon = sicon;

        setPreferredSize(new Dimension(96, 96));

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revealed = !revealed;
                if (revealed)
                    setIcon(icon);
                else
                    setIcon(null);
            }
        });
    }
}
