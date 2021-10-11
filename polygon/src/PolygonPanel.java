import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class PolygonPanel extends JPanel {
    ArrayList<Rectangle> handles;
    boolean fill = false;

    Rectangle currentHandle = null;

    boolean animating = false;
    double rotation = 0.0;

    PolygonPanel() {
        super();

        handles = new ArrayList<>();

        setBackground(Color.black);

        setPreferredSize(new Dimension(256, 256));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (animating)
                    return;

                final int x = e.getX();
                final int y = e.getY();

                for (Rectangle rectangle : handles) {
                    System.out.println(x + " " + y);
                    if (rectangle.contains(x, y)) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            currentHandle = rectangle;
                        }
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            handles.remove(rectangle);
                            repaint();
                        }

                        return;
                    }
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    handles.add(new Rectangle(x, y, 12, 12));
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentHandle = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentHandle == null || animating)
                    return;

                currentHandle.x = e.getX() - 6;
                currentHandle.y = e.getY() - 6;

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handles.size() >= 3) {
            if (animating) {
                // TODO: move centroid calc
                int totalX = 0;
                int totalY = 0;

                for (Rectangle rectangle : handles) {
                    totalX += rectangle.x + 6;
                    totalY += rectangle.y + 6;
                }

                totalX /= handles.size();
                totalY /= handles.size();


                Graphics2D g2D = (Graphics2D) g;
                g2D.translate(totalX, totalY);
                g2D.rotate(rotation);
                g2D.translate(-totalX, -totalY);
            }

            int[] x = new int[handles.size()];
            int[] y = new int[handles.size()];

            for (int i = 0; i < handles.size(); i++) {
                Rectangle rectangle = handles.get(i);

                x[i] = rectangle.x + 6;
                y[i] = rectangle.y + 6;
            }

            g.setColor(Color.red);
            if (!fill)
                g.drawPolygon(x, y, handles.size());
            else
                g.fillPolygon(x, y, handles.size());
        }

        // We want handles to appear over the polygon
        if (!animating) {
            g.setColor(Color.cyan);
            for (Rectangle rectangle : handles) {
                g.drawRect(rectangle.x, rectangle.y, 12, 12);
            }
        }
    }
}
