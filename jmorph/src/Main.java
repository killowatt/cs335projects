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

        JPanel leftImagePanel = new JPanel();
        leftImagePanel.setLayout(new GridBagLayout());

        JPanel rightImagePanel = new JPanel();
        rightImagePanel.setLayout(new GridBagLayout());

        ImageMesh leftImageMesh = new ImageMesh();
        ImageMesh rightImageMesh = new ImageMesh();



        // left image panel
        JPanel leftimgcontrols = new JPanel();

        JSlider lBrightnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
        lBrightnessSlider.setMajorTickSpacing(100);
        lBrightnessSlider.setPaintTicks(true);
        lBrightnessSlider.setSnapToTicks(false);

        JButton openButton = new JButton("Open");

        leftimgcontrols.add(openButton);
        leftimgcontrols.add(new JLabel("Brightness"));
        leftimgcontrols.add(lBrightnessSlider);

        GridBagConstraints constr = new GridBagConstraints();

        constr.weightx = 1.0f;
        constr.weighty = 1.0f;

        leftImagePanel.add(leftImageMesh, constr);

        constr.gridx = 0;
        constr.gridy = 1;

        leftImagePanel.add(leftimgcontrols, constr);



        // right iamge panel
        JPanel rightimgcontrols = new JPanel();

        JSlider rBrightnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
        rBrightnessSlider.setMajorTickSpacing(100);
        rBrightnessSlider.setPaintTicks(true);
        rBrightnessSlider.setSnapToTicks(false);

        JButton openButtonR = new JButton("Open");

        rightimgcontrols.add(openButtonR);
        rightimgcontrols.add(new JLabel("Brightness"));
        rightimgcontrols.add(rBrightnessSlider);

        GridBagConstraints constrz = new GridBagConstraints();

        constrz.weightx = 1.0f;
        constrz.weighty = 1.0f;

        rightImagePanel.add(rightImageMesh, constrz);

        constrz.gridx = 0;
        constrz.gridy = 1;

        rightImagePanel.add(rightimgcontrols, constrz);




        // aa
        imagesPanel.add(leftImagePanel);
        imagesPanel.add(rightImagePanel);


        // User controls panel
        JPanel controls = new JPanel();

        JButton previewButton = new JButton("Preview");

        JButton renderButton = new JButton("Render");

        SpinnerNumberModel delayModel = new SpinnerNumberModel(16, 1, 1000, 1);
        JSpinner delaySpinner = new JSpinner(delayModel);

        SpinnerNumberModel frameCountModel = new SpinnerNumberModel(90, 2, 1200, 50);
        JSpinner frameCountSpinner = new JSpinner(frameCountModel);

        JSlider gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, leftImageMesh.getGridSize());
        gridSizeSlider.setMajorTickSpacing(3);
        gridSizeSlider.setMinorTickSpacing(1);
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
        controls.add(renderButton);


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


        // Set our left and right images other field
        leftImageMesh.other = rightImageMesh;
        rightImageMesh.other = leftImageMesh;

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftImageMesh.setImage(getImageInput());
                pack();
            }
        });

        openButtonR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightImageMesh.setImage(getImageInput());
                pack();
            }
        });

        lBrightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float value = lBrightnessSlider.getValue() / 100.0f;
                leftImageMesh.setBrightness(value);
            }
        });

        rBrightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float value = rBrightnessSlider.getValue() / 100.0f;
                rightImageMesh.setBrightness(value);
            }
        });

        // Set our preview button to show our preview window
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If our left image and right image are not of equal size, show an error
//                if (!leftImageMesh.getSize().equals(rightImageMesh.getSize())) {
//                    JOptionPane.showMessageDialog(null, "Images must be of equal size.", "Error", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }

                // Get our total frames and frame delay from the respective spinners
                int frames = (int) frameCountSpinner.getValue();
                int delay = (int) delaySpinner.getValue();

                // Create a panel for our preview window for later expansion
                JPanel preview = new JPanel();

                // Create and add our morph preview panel to our frame
                preview.add(new MorphPreview(leftImageMesh, rightImageMesh, frames, delay, false));

                // Create a dialog box with our preview panel as the only content
                JOptionPane.showOptionDialog(null, preview, "Preview", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            }
        });

        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // AAAAAAAAAAAAA
                // AAAAAAAAAAAAAAA
                // AAAAAAAAAAAAAAAAAAAAAAA

                // If our left image and right image are not of equal size, show an error
//                if (!leftImageMesh.getSize().equals(rightImageMesh.getSize())) {
//                    JOptionPane.showMessageDialog(null, "Images must be of equal size.", "Error", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }

                // Get our total frames and frame delay from the respective spinners
                int frames = (int) frameCountSpinner.getValue();
                int delay = (int) delaySpinner.getValue();

                // Create a panel for our preview window for later expansion
                JPanel preview = new JPanel();

                // Create and add our morph preview panel to our frame
                preview.add(new MorphPreview(leftImageMesh, rightImageMesh, frames, delay, true));

                // Create a dialog box with our preview panel as the only content
                JOptionPane.showOptionDialog(null, preview, "Preview", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
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
