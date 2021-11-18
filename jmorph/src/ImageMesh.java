import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageMesh extends JPanel {
    BufferedImage ourimg;

    ArrayList<Point2D> vertices;
    ArrayList<Integer> triangles;

    public int subs = 3;

    Point2D selected = null;

    ImageMesh() {
        super();

        vertices = new ArrayList<>();
        triangles = new ArrayList<>();

        generateHandles();

        setBackground(Color.black);
        setPreferredSize(new Dimension(372, 372));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //super.mousePressed(e);
                final int x = e.getX();
                final int y = e.getY();

                for (Point2D point : vertices) {
                    double scaleX = getSize().width;
                    double scaleY = getSize().height;

                    Point2D scaled = new Point2D.Double(point.getX() * scaleX, point.getY() * scaleY);

                    if (e.getPoint().distance(scaled) <= 3.0) {
                        selected = point;
                        return;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //super.mouseReleased(e);
                selected = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //super.mouseDragged(e);
                if (selected == null)
                    return;

                double newX = e.getX() / getSize().getWidth();
                double newY = e.getY() / getSize().getHeight();
                selected.setLocation(newX, newY);

                repaint();
            }
        });
    }

    void generateHandles() {
        vertices.clear();
        triangles.clear();

        final int N = subs + 1;

        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= N; j++) {
                Point2D vertex = new Point2D.Float((float)i / N, (float)j / N);
                vertices.add(vertex);
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int row = j * (N + 1);
                int rowz = (j + 1) * (N + 1);

                triangles.add(row + i);
                triangles.add(row + i + 1);
                triangles.add(rowz + i + 1);

                triangles.add(row + i);
                triangles.add(rowz + i + 1);
                triangles.add(rowz + i);
            }
        }

        System.out.println("Generated " + vertices.size() + " verts with " + triangles.size() + " total tris");
    }

    public void setImage(BufferedImage image) {
        if (image == null)
            return;

        System.out.println("Set image!");
        ourimg = image;

        generateHandles();

        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        revalidate();
        repaint();
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

        if (ourimg != null) {
            g.drawImage(ourimg, 0, 0, d.width, d.height, null);
        }

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
                g.drawOval((int)sX - 3, (int)sY - 3, 6, 6);
            }
        }
    }
}
