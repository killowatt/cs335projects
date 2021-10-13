import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class PolygonPanel extends JPanel {
    ArrayList<Rectangle> handles;
    boolean fill = false;

    Color currentColor = Color.red;

    boolean showPolygon = true;
    boolean showSpline = true;

    int splineIntervals = 32;

    Rectangle currentHandle = null;

    boolean animating = false;
    double rotation = 0.0;

    private int centroidX = 0;
    private int centroidY = 0;

    final int HANDLE_SIZE = 12;
    final int HANDLE_OFFSET = HANDLE_SIZE / 2;

    void setCurrentColor(Color color) {
        currentColor = color;
        repaint();
    }

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
                    if (rectangle.contains(x, y)) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            currentHandle = rectangle;
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            handles.remove(rectangle);
                            repaint();
                        }

                        return;
                    }
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    handles.add(new Rectangle(x, y, HANDLE_SIZE, HANDLE_SIZE));
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

                currentHandle.x = e.getX() - HANDLE_OFFSET;
                currentHandle.y = e.getY() - HANDLE_OFFSET;

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (handles.size() >= 3 && showPolygon) {
            if (animating) {
                // TODO: move centroid calc
                int totalX = 0;
                int totalY = 0;

                for (Rectangle rectangle : handles) {
                    totalX += rectangle.x + HANDLE_OFFSET;
                    totalY += rectangle.y + HANDLE_OFFSET;
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

                x[i] = rectangle.x + HANDLE_OFFSET;
                y[i] = rectangle.y + HANDLE_OFFSET;
            }

            g.setColor(currentColor);
            if (!fill)
                g.drawPolygon(x, y, handles.size());
            else
                g.fillPolygon(x, y, handles.size());
        }

        if (showSpline)
            drawBSpline(g);

        // We want handles to appear over the polygon
        if (!animating) {
            g.setColor(Color.cyan);
            for (Rectangle rectangle : handles) {
                g.drawRect(rectangle.x, rectangle.y, HANDLE_SIZE, HANDLE_SIZE);
            }
        }
    }

    void drawBSpline(Graphics g) {
        if (handles.size() < 4)
            return;

        int[] xp = new int[handles.size()];
        int[] yp = new int[handles.size()];

        for (int segment = 0; segment < handles.size(); segment++) {
            for (int count = 0, i = segment; count < handles.size(); count++, i = (i + 1) % handles.size()) {
                xp[count] = handles.get(i).x + HANDLE_OFFSET;
                yp[count] = handles.get(i).y + HANDLE_OFFSET;
            }
            paintSegment(g, xp, yp);
        }
    }

    void paintSegment(Graphics g, int[] xp, int[] yp) {
        // Code based off practicum week 7 (dancing loop)

        // must have at least 4 points
        if (handles.size() < 4)
            return;

        int count = handles.size();

        double x, y, xOld, yOld, A, B, C;
        double t1, t2, t3;
        double deltaX1, deltaX2, deltaX3;
        double deltaY1, deltaY2, deltaY3;

        // This is forward differencing code to draw fast Bspline
        t1 = 1.0 / splineIntervals;
        t2 = t1 * t1;
        t3 = t2 * t1;

        //  For B-spline curve, "D" is the starting x,y coord
        //  So the first x,y coord is the D term from the cubic equation
        x = (xp[0] + 4.0 * xp[1] + xp[2]) / 6.0;
        y = (yp[0] + 4.0 * yp[1] + yp[2]) / 6.0;
        xOld = x;
        yOld = y;

        // set up deltas for the x-coords of B-spline
        A = (-xp[0] + 3 * xp[1] - 3 * xp[2] + xp[3]) / 6.0;
        B = (3 * xp[0] - 6 * xp[1] + 3 * xp[2]) / 6.0;
        C = (-3 * xp[0] + 3 * xp[2]) / 6.0;

        deltaX1 = A * t3 + B * t2 + C * t1;
        deltaX2 = 6 * A * t3 + 2 * B * t2;
        deltaX3 = 6 * A * t3;

        // set up deltas for the y-coords
        A = (-yp[0] + 3 * yp[1] - 3 * yp[2] + yp[3]) / 6.0;
        B = (3 * yp[0] - 6 * yp[1] + 3 * yp[2]) / 6.0;
        C = (-3 * yp[0] + 3 * yp[2]) / 6.0;

        deltaY1 = A * t3 + B * t2 + C * t1;
        deltaY2 = 6 * A * t3 + 2 * B * t2;
        deltaY3 = 6 * A * t3;

        Graphics2D g2D = (Graphics2D) g;
        g2D.setStroke(new BasicStroke(3));
        g2D.setColor(currentColor);

        for (int i = 0; i < splineIntervals; i++) {
            x += deltaX1;
            deltaX1 += deltaX2;
            deltaX2 += deltaX3;

            y += deltaY1;
            deltaY1 += deltaY2;
            deltaY2 += deltaY3;

            g2D.drawLine((int) xOld, (int) yOld, (int) x, (int) y);
            xOld = x;
            yOld = y;
        }

        g2D.setStroke(new BasicStroke(1));
    }
}
