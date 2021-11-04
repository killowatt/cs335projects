import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/* Assumptions & Known Issues
 * Dragging the slider around, especially with larger images and larger radius values, can be performance heavy
 * The standard deviation value has little effect especially at high radius values, spec only says 1.0 - 3.0 for stddev
 */

// Main GaussianBlur JFrame, handles user controls and layout
class GaussianBlur extends JFrame {
    GaussianBlur() {
        // Title our window "Gaussian Blur"
        super("Gaussian Blur");

        // When we click the close button, exit the application
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create our menu bar and corresponding menus and buttons
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openFileButton = new JMenuItem("Open");
        JSeparator fileSeparator = new JSeparator();
        JMenuItem quitButton = new JMenuItem("Quit");

        // Add our file menu buttons to the file menu
        fileMenu.add(openFileButton);
        fileMenu.add(fileSeparator);
        fileMenu.add(quitButton);

        // Add our file menu to the menu bar
        menuBar.add(fileMenu);

        // Set our JFrame's menu bar to the one we just created
        setJMenuBar(menuBar);

        // Create our user control panel and corresponding widgets
        JPanel controlPanel = new JPanel();

        JLabel radiusLabel = new JLabel("Radius");
        JSlider blurSlider = new JSlider();

        JLabel deviationLabel = new JLabel("Std Dev");
        JSlider deviationSlider = new JSlider();

        JButton resetButton = new JButton("Reset");

        // Fix our labels to be aligned on the center
        radiusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        radiusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        // Create our gaussian blur radius panel, label and slider
        JPanel radiusPanel = new JPanel();
        radiusPanel.setLayout(new BoxLayout(radiusPanel, BoxLayout.Y_AXIS));
        radiusPanel.add(radiusLabel);
        radiusPanel.add(blurSlider);

        // Create our gaussian standard deviation panel, label and slider
        JPanel deviationPanel = new JPanel();
        deviationPanel.setLayout(new BoxLayout(deviationPanel, BoxLayout.Y_AXIS));
        deviationPanel.add(deviationLabel);
        deviationPanel.add(deviationSlider);

        // Add all of our controls to the user control panel
        controlPanel.add(radiusPanel);
        controlPanel.add(deviationPanel);
        controlPanel.add(resetButton);

        // Create our gaussian blur image widget
        GaussianBlurImage blurImage = new GaussianBlurImage();

        // Add our user controls and gaussian blur image widget to the JFrame
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(controlPanel);
        contentPane.add(blurImage);

        // Set up our blur slider parameters
        blurSlider.setMinimum(0);
        blurSlider.setMaximum(10);
        blurSlider.setMinorTickSpacing(1);
        blurSlider.setMajorTickSpacing(5);
        blurSlider.setSnapToTicks(true);
        blurSlider.setPaintTicks(true);
        blurSlider.setPaintLabels(true);
        blurSlider.setValue(0);

        // Set up a listener to update the gaussian blur image when we drag the radius slider
        blurSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                blurImage.setBlur(blurSlider.getValue(), deviationSlider.getValue());
            }
        });

        // Set up our deviation slider parameters
        deviationSlider.setMinimum(1);
        deviationSlider.setMaximum(3);
        deviationSlider.setMajorTickSpacing(1);
        deviationSlider.setSnapToTicks(true);
        deviationSlider.setPaintTicks(true);
        deviationSlider.setPaintLabels(true);
        deviationSlider.setPreferredSize(new Dimension(75, 50));
        deviationSlider.setValue(1);

        // Set up a listener to update the gaussian blur image when we drag the deviation slider
        deviationSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                blurImage.setBlur(blurSlider.getValue(), deviationSlider.getValue());
            }
        });

        // Set up our reset button to just reset the slider values back to zero
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blurSlider.setValue(0);
                deviationSlider.setValue(1);
            }
        });

        // Set up our open file button to open up a file dialog and attempt to set the gaussian blur image
        final JFrame thisFrame = this;
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and show our file dialog
                FileDialog dialog = new FileDialog(thisFrame, "Choose an image", FileDialog.LOAD);
                dialog.setVisible(true);

                // If the directory or file name are null, assume the user canceled
                if (dialog.getDirectory() == null || dialog.getFile() == null)
                    return;

                // Create the full path from the directory and the file name
                String fileName = dialog.getDirectory() + dialog.getFile();

                try {
                    // Try to load the file from disk
                    BufferedImage image = ImageIO.read(new File(fileName));

                    // If we succeeded reading the file but buffered image is null, the file is invalid
                    // Show an error
                    if (image == null) {
                        JOptionPane.showMessageDialog(thisFrame, "Failed to load image!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // If we succeeded, set the gaussian blur image
                    blurImage.setImage(ImageIO.read(new File(fileName)));
                } catch (IOException ex) {
                    // If we failed to read the file from disk, show an error
                    JOptionPane.showMessageDialog(thisFrame, "Failed to load file!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                // Once our new image has been loaded, pack the frame so it automatically resizes
                pack();

                // Reset pir blur and deviation sliders
                blurSlider.setValue(0);
                deviationSlider.setValue(1);
            }
        });

        // Set up our quit button to just call system exit
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Finally, pack our JFrame, center it on screen and make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        // Simply create our main GaussianBlur JFrame
        GaussianBlur gaussianBlur = new GaussianBlur();
    }
}
