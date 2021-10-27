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

    public void hideCard() {
        setIcon(game.getDefaultIcon());
    }

    // Called when the card is correctly matched, disables the button
    public void revealCorrect() {
        // Redundantly set the card's icon and disable the button
        setIcon(game.getIcons()[cardIndex]);
        setEnabled(false);
    }

    // Reset this card back to the hidden state
    public void reset() {
        // Re-enable the button and set the hidden icon
        setEnabled(true);
        setIcon(game.getDefaultIcon());
    }

    GameButton(MemoryGame memoryGame, int card) {
        // Call parent constructor, no overlaid text
        super("");

        // Set up our card's id and a reference to our game JFrame
        game = memoryGame;
        cardIndex = card;

        // Set the button's icon to the unrevealed default one
        setIcon(game.getDefaultIcon());

        // Set our preferred size to be a little bigger than our icons
        setPreferredSize(new Dimension(96, 96));

        // Set up our button so that when pressed it notifies the game it has been selected
        // We use thisButton since the "this" statement to provide this button instance to the anonymous class
        GameButton thisButton = this;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reveal the card and then notify our game
                setIcon(game.getIcons()[cardIndex]);
                game.onCardSelected(thisButton);
            }
        });
    }
}
