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

class JMorph extends JFrame {
    JMorph() {
        super("JMorph");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JPanel imagesPanel = new JPanel();
        ImageMesh a = new ImageMesh();
        ImageMesh b = new ImageMesh();

        a.other = b;
        b.other = a;

        //imagesPanel.setLayout(new GridBagLayout());

//        GridBagConstraints cz = new GridBagConstraints();
//        cz.fill = GridBagConstraints.BOTH;
//        cz.weightx = 1.0f;
//        cz.weighty = 1.0f;
//        cz.insets = new Insets(4, 4, 4, 4);

        imagesPanel.add(a);//, cz);
        imagesPanel.add(b);//, cz);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 0;

        JPanel controls = new JPanel();

        JButton previewButton = new JButton("Preview");



//        JSlider fpsSlider = new JSlider();
//        fpsSlider.setMinimum(1);
//        fpsSlider.setMaximum(60);
//        fpsSlider.setMinorTickSpacing(1);
//        fpsSlider.setMajorTickSpacing(10);
//        fpsSlider.setSnapToTicks(true);
//        fpsSlider.setPaintTicks(true);
//        fpsSlider.setPaintLabels(true);
//        fpsSlider.setValue(30);

        SpinnerNumberModel model = new SpinnerNumberModel(16, 1, 1000, 1);
        JSpinner fpsSpinner = new JSpinner(model);

        SpinnerNumberModel lenm = new SpinnerNumberModel(90, 2, 1200, 50);
        JSpinner lenSpin = new JSpinner(lenm);

        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int frames = (int)lenSpin.getValue();
                int delay = (int)fpsSpinner.getValue();

                JPanel preview = new JPanel();

                preview.add(new MorphPreview(a, b, frames, delay));


                JOptionPane.showOptionDialog(null, preview, "Preview", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
            }
        });

        JSlider gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 2, 10, a.getGridSize());
        gridSizeSlider.setMajorTickSpacing(1);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setSnapToTicks(true);
        gridSizeSlider.setPaintLabels(true);

        gridSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                a.setGridSize(gridSizeSlider.getValue());
                b.setGridSize(gridSizeSlider.getValue());
            }
        });

        controls.add(new JLabel("Grid Size"));
        controls.add(gridSizeSlider);
        controls.add(new JLabel("Frame Delay"));
        controls.add(fpsSpinner);
        controls.add(new JLabel("# Frames"));
        controls.add(lenSpin);

        controls.add(previewButton);

        constraints.weighty = 1.0f;
        constraints.weightx = 1.0f;
        panel.add(imagesPanel, constraints);

        constraints.weighty = 0.0f;
        constraints.weightx = 0.0f;

        constraints.gridy = 1;
        panel.add(controls, constraints);

        getContentPane().add(panel);


        // menu
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem openLeftImage = new JMenuItem("Open Left Image");
        JMenuItem openRightImage = new JMenuItem("Open Right Image");

        fileMenu.add(openLeftImage);
        fileMenu.add(openRightImage);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);



        //
        openLeftImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                a.setImage(getimg());
                pack();
            }
        });

        openRightImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.setImage(getimg());
                pack();
            }
        });



        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    final JFrame thisFrame = this;
    BufferedImage getimg() {
        // Create and show our file dialog
        FileDialog dialog = new FileDialog(thisFrame, "Choose an image", FileDialog.LOAD);
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
                JOptionPane.showMessageDialog(thisFrame, "Failed to load image!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return image;
        } catch (IOException ex) {
            // If we failed to read the file from disk, show an error
            JOptionPane.showMessageDialog(thisFrame, "Failed to load file!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}

public class Main {
    public static void main(String[] args) {
        JMorph jMorph = new JMorph();
    }
}
