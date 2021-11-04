import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

class GaussianBlurImage extends JComponent {
    private BufferedImage bufferedImage;
    private BufferedImage blurred;

    boolean filter = false;

    GaussianBlurImage() {
        super();
    }

    public void setImage(BufferedImage image) {
        bufferedImage = image;

        blurred = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);


        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));



        revalidate();
    }

    private final float[] LOWPASS3x3 =
            {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};

    public void setBlur(int radius) {
       // Kernel kernel = new Kernel(3, 3, LOWPASS3x3);

        int kernelWidth = (2 * radius) + 1;

        float[] kernelz = generateKernel(radius);
        Kernel kernel = new Kernel(kernelWidth, kernelWidth, kernelz);

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);



        BufferedImage bi = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = bi.createGraphics();
        g2D.drawImage(bufferedImage, 0, 0, null);

        op.filter(bi, blurred);

        filter = true;

        repaint();
    }

    public void reset() {
        filter = false;
        repaint();
    }

    private float[] generateKernel(int radius) {

        double sigma = Math.max(radius / 2.0, 1.0);

        int kernelWidth = (2 * radius) + 1;

        float[] kernel = new float[kernelWidth * kernelWidth];

        float sum = 0.0f;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                //System.out.println("xy " + x + " " + y);

                double exponentNumerator = -(x * x + y * y);
                double exponentDenominator = (2 * sigma * sigma);

                double eExpression = Math.pow(Math.E, exponentNumerator / exponentDenominator);
                double kernelValue = eExpression / (2.0 * Math.PI * sigma * sigma);

                int index = kernelWidth * (y + radius) + (x + radius);
                kernel[index] = (float)kernelValue;
                sum += (float)kernelValue;
            }
        }

        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D)g;

        if (!filter) {
            g2D.drawImage(bufferedImage, 0, 0, this);
            System.out.println("repaint");

        }
        else {
            g2D.drawImage(blurred, 0, 0, this);
            System.out.println("repaint blurred");

        }
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

        blurSlider.setPaintTicks(true);
        blurSlider.setSnapToTicks(true);
        blurSlider.setMinimum(0);
        blurSlider.setMaximum(8);
        blurSlider.setMajorTickSpacing(1);

        blurSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println(blurSlider.getValue());
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
