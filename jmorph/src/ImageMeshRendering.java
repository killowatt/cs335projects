import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

// Image mesh rendering class, helper since we need to render grids in both the morph preview and image mesh classes
public class ImageMeshRendering {
    static final int HANDLE_SIZE = 6;
    static final int HANDLE_RADIUS = HANDLE_SIZE / 2;

    // Helper method to draw a single scaled line between two points
    static void DrawLine(Graphics g, Dimension d, Point2D first, Point2D second) {
        int firstX = (int) (first.getX() * d.getWidth());
        int firstY = (int) (first.getY() * d.getHeight());

        int secondX = (int) (second.getX() * d.getWidth());
        int secondY = (int) (second.getY() * d.getHeight());

        g.drawLine(firstX, firstY, secondX, secondY);
    }

    // Primary method for rendering our grids given a set of vertices and triangle indices
    public static void DrawMesh(Graphics g, Dimension d, ArrayList<Point2D> vertices, ArrayList<Integer> triangles) {
        // Make our triangles gray
        g.setColor(Color.gray);

        // For each set of three indices making a triangle...
        for (int t = 0; t < triangles.size(); t += 3) {
            // Acquire the three points for our triangle given indices
            Point2D firstVertex = vertices.get(triangles.get(t));
            Point2D secondVertex = vertices.get(triangles.get(t + 1));
            Point2D thirdVertex = vertices.get(triangles.get(t + 2));

            // Draw the lines between each of our points
            DrawLine(g, d, firstVertex, secondVertex);
            DrawLine(g, d, secondVertex, thirdVertex);
            DrawLine(g, d, thirdVertex, firstVertex);
        }
    }

    // Draw handle method for our grids, used to keep handle drawing consistent
    public static void DrawHandle(Graphics g, Dimension size, Point2D vertex) {
        // Get their location
        double x = vertex.getX();
        double y = vertex.getY();

        // Scale that by our rendering area size
        double scaledX = x * size.getWidth();
        double scaledY = y * size.getHeight();

        // Ignore points that are on the boundary
        if (x > 0.0 && x < 1.0 && y > 0.0 && y < 1.0) {
            // Draw any handles on the interior of the mesh at our scaled location
            g.drawOval((int) scaledX - HANDLE_RADIUS, (int) scaledY - HANDLE_RADIUS, HANDLE_SIZE, HANDLE_SIZE);
        }
    }
}
