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

    // Our timer and its current task if any
    Timer timer;
    TimerTask task;

    // Starts a timer to hide the card after three seconds
    public void startHide() {
        // Cancel previous undesired timer task
        if (task != null)
            task.cancel();

        // Start a new timer task that when executed will reset the button's icon
        task = new TimerTask() {
            @Override
            public void run() {
                setIcon(game.getDefaultIcon());
                task = null;
            }
        };
        // Schedule the task for 3000ms (3 seconds) from now
        timer.schedule(task, 3000);
    }

    // Called when the card is correctly matched, disables the button
    public void revealCorrect() {
        // Cancel any existing undesired timer task
        if (task != null)
            task.cancel();

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

        // Create our button's timer
        timer = new Timer();

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
