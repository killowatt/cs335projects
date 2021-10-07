import javax.swing.*;
import java.awt.*;

// Game button class that holds a cell's current state
class GameButton extends JButton {
    // Tracks the state of this button, if it's a trap, if it's flagged, if it's revealed
    // I would make getters for these since leaving them public like this can easily lead to programmer error, but
    // it currently is not a problem
    boolean isTrap = false;
    boolean isFlagged = false;
    boolean isRevealed = false;

    // Tracks the number of neighbor traps for this cell
    public int neighborTraps = -1;

    // Static variable that checks if cheat mode is currently enabled for buttons
    public static boolean cheatMode = false;

    // Static instances for our trap and flag icons
    static ImageIcon trapIcon = new ImageIcon("images/trap.png");
    static ImageIcon flagIcon = new ImageIcon("images/flag.png");

    // Static array instance of all of our neighbor trap count icons
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

    // Constructor for the game button, just sets the preferred size and no text
    GameButton() {
        super("");
        setPreferredSize(new Dimension(24, 24));
    }

    // Reveal method, sets up the state for when this button gets revealed
    public void reveal() {
        // Set the revealed flag and set the background color
        isRevealed = true;
        setBackground(new Color(63, 63, 63, 255));

        if (isTrap) // If this was a trap, reveal the trap icon
            setIcon(trapIcon);
        else if (neighborTraps - 1 >= 0) // If this has neighbor traps, set the icon
            setIcon(numberIcons[neighborTraps - 1]);
        else // If nothing else, set the empty (null) icon
            setIcon(null);
    }

    // Set trap method, sets the trap flag and checks the cheat state
    public void setTrap(boolean value) {
        isTrap = value;

        checkCheats();
    }

    // Cheat state check
    public void checkCheats() {
        // Ignore non-traps and flagged spots
        if (!isTrap || isFlagged)
            return;

        // If cheat mode is enabled, always show the trap icon
        if (cheatMode)
            setIcon(trapIcon);
        else
            setIcon(null);
    }

    // Toggle the flagged flag of this button. Returns an integer corresponding to how much the remaining
    // traps counter should be modified
    public int toggleFlag() {
        // If this is already revealed, nothing should change
        if (isRevealed)
            return 0;

        // Toggle the flagged flag
        isFlagged = !isFlagged;
        if (isFlagged)
            setIcon(flagIcon);
        else
            setIcon(null);

        // Check against the cheat state and return -1 or 1 based on the current flagged value
        checkCheats();
        return isFlagged ? -1 : 1;
    }
}
