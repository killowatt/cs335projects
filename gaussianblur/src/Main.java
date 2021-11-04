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

        JSlider blurSlider = new JSlider();

        JButton resetButton = new JButton("Reset");

        GaussianBlurImage ererfg = new GaussianBlurImage();

        controlPanel.add(blurSlider);
        controlPanel.add(resetButton);




        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(controlPanel);
        contentPane.add(ererfg);

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final GaussianBlur thf = this;
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ererfg.reset();
            }
        });

        blurSlider.setPaintLabels(true);
        blurSlider.setPaintTicks(true);
        blurSlider.setSnapToTicks(true);
        blurSlider.setMinimum(0);
        blurSlider.setMaximum(10);
        blurSlider.setMinorTickSpacing(1);
        blurSlider.setMajorTickSpacing(5);

        blurSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ererfg.setBlur(blurSlider.getValue());
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
                    ererfg.setImage(ImageIO.read(new File(fileName)));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                pack();
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
