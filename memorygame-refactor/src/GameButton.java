import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class GameButton extends JButton {
    // A reference to our game JFrame and this button's card id
    MemoryGame game;
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

    GameButton(MemoryGame memoryGame, int card) {
        // Call parent constructor, no overlaid text
        super("");

        // Set up our card's id and a reference to our game JFrame
        game = memoryGame;
        cardIndex = card;

        // Set the button's icon to the unrevealed default one
        setIcon(defaultIcon);

        // Set our preferred size to be a little bigger than our icons
        setPreferredSize(new Dimension(96, 96));

        // Set up our button so that when pressed it notifies the game it has been selected
        // We use thisButton since the "this" statement to provide this button instance to the anonymous class
        GameButton thisButton = this;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reveal the card and then notify our game
                setIcon(icons[cardIndex]);
                game.onCardSelected(thisButton);
            }
        });
    }
}
