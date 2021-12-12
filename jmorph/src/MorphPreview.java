import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// TODO: prevent closing when writing files
// TODO: end animation/writing file prematurely if closed

// Morph Preview panel, handles the morph animation preview
public class MorphPreview extends JPanel {
    // The timer and current time for our animation
    Timer timer;
    float time = 0.0f;

    // The vertices and triangles of our grid
    ArrayList<Point2D> vertices;
    ArrayList<Integer> triangles;

    BufferedImage warped;
    BufferedImage warped2;

    ImageMesh firs;
    ImageMesh secd;

    BufferedImage frame = null;

    boolean isRendering = false;

    int currFrame = 0;

    // Constructor for our morph preview panel
    MorphPreview(ImageMesh first, ImageMesh second, int frames, int delay, boolean renderToFile) {
        isRendering = renderToFile;

        // Use a black background and grab the first image's size
        setBackground(Color.black);
        setPreferredSize(first.getSize());

        firs = first;
        secd = second;

        // Create a copy of the first image's vertices as a starting point
        vertices = new ArrayList<>(first.vertices);
        triangles = new ArrayList<>(first.triangles);

        // Make deep copies of all the vertices...
        for (int i = 0; i < vertices.size(); i++) {
            Point2D original = first.vertices.get(i);
            Point2D copy = new Point2D.Double(original.getX(), original.getY());
            vertices.set(i, copy);
        }

        // Set our time step so that after all frames are played time will equal one.
        float timeStep = 1.0f / frames;

        // Set up our animation timer
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clamp our time so that we don't overshoot vertex positions
                if (time > 1.0f)
                    time = 1.0f;

                // Interpolate between the points of the first and second image
                for (int i = 0; i < vertices.size(); i++) {
                    Point2D firstVertex = first.vertices.get(i);
                    Point2D secondVertex = second.vertices.get(i);

                    double x = firstVertex.getX() + (secondVertex.getX() - firstVertex.getX()) * time;
                    double y = firstVertex.getY() + (secondVertex.getY() - firstVertex.getY()) * time;

                    vertices.get(i).setLocation(x, y);
                }

                // Repaint the panel and increment the timestep
                warped = getwarp(firs.getimageok(), firs.vertices, vertices);
                warped2 = getwarp(secd.getimageok(), secd.vertices, vertices);

                outputImage();
                repaint();

                time += timeStep;
                currFrame++;

                // Once the animation completes, ensure all of our points ended up the same as the second image
                if (time >= 1.0f) {
                    timer.stop();

                    for (int i = 0; i < vertices.size(); i++) {
                        Point2D finalVertex = second.vertices.get(i);

                        double finalX = finalVertex.getX();
                        double finalY = finalVertex.getY();

                        vertices.get(i).setLocation(finalX, finalY);
                    }

                    time = 1.0f;
                    repaint();
                }
            }
        });

        // Start our timer to play our animation
        timer.start();
    }

    BufferedImage getwarp(BufferedImage srcimg, ArrayList<Point2D> from, ArrayList<Point2D> to) {
        BufferedImage result = new BufferedImage(firs.image.getWidth(this), firs.image.getHeight(this), BufferedImage.TYPE_INT_RGB);

        double scaleX = getSize().width;
        double scaleY = getSize().height;

        for (int i = 0; i < triangles.size(); i += 3) {
            double[] srcX = new double[3];
            double[] srcY = new double[3];
            double[] dstX = new double[3];
            double[] dstY = new double[3];

            int i1 = triangles.get(i);
            int i2 = triangles.get(i + 1);
            int i3 = triangles.get(i + 2);

            srcX[0] = from.get(i1).getX() * scaleX;
            srcX[1] = from.get(i2).getX() * scaleX;
            srcX[2] = from.get(i3).getX() * scaleX;

            srcY[0] = from.get(i1).getY() * scaleY;
            srcY[1] = from.get(i2).getY() * scaleY;
            srcY[2] = from.get(i3).getY() * scaleY;

            dstX[0] = to.get(i1).getX() * scaleX;
            dstX[1] = to.get(i2).getX() * scaleX;
            dstX[2] = to.get(i3).getX() * scaleX;

            dstY[0] = to.get(i1).getY() * scaleY;
            dstY[1] = to.get(i2).getY() * scaleY;
            dstY[2] = to.get(i3).getY() * scaleY;

            ImageMeshRendering.WarpTriangle(srcimg, result, srcX, srcY, dstX, dstY, null, null, false);
        }

        return result;
    }

    // TODO: use BRIGHTNESS applied image from image mesh
    void outputImage() {
        frame = new BufferedImage(firs.image.getWidth(), firs.image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2D = frame.createGraphics();

        //g.drawImage(firstimg, 0, 0, firstimg.getWidth(), firstimg.getHeight(), null);
        if (warped != null)
            g2D.drawImage(warped, 0, 0, warped.getWidth(), warped.getHeight(), null);

        if (warped2 != null) {
            System.out.println(time);

            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, time);
            g2D.setComposite(ac);

            g2D.drawImage(warped2, 0, 0, warped.getWidth(), warped.getHeight(), null);
        }

        g2D.drawString("Test", 16, 16);

        if (isRendering) {
            try {
                String fn = "frames/frame" + currFrame + ".png";
                ImageIO.write(frame, "png", new File(fn));
                System.out.println(fn + " wrote to disk");
            } catch (IOException e) {
                System.out.println("Failed to write??");
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (frame != null)
            g.drawImage(frame, 0, 0, frame.getWidth(), frame.getHeight(), null);

        if (isRendering)
            return; // we dont draw points in render preview

        // The size of our rendering area
        Dimension size = getSize();

        // Draw our triangle grid
        ImageMeshRendering.DrawMesh(g, size, vertices, triangles);

        // Then, for every point in our grid
        for (Point2D vertex : vertices) {
            // Set the color for the handles
            g.setColor(Color.cyan);

            // Use our helper to draw this handle for consistency
            ImageMeshRendering.DrawHandle(g, size, vertex);
        }
    }
}
