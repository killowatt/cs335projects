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
                showMorphDialog(true);
            }
        });

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

    void showMorphDialog(boolean isPreview) {
        // If we don't have both images, show an error and return
        if (!leftImageMesh.hasImage() || !rightImageMesh.hasImage()) {
            JOptionPane.showMessageDialog(this, "You need to load both images!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get our total frame count from the spinner
        int frames = (int) frameCountSpinner.getValue();

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
