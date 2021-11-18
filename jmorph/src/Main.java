import javax.imageio.ImageIO;
import javax.swing.*;
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

        imagesPanel.setLayout(new GridBagLayout());

        GridBagConstraints cz = new GridBagConstraints();
        cz.fill = GridBagConstraints.BOTH;
        cz.weightx = 1.0f;
        cz.weighty = 1.0f;
        cz.insets = new Insets(4, 4, 4, 4);

        imagesPanel.add(a, cz);
        imagesPanel.add(b, cz);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 0;

        JPanel controls = new JPanel();
        controls.add(new JButton("Render"));
        controls.add(new JSlider());

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
