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

    public ImageMesh other;

    ArrayList<Point2D> vertices;
    ArrayList<Integer> triangles;

    public int subs = 5;

    Point2D selected = null;
    int sidx = -1;

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

                for (int i = 0; i < vertices.size(); i++) {
                    Point2D point = vertices.get(i);

                    double scaleX = getSize().width;
                    double scaleY = getSize().height;

                    Point2D scaled = new Point2D.Double(point.getX() * scaleX, point.getY() * scaleY);

                    if (e.getPoint().distance(scaled) <= 3.0) {
                        selected = point;

                        other.sidx = i;
                        other.repaint();

                        return;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //super.mouseReleased(e);
                selected = null;
                repaint();

                other.sidx = -1;
                other.repaint();
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

    void setGridSize(int size) {
        if (size == subs)
            return; // ignore

        subs = size;
        generateHandles();
        repaint();
    }

    int getGridSize() {
        return subs;
    }

    void generateHandles() {
        vertices.clear();
        triangles.clear();

        final int N = subs;

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension d = getSize();

        if (ourimg != null) {
            g.drawImage(ourimg, 0, 0, d.width, d.height, null);
        }

        GridUtilities.DrawGrid(g, d, vertices, triangles);

        for (int i = 0; i < vertices.size(); i++) {
            Point2D p = vertices.get(i);

            if (p == selected || i == sidx)
                g.setColor(Color.orange);
            else
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
