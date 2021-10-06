import javax.swing.*;

// Instructions panel class, it's just a JPanel with lots of text
public class InstructionsPanel extends JPanel {
    InstructionsPanel() {
        // Use a box layout that expands along the Y axis
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Basic Rules
        add(new JLabel("Rules"));
        add(new JLabel("The goal of the Caldera Weasel game is to avoid thermal traps."));
        add(new JLabel("You do this by looking at the numbers in each cell."));
        add(new JLabel("This number represents the number of surrounding cells that are traps."));
        add(new JLabel("You win by avoiding all of the traps and revealing every non-trap space!"));

        add(new JLabel(" "));

        // Game Reset
        add(new JLabel("Reset"));
        add(new JLabel("After a game over, you can start a new game by going to the menu bar and selecting"));
        add(new JLabel("Game > Reset."));

        add(new JLabel(" "));

        // Game Difficulty
        add(new JLabel("Difficulty"));
        add(new JLabel("There are several difficulty settings to choose from."));
        add(new JLabel("Beginner - 4 x 4 grid with 5 traps"));
        add(new JLabel("Intermediate - 8 x 8 grid with 14 traps"));
        add(new JLabel("Expert - 15 x 15 grid with 60 traps"));
        add(new JLabel("Custom - Your own settings, up to 32 x 32 with 512 traps"));
        add(new JLabel("Note: Difficulty only applies once a new game is started."));

        add(new JLabel(" "));

        // Cheat Mode
        add(new JLabel("Cheat Mode"));
        add(new JLabel("Cheat mode allows you to see which spaces are traps by revealing their icons."));
        add(new JLabel("Your first move is always guaranteed to be safe, so they will not appear until after"));
        add(new JLabel("you've made your first move!"));
    }
}
