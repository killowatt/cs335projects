import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GridUtilities {
    static void bro(Graphics g, Dimension d, Point2D first, Point2D second) {
        int x0 = (int)(first.getX() * d.getWidth());
        int y0 = (int)(first.getY() * d.getHeight());

        int x1 = (int)(second.getX() * d.getWidth());
        int y1 = (int)(second.getY() * d.getHeight());

        g.drawLine(x0, y0, x1, y1);
    }

    public static void DrawGrid(Graphics g, Dimension d, ArrayList<Point2D> vertices, ArrayList<Integer> triangles) {
        for (int t = 0; t < triangles.size(); t += 3) {
            g.setColor(Color.gray);

            Point2D firstVertex = vertices.get(triangles.get(t));
            Point2D secondVertex = vertices.get(triangles.get(t + 1));
            Point2D thirdVertex = vertices.get(triangles.get(t + 2));

            bro(g, d, firstVertex, secondVertex);
            bro(g, d, secondVertex, thirdVertex);
            bro(g, d, thirdVertex, firstVertex);
        }
    }
}
