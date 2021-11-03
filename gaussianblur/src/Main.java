import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class GaussianBlurImage extends JComponent {
    private BufferedImage bufferedImage;

    GaussianBlurImage() {
        super();
    }

    public void setImage(BufferedImage image) {
        bufferedImage = image;

        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("repaint");

        Graphics2D g2D = (Graphics2D)g;
        g2D.drawImage(bufferedImage, 0, 0, this);
    }
}

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

        blurSlider.setMajorTickSpacing(25);
        blurSlider.setPaintTicks(true);
        blurSlider.setSnapToTicks(true);

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
