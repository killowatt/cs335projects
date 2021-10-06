import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

class GameDifficulty {
    public int gridWidth;
    public int gridHeight;
    public int totalTraps;

    GameDifficulty(int gridWidth, int gridHeight, int totalTraps) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.totalTraps = totalTraps;
    }

    static final GameDifficulty Beginner = new GameDifficulty(4, 4, 5);
    static final GameDifficulty Intermediate = new GameDifficulty(8, 8, 14);
    static final GameDifficulty Expert = new GameDifficulty(15, 15, 60);
}

class DifficultyDialog extends JPanel {
    JSpinner widthSpinner;
    JSpinner heightSpinner;
    JSpinner trapsSpinner;

    DifficultyDialog() {
        setLayout(new GridLayout(3, 2));

        SpinnerNumberModel widthModel = new SpinnerNumberModel(8, 4, 32, 1);
        widthSpinner = new JSpinner(widthModel);

        SpinnerNumberModel heightModel = new SpinnerNumberModel(8, 4, 32, 1);
        heightSpinner = new JSpinner(heightModel);

        SpinnerNumberModel trapsModel = new SpinnerNumberModel(14, 1, 512, 1);
        trapsSpinner = new JSpinner(trapsModel);

        add(new JLabel("Grid Width:"));
        add(widthSpinner);

        add(new JLabel("Grid Height:"));
        add(heightSpinner);

        add(new JLabel("Total Traps:"));
        add(trapsSpinner);
    }

    GameDifficulty getDifficulty() {
        try {
            widthSpinner.commitEdit();
            heightSpinner.commitEdit();
            trapsSpinner.commitEdit();
        } catch (ParseException e) {
            System.out.println("Bad difficulty input");
            return GameDifficulty.Intermediate;
        }

        return new GameDifficulty((int) widthSpinner.getValue(),
                (int) heightSpinner.getValue(),
                (int) trapsSpinner.getValue());
    }
}