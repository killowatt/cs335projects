import javax.swing.*;
import java.awt.*;

class Keyboard extends JFrame
{
    Keyboard() {
        // Call the parent JFrame constructor to set our window title
        super("Keyboard");

        // Closing the window will close the entire application
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //setLayout(null);

        // Get our frame's content pane and use a grid layout with 5 rows
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(5, 0));

        // Call setup methods for the input panel, keyboard row, and final row
        setupInputPanel(contentPane);
        setupKeyboardRows(contentPane);
        setupFinalRow(contentPane);

        // Auto-fit our window and make it non-resizable, then make it visible
        pack();
        setLocationRelativeTo(null); // Centers the JFrame (window)
        setVisible(true);
    }

    // Sets up our input panel, comprised of the text input as well as attach and send buttons
    void setupInputPanel(Container contentPane) {
        // Create our input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        // Create the text field we'll use for text input, ensuring it can't be edited with hardware keyboard
        JTextField inputTextField = new JTextField("Type a message...", 32);
        inputTextField.setEditable(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;

        // Add our components to the input panel
        inputPanel.add(new JButton("Attach"), constraints);
        inputPanel.add(inputTextField, constraints);
        inputPanel.add( new JButton("Send"), constraints);

        // Add our input panel to the frame
        contentPane.add(inputPanel, BorderLayout.CENTER);
    }

    // Procedurally sets up the keyboard rows, with each character as a button
    void setupKeyboardRows(Container contentPane) {
        // Keyboard row strings, which get split per-character for buttons
        String[] keyboardRows = {
                "QWERTYUIOP",
                "ASDFGHJKL",
                "⬆ZXCVBNM⬅"
        };

        // For each row, create a new panel and make a button from each character
        for (String row : keyboardRows) {
            JPanel rowPanel = new JPanel();

            rowPanel.setLayout(new GridBagLayout());

            // Split this row's string into individual characters, then create buttons from each one
            char[] rowCharacters = row.toCharArray();
            for (char character : rowCharacters) {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.BOTH;
                constraints.weighty = 1.0f;
                constraints.weightx = 1.0f;

                rowPanel.add(new JButton(Character.toString(character)), constraints);
            }

            // Add our new row to the desired container
            contentPane.add(rowPanel);
        }
    }

    // Procedurally sets up the final row, using buttons for space, return, etc.
    void setupFinalRow(Container contentPane) {
        // Button strings for our last keyboard row
        String[] finalRow = {
                "123",
                ":)",
                "Space",
                "Return"
        };

        // Procedurally generate our last row
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;

        // For each string in our final row, create a corresponding button
        for (String text : finalRow) {
            if (text.equals("Space"))
            {
                constraints.weightx = 0.4f;
            }
            else
            {
                constraints.weightx = 0.0f;
            }

            rowPanel.add(new JButton(text), constraints);
        }

        // Add our new row to the desired container
        contentPane.add(rowPanel);
    }
}

public class Main {
    // Our main entry point, responsible for creating our Keyboard JFrame
    public static void main(String[] args) {
        // Attempt to modify the look and feel to match the native platform
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create our keyboard JFrame
        Keyboard keyboard = new Keyboard();
    }
}
