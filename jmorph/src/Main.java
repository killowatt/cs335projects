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

class ImageMeshPanel extends JPanel {
    ImageMeshPanel(ImageMesh imageMesh, JMorph morph) {
        setLayout(new GridBagLayout());



        GridBagConstraints czs = new GridBagConstraints();
        czs.fill = GridBagConstraints.BOTH;
        czs.weighty = 1.0f;
        czs.weightx = 1.0f;



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

        //constr.weightx = 1.0f;
        //constr.weighty = 1.0f;

        add(imageMesh, czs);

        constr.gridx = 0;
        constr.gridy = 1;

        add(leftimgcontrols, constr);


        lBrightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float value = lBrightnessSlider.getValue() / 100.0f;
                imageMesh.setBrightness(value);
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageMesh.setImage(morph.getImageInput());
                morph.pack();
            }
        });
    }
}

// Main JMorph frame, handles the creation and layout of the main window
class JMorph extends JFrame {
    ImageMesh leftImageMesh;
    ImageMesh rightImageMesh;

    JSpinner frameCountSpinner;
    JSpinner delaySpinner;

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
        imagesPanel.setLayout(new GridBagLayout());

        leftImageMesh = new ImageMesh();
        rightImageMesh = new ImageMesh();

        ImageMeshPanel leftImagePanel = new ImageMeshPanel(leftImageMesh, this);
        ImageMeshPanel rightImagePanel = new ImageMeshPanel(rightImageMesh, this);

        // aa
        GridBagConstraints czs = new GridBagConstraints();
        czs.fill = GridBagConstraints.BOTH;
        czs.weighty = 1.0f;
        czs.weightx = 1.0f;

        czs.insets = new Insets(8, 8, 8, 4);

        imagesPanel.add(leftImagePanel, czs);

        czs.insets.right = 8;
        czs.insets.left = 4;

        imagesPanel.add(rightImagePanel, czs);


        // User controls panel
        JPanel controls = new JPanel();

        JButton previewButton = new JButton("Preview");

        JButton renderButton = new JButton("Render");

        SpinnerNumberModel delayModel = new SpinnerNumberModel(16, 1, 1000, 1);
        delaySpinner = new JSpinner(delayModel);

        SpinnerNumberModel frameCountModel = new SpinnerNumberModel(90, 2, 1200, 50);
        frameCountSpinner = new JSpinner(frameCountModel);

        JSlider gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, leftImageMesh.getGridSize());
        gridSizeSlider.setMajorTickSpacing(2);
        gridSizeSlider.setMinorTickSpacing(1);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setSnapToTicks(true);
        gridSizeSlider.setPaintLabels(true);

        controls.add(new JLabel("Grid Size"));
        controls.add(gridSizeSlider);

        controls.add(new JLabel("Frames"));
        controls.add(frameCountSpinner);

        controls.add(new JLabel("Frame Delay"));
        controls.add(delaySpinner);

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

        // Set our preview button to show our preview window
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showpreview(false);
            }
        });

        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showpreview(true);
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

    void showpreview(boolean isRender) {
        // Get our total frames and frame delay from the respective spinners
        int frames = (int) frameCountSpinner.getValue();
        int delay = (int) delaySpinner.getValue();

        WarpDialog pv = new WarpDialog(leftImageMesh, rightImageMesh, frames, delay, isRender);
    }
}

// Main entrypoint
public class Main {
    public static void main(String[] args) {
        // Simply create our main JMorph window
        JMorph jMorph = new JMorph();
    }
}
