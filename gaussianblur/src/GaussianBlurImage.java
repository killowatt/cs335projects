import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

// Gaussian Blur Image class that handles blurring an image and painting it on screen
class GaussianBlurImage extends JComponent {
    // Whether or not to show the filtered image
    boolean filter = false;

    // Our original image and the blurred version of it
    private BufferedImage image;
    private BufferedImage blurredImage;

    // Default constructor, just calls the JComponent constructor
    GaussianBlurImage() {
        super();
    }

    // Set image method, handles setting our original image to something new and updating our size
    public void setImage(BufferedImage bufferedImage) {
        // If the image provided is null, just ignore
        if (bufferedImage == null)
            return;

        // By default the image should be unfiltered
        filter = false;

        // Our original image must be copied first in our desired pixel format
        image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();
        g2D.drawImage(bufferedImage, 0, 0, null);

        // Set the preferred size of this component to the image size
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        // Revalidate, should repaint the widget as well as let the system know this component has resized
        revalidate();
    }

    // Set blur method, handles creating and applying our gaussian blur kernel to our image
    public void setBlur(int radius, int deviation) {
        // If our image is null or if our radius is zero, ignore
        if (image == null || radius <= 0)
            return;

        // Our kernel size, always odd so that we have a center point in our kernel
        int kernelSize = (2 * radius) + 1;

        // Use our generate kernel method to dynamically create our kernel and use it in our Kernel object
        float[] kernelData = generateKernel(radius, kernelSize, deviation);
        Kernel kernel = new Kernel(kernelSize, kernelSize, kernelData);

        // Set up our convolve operation to use our kernel, ignoring image edges
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        // Filter our image
        blurredImage = op.filter(image, null);

        // Set the image filtered flag
        filter = true;

        // Repaint this component since things have changed
        repaint();
    }

    private float[] generateKernel(int radius, int kernelSize, int deviation) {
        // Based on the link provided in the Exercise 8 spec
        // https://aryamansharda.medium.com/image-filters-gaussian-blur-eb36db6781b1

        // Scale our sigma value with the radius
        double sigma = Math.max(radius / 2.0, deviation);

        // Set up a 1D array for our kernel to be stored
        float[] kernel = new float[kernelSize * kernelSize];

        // Sum value for normalization
        float sum = 0.0f;

        // Iterate through every possible position in the kernel
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                // Numerator and denominator for our gaussian equation
                double exponentNumerator = -(x * x + y * y);
                double exponentDenominator = (2 * sigma * sigma);

                // Finally acquire the kernel value at this position
                double eExpression = Math.pow(Math.E, exponentNumerator / exponentDenominator);
                double kernelValue = eExpression / (2.0 * Math.PI * sigma * sigma);

                // Convert 2D index to 1D and set it in our kernel array, updating our sum
                int index = kernelSize * (y + radius) + (x + radius);
                kernel[index] = (float) kernelValue;

                sum += (float) kernelValue;
            }
        }

        // Normalize our kernel using the sum
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        // Return our kernel as a 1D array
        return kernel;
    }

    // Our paint component method
    @Override
    protected void paintComponent(Graphics g) {
        // Get a 2D graphics context from our regular one
        Graphics2D g2D = (Graphics2D) g;

        // If our image has yet to be filtered, just draw the original, otherwise draw the filtered one
        if (!filter)
            g2D.drawImage(image, 0, 0, this);
        else
            g2D.drawImage(blurredImage, 0, 0, this);
    }
}
