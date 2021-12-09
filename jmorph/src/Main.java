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

// Main JMorph frame, handles the creation and layout of the main window
class JMorph extends JFrame {
    JMorph() {
        // Set our window title to JMorph
        super("JMorph");

        // Set so when the window is closed the application also exits
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());


        // Images panel
        JPanel imagesPanel = new JPanel();
        ImageMesh leftImageMesh = new ImageMesh();
        ImageMesh rightImageMesh = new ImageMesh();

        imagesPanel.add(leftImageMesh);
        imagesPanel.add(rightImageMesh);


        // User controls panel
        JPanel controls = new JPanel();

        JButton previewButton = new JButton("Preview");

        JButton testButton = new JButton("TEST");

        SpinnerNumberModel delayModel = new SpinnerNumberModel(16, 1, 1000, 1);
        JSpinner delaySpinner = new JSpinner(delayModel);

        SpinnerNumberModel frameCountModel = new SpinnerNumberModel(90, 2, 1200, 50);
        JSpinner frameCountSpinner = new JSpinner(frameCountModel);

        JSlider gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 2, 10, leftImageMesh.getGridSize());
        gridSizeSlider.setMajorTickSpacing(1);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setSnapToTicks(true);
        gridSizeSlider.setPaintLabels(true);

        controls.add(new JLabel("Grid Size"));
        controls.add(gridSizeSlider);

        controls.add(new JLabel("Frame Delay"));
        controls.add(delaySpinner);

        controls.add(new JLabel("# Frames"));
        controls.add(frameCountSpinner);

        controls.add(previewButton);
        controls.add(testButton);

        // Add our images panel and controls panel to the main panel
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 0;
        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;
        panel.add(imagesPanel, constraints);

        constraints.weighty = 0.0f;
        constraints.weightx = 0.0f;
        constraints.gridy = 1;
        panel.add(controls, constraints);

        getContentPane().add(panel);


        // Menu Bar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openLeftImage = new JMenuItem("Open Left Image");
        JMenuItem openRightImage = new JMenuItem("Open Right Image");

        fileMenu.add(openLeftImage);
        fileMenu.add(openRightImage);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);


        // Set our left and right images other field
        leftImageMesh.other = rightImageMesh;
        rightImageMesh.other = leftImageMesh;

        // Set our preview button to show our preview window
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If our left image and right image are not of equal size, show an error
                if (!leftImageMesh.getSize().equals(rightImageMesh.getSize())) {
                    JOptionPane.showMessageDialog(null, "Images must be of equal size.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get our total frames and frame delay from the respective spinners
                int frames = (int) frameCountSpinner.getValue();
                int delay = (int) delaySpinner.getValue();

                // Create a panel for our preview window for later expansion
                JPanel preview = new JPanel();

                // Create and add our morph preview panel to our frame
                preview.add(new MorphPreview(leftImageMesh, rightImageMesh, frames, delay));

                // Create a dialog box with our preview panel as the only content
                JOptionPane.showOptionDialog(null, preview, "Preview", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            }
        });

        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftImageMesh.doBrain();
            }
        });

        // Set up our grid size slider to update the left and right images' grid sizes
        gridSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                leftImageMesh.setGridSize(gridSizeSlider.getValue());
                rightImageMesh.setGridSize(gridSizeSlider.getValue());
            }
        });

        // Set up our left image open button to set the left image
        openLeftImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftImageMesh.setImage(getImageInput());
                pack();
            }
        });

        // Set up our right image open button to set the right image
        openRightImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightImageMesh.setImage(getImageInput());
                pack();
            }
        });

        // Pack our layout, center the window on screen, and show it
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper method to query the user for an image
    BufferedImage getImageInput() {
        // Create and show our file dialog
        FileDialog dialog = new FileDialog(this, "Choose an image", FileDialog.LOAD);
        dialog.setVisible(true);

        // If the directory or file name are null, assume the user canceled
        if (dialog.getDirectory() == null || dialog.getFile() == null)
            return null;

        // Create the full path from the directory and the file name
        String fileName = dialog.getDirectory() + dialog.getFile();

        try {
            // Try to load the file from disk
            BufferedImage image = ImageIO.read(new File(fileName));

            // If we succeeded reading the file but buffered image is null, the file is invalid
            // Show an error
            if (image == null) {
                JOptionPane.showMessageDialog(this, "Failed to load image!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return image;
        } catch (IOException ex) {
            // If we failed to read the file from disk, show an error
            JOptionPane.showMessageDialog(this, "Failed to load file!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}

// Main entrypoint
public class Main {
    public static void main(String[] args) {
        // Simply create our main JMorph window
        JMorph jMorph = new JMorph();
    }
}
