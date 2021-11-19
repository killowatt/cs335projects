import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MorphPreview extends JPanel {
    Timer timer;

    ArrayList<Point2D> vertices;
    ArrayList<Integer> triangles;

    float t = 0.0f;

    MorphPreview(ImageMesh first, ImageMesh second, int frames, int delay) {
        setBackground(Color.black);
        setPreferredSize(new Dimension(372, 372));

        // also the first and second image should == size
        // if first =/= second size in verts/tris, complain...

        vertices = new ArrayList<>(first.vertices);
        triangles = new ArrayList<>(first.triangles);

        for (int i = 0; i < vertices.size(); i++) {
            Point2D copy = new Point2D.Double(first.vertices.get(i).getX(),
                    first.vertices.get(i).getY());
            vertices.set(i, copy);
        }

        float step = 1.0f / frames;

        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (int i = 0; i < vertices.size(); i++) {

                    Point2D v0 = first.vertices.get(i);
                    Point2D v1 = second.vertices.get(i);

                    double xi = v0.getX() + (v1.getX() - v0.getX()) * t;
                    double yi = v0.getY() + (v1.getY() - v0.getY()) * t;

                    Point2D p = vertices.get(i);
                    p.setLocation(xi, yi);
                }

                repaint();
                t += step;

                if (t >= 1.0f) {
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    static void bro(Graphics g, Dimension d, Point2D first, Point2D second) {
        int x0 = (int)(first.getX() * d.getWidth());
        int y0 = (int)(first.getY() * d.getHeight());

        int x1 = (int)(second.getX() * d.getWidth());
        int y1 = (int)(second.getY() * d.getHeight());

        g.drawLine(x0, y0, x1, y1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension d = getSize();

        for (int t = 0; t < triangles.size(); t += 3) {
            g.setColor(Color.gray);

            Point2D firstVertex = vertices.get(triangles.get(t));
            Point2D secondVertex = vertices.get(triangles.get(t + 1));
            Point2D thirdVertex = vertices.get(triangles.get(t + 2));

            bro(g, d, firstVertex, secondVertex);
            bro(g, d, secondVertex, thirdVertex);
            bro(g, d, thirdVertex, firstVertex);
        }

        for (Point2D p : vertices) {
            g.setColor(Color.cyan);

            double x = p.getX();
            double y = p.getY();

            double sX = x * d.getWidth();
            double sY = y * d.getHeight();

            if (x > 0.0 && x < 1.0 &&
                    y > 0.0 && y < 1.0) {
                //g.fillRect((int)sX - 3, (int)sY - 3, 6 , 6);
                g.drawOval((int) sX - 3, (int) sY - 3, 6, 6);
            }
        }
    }
}
