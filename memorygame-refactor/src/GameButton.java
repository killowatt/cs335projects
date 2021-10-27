import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class GameButton extends JButton {
    public int cardIndex = 0;

    static ImageIcon defaultIcon = new ImageIcon("images/default.png");

    static ImageIcon[] icons = new ImageIcon[] {
            new ImageIcon("images/0.png"),
            new ImageIcon("images/1.png"),
            new ImageIcon("images/2.png"),
            new ImageIcon("images/3.png"),
            new ImageIcon("images/4.png"),
            new ImageIcon("images/5.png"),
            new ImageIcon("images/6.png"),
            new ImageIcon("images/7.png")
    };

    public void hideCard() {
        setIcon(defaultIcon);
    }

    public void showCard() {
        setIcon(icons[cardIndex]);
    }

    // Called when the card is correctly matched, disables the button
    public void revealCorrect() {
        // Redundantly set the card's icon and disable the button
        setIcon(icons[cardIndex]);
        setEnabled(false);
    }

    // Reset this card back to the hidden state
    public void reset() {
        // Re-enable the button and set the hidden icon
        setEnabled(true);
        setIcon(defaultIcon);
    }

    GameButton() {
        // Call parent constructor, no overlaid text
        super("");

        // Set the button's icon to the unrevealed default one
        setIcon(defaultIcon);

        // Set our preferred size to be a little bigger than our icons
        setPreferredSize(new Dimension(96, 96));
    }
}
