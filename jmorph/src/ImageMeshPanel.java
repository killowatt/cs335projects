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

// Image Mesh Panel class, holds an image mesh and user controls
class ImageMeshPanel extends JPanel {
    ImageMeshPanel(ImageMesh imageMesh, JMorph morph) {
        // Use a grid bag layout
        setLayout(new GridBagLayout());

        // Create our JPanel for user controls
        JPanel controls = new JPanel();

        // Create our image file open button
        JButton openButton = new JButton("Open");

        // Create our brightness slider
        JSlider brightnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, (int)(imageMesh.getBrightness() * 100.0f));
        brightnessSlider.setMajorTickSpacing(100);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setSnapToTicks(false);

        // Add our controls
        controls.add(openButton);
        controls.add(new JLabel("Brightness"));
        controls.add(brightnessSlider);

        // Create our JPanel that holds our ImageMesh panel
        JPanel imageMeshContainer = new JPanel(new GridBagLayout());
        imageMeshContainer.setPreferredSize(new Dimension(372, 372));
        imageMeshContainer.setBackground(Color.black);
        imageMeshContainer.add(imageMesh);

        // Set up our constraints to fill all available space
        GridBagConstraints imageConstraints = new GridBagConstraints();
        imageConstraints.fill = GridBagConstraints.BOTH;
        imageConstraints.weighty = 1.0f;
        imageConstraints.weightx = 1.0f;

        // Add our image mesh container to this panel
        add(imageMeshContainer, imageConstraints);

        // Set up our constraints for our user controls
        GridBagConstraints controlConstraints = new GridBagConstraints();
        controlConstraints.gridx = 0;
        controlConstraints.gridy = 1;

        // Add our user controls to this panel
        add(controls, controlConstraints);

        // Set up our open file button to set the image mesh image
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageMesh.setImage(getImageInput(morph));
                morph.pack();
            }
        });

        // Set up our brightness slider to set the image mesh's brightness value
        brightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float value = brightnessSlider.getValue() / 100.0f;
                imageMesh.setBrightness(value);
            }
        });
    }

    // Helper method to query the user for an image
    BufferedImage getImageInput(JFrame frame) {
        // Create and show our file dialog
        FileDialog dialog = new FileDialog(frame, "Choose an image", FileDialog.LOAD);
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