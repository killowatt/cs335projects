import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MorphDialog extends JDialog {
    // The vertices and triangles of our grid
    private final ArrayList<Vertex> vertices;
    private final ArrayList<Integer> triangles;

    // Start and end image mesh objects
    private ImageMesh startImage;
    private ImageMesh endImage;

    // Thread object we use to render images
    Thread thread;

    // Last fully rendered frame
    private BufferedImage lastFrame = null;

    // Total number of frames
    private final int totalFrames;

    // The current frame of the render
    private int currentFrame = 0;

    // Width and height of our renders
    private final int width;
    private final int height;

    // Internal flag, when set will stop rendering of the remaining frames (but will finish the current one)
    private boolean stopRendering = false;

    // Preview flag, when set will draw handles and prevents saving to file
    private final boolean isPreview;

    // Dialog constructor, takes two image meshes, a total frame count, and a preview flag
    MorphDialog(ImageMesh start, ImageMesh end, int totalFrames, boolean isPreview) {
        // Original dialog constructor, make sure we're a modal dialog
        super((Frame) null, true);

        // Dispose of this dialog when closed
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set our start and end image mesh objects
        this.startImage = start;
        this.endImage = end;

        // Set the total frames and preview flag
        this.totalFrames = totalFrames;
        this.isPreview = isPreview;

        // Set our width and height to match the starting image
        this.width = startImage.getWidth();
        this.height = startImage.getHeight();

        // Create a copy of the first image's vertices as a starting point
        vertices = new ArrayList<>(startImage.vertices);
        triangles = new ArrayList<>(endImage.triangles);

        // Make deep copies of all the vertices since the above code doesn't
        for (int i = 0; i < vertices.size(); i++) {
            Vertex original = startImage.vertices.get(i);
            vertices.set(i, original.copy());
        }

        // Ensure we have a directory to write our frames to
        new File("frames/").mkdir();

        // Create our preview panel...
        JPanel previewPanel = new JPanel() {
            // Override its paint method to draw our frames...
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // If we have a last rendered frame, draw it
                if (lastFrame != null)
                    g.drawImage(lastFrame, 0, 0, width, height, null);

                // If this is the preview, render our handles
                if (isPreview) {
                    // The size of our rendering area
                    Dimension size = new Dimension(width, height);

                    // Draw our triangle grid
                    MorphGridRendering.DrawMesh(g, size, vertices, triangles);

                    // Then, for every point in our grid
                    for (Vertex vertex : vertices) {
                        // Set the color for the handles
                        g.setColor(Color.cyan);

                        // Use our helper to draw this handle for consistency
                        MorphGridRendering.DrawHandle(g, size, vertex);
                    }
                }
            }
        };

        // Set the size of our panel to match our image size, set the background color
        previewPanel.setPreferredSize(new Dimension(width, height));
        previewPanel.setBackground(Color.black);

        // Add our preview panel to this dialog
        add(previewPanel);

        // Create our thread to render frames
        thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // While we still have frames left to draw, assuming we shouldn't stop rendering...
                    while (currentFrame < totalFrames && !stopRendering) {
                        // Render the next frame and order a repaint
                        renderFrame();
                        previewPanel.repaint();

                        // Increment frame counter
                        currentFrame++;
                    }
                }
            });

        // Start rendering!
        thread.start();

        // Pack this dialog, center it on screen and make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Because this dialog is modal, execution stops when it is made visible. This is only called once the dialog
        // is closed.
        stopRendering = true;
    }

    // Render frame method, handles interpolating the points and ordering warped images
    void renderFrame() {
        // The current time, normalized from 0 to 1
        float time = (float) (currentFrame + 1) / (float) totalFrames;

        // Interpolate between the points of the first and second image
        for (int i = 0; i < vertices.size(); i++) {
            Vertex firstVertex = startImage.vertices.get(i);
            Vertex secondVertex = endImage.vertices.get(i);

            // Our first vertex, plus the vector between our second and first, multiplied by time
            float x = firstVertex.x + (secondVertex.x - firstVertex.x) * time;
            float y = firstVertex.y + (secondVertex.y - firstVertex.y) * time;

            // Update the position of this vertex
            vertices.get(i).setPosition(x, y);
        }

        // Get morphed versions of our start and end images
        BufferedImage startMorphed = generateMorph(startImage.getImage(), startImage.vertices, vertices);
        BufferedImage endMorphed = generateMorph(endImage.getImage(), endImage.vertices, vertices);

        // Overlay our end image on top of the start image with appropriate alpha value
        lastFrame = overlayImage(startMorphed, endMorphed, time);

        // If this is the full render, write the last frame to disk
        if (!isPreview) {
            // Try to write to the frames directory, format frame(currentFrame).png
            String fn = "frames/frame" + currentFrame + ".png";
            try {
                ImageIO.write(lastFrame, "png", new File(fn));
            } catch (IOException e) {
                // Show an error message on exception
                JOptionPane.showMessageDialog(null, "Failed to save " + fn + " to disk!", "Render Failed", JOptionPane.ERROR_MESSAGE);
                stopRendering = true; // Set our stop flag since an error occurred
            }
        }
    }

    // Generates a morphed image given a source image and from/to vertices
    BufferedImage generateMorph(BufferedImage image, ArrayList<Vertex> from, ArrayList<Vertex> to) {
        // Create a result image buffer to store our morph into
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < triangles.size(); i += 3) {
            Vertex[] source = new Vertex[3];
            Vertex[] destination = new Vertex[3];

            int i1 = triangles.get(i);
            int i2 = triangles.get(i + 1);
            int i3 = triangles.get(i + 2);

            source[0] = from.get(i1).scale(width, height);
            source[1] = from.get(i2).scale(width, height);
            source[2] = from.get(i3).scale(width, height);

            destination[0] = to.get(i1).scale(width, height);
            destination[1] = to.get(i2).scale(width, height);
            destination[2] = to.get(i3).scale(width, height);

            MorphGridRendering.MorphTriangle(image, result, source, destination);
        }

        return result;
    }

    // Overlay the end image over the start image with a given alpha value
    BufferedImage overlayImage(BufferedImage startImage, BufferedImage endImage, float alpha) {
        BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2D = frame.createGraphics();

        if (startImage != null)
            g2D.drawImage(startImage, 0, 0, width, height, null);

        if (endImage != null) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2D.setComposite(ac);

            g2D.drawImage(endImage, 0, 0, width, height, null);
        }

        return frame;
    }
}