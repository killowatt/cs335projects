import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

class GaussianBlur extends JFrame {
    GaussianBlur() {
        super("Gaussian Blur");

        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem openFileButton = new JMenuItem("Open");
        JSeparator fileSeparator = new JSeparator();
        JMenuItem quitButton = new JMenuItem("Quit");

        fileMenu.add(openFileButton);
        fileMenu.add(fileSeparator);
        fileMenu.add(quitButton);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);


        JPanel controlPanel = new JPanel();

        JLabel radiusLabel = new JLabel("Radius");
        JSlider blurSlider = new JSlider();

        JLabel deviationLabel = new JLabel("Std Dev");
        JSlider deviationSlider = new JSlider();

        JButton resetButton = new JButton("Reset");

        radiusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        radiusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JPanel radiusPanel = new JPanel();
        radiusPanel.setLayout(new BoxLayout(radiusPanel, BoxLayout.Y_AXIS));
        radiusPanel.add(radiusLabel);
        radiusPanel.add(blurSlider);

        JPanel deviationPanel = new JPanel();
        deviationPanel.setLayout(new BoxLayout(deviationPanel, BoxLayout.Y_AXIS));
        deviationPanel.add(deviationLabel);
        deviationPanel.add(deviationSlider);

        controlPanel.add(radiusPanel);
        controlPanel.add(deviationPanel);
        controlPanel.add(resetButton);

        GaussianBlurImage blurImage = new GaussianBlurImage();

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(controlPanel);
        contentPane.add(blurImage);

        //
        blurSlider.setMinimum(0);
        blurSlider.setMaximum(10);
        blurSlider.setMinorTickSpacing(1);
        blurSlider.setMajorTickSpacing(5);
        blurSlider.setSnapToTicks(true);
        blurSlider.setPaintTicks(true);
        blurSlider.setPaintLabels(true);
        blurSlider.setValue(0);

        blurSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                blurImage.setBlur(blurSlider.getValue(), deviationSlider.getValue());
            }
        });

        //
        deviationSlider.setMinimum(1);
        deviationSlider.setMaximum(3);
        deviationSlider.setMajorTickSpacing(1);
        deviationSlider.setSnapToTicks(true);
        deviationSlider.setPaintTicks(true);
        deviationSlider.setPaintLabels(true);
        deviationSlider.setPreferredSize(new Dimension(75, 50));
        deviationSlider.setValue(1);

        deviationSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                blurImage.setBlur(blurSlider.getValue(), deviationSlider.getValue());
            }
        });

        final GaussianBlur thf = this;
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blurSlider.setValue(0);
                deviationSlider.setValue(1);
            }
        });

        final JFrame thisFrame = this;
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog dialog = new FileDialog(thisFrame, "Choose an image", FileDialog.LOAD);
                dialog.setVisible(true);

                String fileName = dialog.getDirectory() + dialog.getFile();

                try {
                    blurImage.setImage(ImageIO.read(new File(fileName)));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                pack();

                blurSlider.setValue(0);
                deviationSlider.setValue(1);
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        GaussianBlur gaussianBlur = new GaussianBlur();
    }
}
