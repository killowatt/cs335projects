import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

// Class that encapsulates a game difficulty setting. Contains our default settings and can be made custom
class GameDifficulty {
    public int gridWidth;
    public int gridHeight;
    public int totalTraps;

    // Constructor, simply sets the above values
    GameDifficulty(int gridWidth, int gridHeight, int totalTraps) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.totalTraps = totalTraps;
    }

    // Static instances for each of our default game difficulty settings
    static final GameDifficulty Beginner = new GameDifficulty(4, 4, 5);
    static final GameDifficulty Intermediate = new GameDifficulty(8, 8, 14);
    static final GameDifficulty Expert = new GameDifficulty(15, 15, 60);
}

// Difficulty panel we create for setting up a custom difficulty
class DifficultyPanel extends JPanel {
    // Spinner fields for our game parameters
    JSpinner widthSpinner;
    JSpinner heightSpinner;
    JSpinner trapsSpinner;

    // Constructor, sets up this panel's elements and layout
    DifficultyPanel() {
        // Use a grid layout
        setLayout(new GridLayout(3, 2));

        // Set up spinners for width, height and total traps using reasonable minimum and maximums
        SpinnerNumberModel widthModel = new SpinnerNumberModel(8, 4, 32, 1);
        widthSpinner = new JSpinner(widthModel);

        SpinnerNumberModel heightModel = new SpinnerNumberModel(8, 4, 32, 1);
        heightSpinner = new JSpinner(heightModel);

        SpinnerNumberModel trapsModel = new SpinnerNumberModel(14, 1, 512, 1);
        trapsSpinner = new JSpinner(trapsModel);

        // Add these spinners and corresponding labels to the panel
        add(new JLabel("Grid Width:"));
        add(widthSpinner);

        add(new JLabel("Grid Height:"));
        add(heightSpinner);

        add(new JLabel("Total Traps:"));
        add(trapsSpinner);
    }

    // Helper method that gets the resulting difficulty object from the panel's input
    GameDifficulty getDifficulty() {
        // Try to "commit" all the spinner entries
        try {
            widthSpinner.commitEdit();
            heightSpinner.commitEdit();
            trapsSpinner.commitEdit();
        } catch (ParseException e) {
            // If we fail to commit these entries, just return a default difficulty setting and error to console
            System.out.println("Bad difficulty input");
            return GameDifficulty.Intermediate;
        }

        // Return a new game difficulty object, casting internal spinner values to integer
        return new GameDifficulty((int) widthSpinner.getValue(),
                (int) heightSpinner.getValue(),
                (int) trapsSpinner.getValue());
    }
}