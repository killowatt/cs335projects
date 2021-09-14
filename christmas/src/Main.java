import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

class Christmas extends JFrame
{
    Christmas() {
        // Call the parent JFrame constructor to set our window title
        super("Christmas");

        // Closing the window will close the entire application
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create our JLabel for our button press result and add it to a panel, so text is centered
        JLabel christmasText = new JLabel("Press the button");
        JPanel christmasPanel = new JPanel();

        christmasPanel.add(christmasText);

        // Create our button that checks if it is Christmas yet
        JButton checkButton = new JButton("Is it Christmas yet?");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the current date
                LocalDate date = java.time.LocalDate.now();

                // Check the current date, if the month & date match, its Christmas
                if (date.getMonthValue() == 12 && date.getDayOfMonth() == 25)
                    christmasText.setText("Yes, it is Christmas");
                else
                    christmasText.setText("No, it isn't Christmas");
            }
        });

        // Add our button & panel to the JFrame
        add(checkButton, BorderLayout.CENTER);
        add(christmasPanel, BorderLayout.PAGE_END);

        // Pack our window contents, center the window on screen and make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    // Our main entry point, responsible for creating our Keyboard JFrame
    public static void main(String[] args) {
        // Create our Christmas JFrame
        Christmas christmas = new Christmas();
    }
}
