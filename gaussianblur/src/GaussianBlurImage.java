import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

class GaussianBlurImage extends JComponent {
    boolean filter = false;
    private BufferedImage image;
    private BufferedImage blurredImage;

    GaussianBlurImage() {
        super();
    }

    public void setImage(BufferedImage bufferedImage) {
        filter = false;

        image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();
        g2D.drawImage(bufferedImage, 0, 0, null);

        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        revalidate();
    }

    public void setBlur(int radius, int deviation) {
        if (image == null || radius <= 0)
            return;

        int kernelSize = (2 * radius) + 1;

        float[] kernelData = generateKernel(radius, kernelSize, deviation);
        Kernel kernel = new Kernel(kernelSize, kernelSize, kernelData);

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        blurredImage = op.filter(image, null);

        filter = true;

        repaint();
    }

    private float[] generateKernel(int radius, int kernelSize, int deviation) {
        // https://aryamansharda.medium.com/image-filters-gaussian-blur-eb36db6781b1

        double sigma = Math.max(radius / 2.0, deviation);

        float[] kernel = new float[kernelSize * kernelSize];

        float sum = 0.0f;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                //System.out.println("xy " + x + " " + y);

                double exponentNumerator = -(x * x + y * y);
                double exponentDenominator = (2 * sigma * sigma);

                double eExpression = Math.pow(Math.E, exponentNumerator / exponentDenominator);
                double kernelValue = eExpression / (2.0 * Math.PI * sigma * sigma);

                int index = kernelSize * (y + radius) + (x + radius);
                kernel[index] = (float) kernelValue;
                sum += (float) kernelValue;
            }
        }

        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        if (!filter)
            g2D.drawImage(image, 0, 0, this);
        else
            g2D.drawImage(blurredImage, 0, 0, this);
    }
}
