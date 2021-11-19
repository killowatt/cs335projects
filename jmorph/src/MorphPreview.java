import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

// Morph Preview panel, handles the morph animation preview
public class MorphPreview extends JPanel {
    // The timer and current time for our animation
    Timer timer;
    float time = 0.0f;

    // The vertices and triangles of our grid
    ArrayList<Point2D> vertices;
    ArrayList<Integer> triangles;

    // Constructor for our morph preview panel
    MorphPreview(ImageMesh first, ImageMesh second, int frames, int delay) {
        // Use a black background and grab the first image's size
        setBackground(Color.black);
        setPreferredSize(first.getSize());

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
                repaint();
                time += timeStep;

                // Once the animation completes, ensure all of our points ended up the same as the second image
                if (time >= 1.0f) {
                    timer.stop();

                    for (int i = 0; i < vertices.size(); i++) {
                        Point2D finalVertex = second.vertices.get(i);

                        double finalX = finalVertex.getX();
                        double finalY = finalVertex.getY();

                        vertices.get(i).setLocation(finalX, finalY);
                    }

                    repaint();
                }
            }
        });

        // Start our timer to play our animation
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
