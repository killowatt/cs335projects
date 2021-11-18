import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageMesh extends JPanel {
    BufferedImage ourimg;

    ImageMesh() {
        super();

        setBackground(Color.black);
        setPreferredSize(new Dimension(256, 256));
    }

    public void setImage(BufferedImage image) {
        if (image == null)
            return;

        System.out.println("Set image!");
        ourimg = image;

        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));


        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (ourimg == null)
            return;

        g.drawImage(ourimg, 0, 0, null);
    }
}
