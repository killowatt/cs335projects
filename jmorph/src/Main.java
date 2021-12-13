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
    // Left and right image mesh objects
    ImageMesh leftImageMesh;
    ImageMesh rightImageMesh;

    // Our spinner
    JSpinner frameCountSpinner;

    JMorph() {
        // Set our window title to JMorph
        super("JMorph");

        // Set so when the window is closed the application also exits
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create our main panel with a grid bag layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        // Create the panel that will contain both of our images
        JPanel imagesPanel = new JPanel();
        imagesPanel.setLayout(new GridBagLayout());

        // Create our image mesh objects
        leftImageMesh = new ImageMesh();
        rightImageMesh = new ImageMesh();

        // Create the panels they will belong to
        ImageMeshPanel leftImagePanel = new ImageMeshPanel(leftImageMesh, this);
        ImageMeshPanel rightImagePanel = new ImageMeshPanel(rightImageMesh, this);

        // Set up our constraints for each of our image mesh panels
        GridBagConstraints imagePanelConstraints = new GridBagConstraints();
        imagePanelConstraints.fill = GridBagConstraints.BOTH;
        imagePanelConstraints.weighty = 1.0f;
        imagePanelConstraints.weightx = 1.0f;

        // Set up insets for the left image panel and add it to our images panel
        imagePanelConstraints.insets = new Insets(8, 8, 8, 4);
        imagesPanel.add(leftImagePanel, imagePanelConstraints);

        // Set up insets for the right image panel and add it to our images panel
        imagePanelConstraints.insets = new Insets(8, 4, 8, 8);
        imagesPanel.add(rightImagePanel, imagePanelConstraints);


        // Create our user controls JPanel
        JPanel controls = new JPanel();

        // Create our grid size slider
        JSlider gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, leftImageMesh.getGridSize());
        gridSizeSlider.setMajorTickSpacing(2);
        gridSizeSlider.setMinorTickSpacing(1);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setSnapToTicks(true);
        gridSizeSlider.setPaintLabels(true);

        // Add our grid size slider to the panel with label
        controls.add(new JLabel("Grid Size"));
        controls.add(gridSizeSlider);

        // Create our frame count spinner
        SpinnerNumberModel frameCountModel = new SpinnerNumberModel(90, 2, 1200, 50);
        frameCountSpinner = new JSpinner(frameCountModel);

        // Add our frame count spinner to the panel with label
        controls.add(new JLabel("Frames"));
        controls.add(frameCountSpinner);

        // Create our preview and render buttons
        JButton previewButton = new JButton("Preview");
        JButton renderButton = new JButton("Render");

        // Add our preview and render buttons to the panel
        controls.add(previewButton);
        controls.add(renderButton);

        // Add our images panel to the main panel
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 0;
        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;
        mainPanel.add(imagesPanel, constraints);

        // Modify the constraints and add our user controls to the main panel
        constraints.weighty = 0.0f;
        constraints.weightx = 0.0f;
        constraints.gridy = 1;
        mainPanel.add(controls, constraints);

        // Add our main panel to the main JFrame
        getContentPane().add(mainPanel);


        // Set our left and right images other field
        leftImageMesh.other = rightImageMesh;
        rightImageMesh.other = leftImageMesh;

        // Set our preview button to show the preview morph dialog
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMorphDialog(true);
            }
        });

        // Set our render button to show the non-preview morph dialog
        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMorphDialog(false);
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

    // Helper method that checks input before showing the morph dialog
    void showMorphDialog(boolean isPreview) {
        // If we don't have both images, show an error and return
        if (!leftImageMesh.hasImage() || !rightImageMesh.hasImage()) {
            JOptionPane.showMessageDialog(this, "You need to load both images!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get our total frame count from the spinner
        int frames = (int) frameCountSpinner.getValue();

        // Create our morph dialog
        MorphDialog morphDialog = new MorphDialog(leftImageMesh, rightImageMesh, frames, isPreview);
    }
}

// Main entrypoint
public class Main {
    public static void main(String[] args) {
        // Simply create our main JMorph window
        JMorph jMorph = new JMorph();
    }
}
