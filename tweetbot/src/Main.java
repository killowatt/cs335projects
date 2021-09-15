import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Keyboard extends JFrame
{
    // The text field used to display user input
    private JTextField textField;

    // The current user input as a string
    private String currentInput = "";

    // Should the next character should be capitalized
    private boolean usingShift = false;

    // Keyboard constructor, responsible for entire keyboard setup
    Keyboard() {
        // Call the parent JFrame constructor to set our window title
        super("Keyboard");

        // Closing the window will close the entire application
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Get our frame's content pane and use a grid layout with 5 rows
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(5, 0));

        // Call setup methods for the input panel, keyboard row, and final row
        setupInputPanel(contentPane);
        setupKeyboardRows(contentPane);
        setupFinalRow(contentPane);

        // Auto-fit our window, center it on screen, then make it visible.
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method called whenever the input text is modified
    void updateInputField() {
        // If the current input isn't empty, set our field's text to that
        // Otherwise, use a placeholder message when the input is empty
        if (!currentInput.isEmpty())
            textField.setText(currentInput);
        else
            textField.setText("Type a message...");
    }

    // Sets up our input panel, comprised of the text input as well as attach and send buttons
    void setupInputPanel(Container contentPane) {
        // Create our input panel, using a grid bag layout
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        // Common constraints for the input panel
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;

        // Add the "attach" button to the input panel
        inputPanel.add(new JButton("Attach"), constraints);

        // Create the text field we'll use for text input, ensuring it can't be edited with hardware keyboard
        textField = new JTextField("", 32);
        textField.setEditable(false);
        updateInputField();
        inputPanel.add(textField, constraints);

        // Create the send button, which prints the current input to console when clicked
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Print the current input and then clear it for our next message
                System.out.println(currentInput);
                currentInput = "";

                // Update the text field since we've modified the current input
                updateInputField();
            }
        });
        inputPanel.add(sendButton, constraints);

        // Add our input panel to the frame
        contentPane.add(inputPanel, BorderLayout.CENTER);
    }

    // Procedurally sets up the keyboard rows, with each character as a button
    void setupKeyboardRows(Container contentPane) {
        // Keyboard row strings, which get split per-character for buttons
        String[] keyboardRows = {
                "QWERTYUIOP", // Replace?
                "ASDFGHJKL",
                "ZXCVBNM"
        };

        // Common constraints for the keyboard rows
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;

        // For each row, create a new panel and make a button from each character
        for (int i = 0; i < keyboardRows.length; i++) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridBagLayout());

            // If this is the last row, first add the shift button
            if (i == keyboardRows.length - 1) {
                JButton shiftButton = new JButton("⬆");
                shiftButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Simply toggle usingShift's boolean value. The next time a key is pressed, we check
                        // against this boolean to determine capitalization
                        usingShift = !usingShift;
                    }
                });

                rowPanel.add(shiftButton, constraints);
            }

            // Split this row's string into individual characters, then create buttons from each one
            char[] rowCharacters = keyboardRows[i].toCharArray();
            for (char character : rowCharacters) {
                JButton key = new JButton(Character.toString(character));
                key.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Add this key to the end of our input string, either in upper or lower case
                        // based on the current value of usingShift
                        currentInput += usingShift ?
                                Character.toString(character).toUpperCase() :
                                Character.toString(character).toLowerCase();

                        // Reset the usingShift variable and then update the text field
                        usingShift = false;
                        updateInputField();
                    }
                });

                rowPanel.add(key, constraints);
            }

            // If this is the last row, add the backspace button
            if (i == keyboardRows.length - 1) {
                JButton backspaceButton = new JButton("⬅");
                backspaceButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // If the current input is NOT empty, then remove the last character from it
                        if (!currentInput.isEmpty()) {
                            currentInput = currentInput.substring(0, currentInput.length() - 1);
                            updateInputField();
                        }
                    }
                });

                rowPanel.add(backspaceButton, constraints);
            }

            // Add our new row to the desired container
            contentPane.add(rowPanel);
        }
    }

    // Procedurally sets up the final row, using buttons for space, return, etc.
    void setupFinalRow(Container contentPane) {
        // Procedurally generate our last row
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new GridBagLayout());

        // Common constraints for the final row
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0f;

        // Numbers button, non-functional
        rowPanel.add(new JButton("123"), constraints);

        // Emoji button, adds :-) to the input string when clicked
        JButton emojiButton = new JButton("\uD83D\uDE03");
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add ":-)" to the input string then update the text field
                currentInput += ":-)";
                updateInputField();
            }
        });
        rowPanel.add(emojiButton, constraints);

        // Add the smiley button (using unicode delimiter), non-functional
        rowPanel.add(new JButton("\uD83C\uDFA4"), constraints);

        // Add the space button, adds a whitespace to the input string when pressed
        JButton spaceButton = new JButton("Space");
        spaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentInput += " ";
                updateInputField();
            }
        });
        constraints.weightx = 0.4f;
        rowPanel.add(spaceButton, constraints);

        constraints.weightx = 0.0f;
        rowPanel.add(new JButton("Return"), constraints);

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
