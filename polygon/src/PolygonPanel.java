import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PolygonPanel extends JPanel {
    // Size and half-size of handle constants, in pixels
    private final int HANDLE_SIZE = 12;
    private final int HANDLE_OFFSET = HANDLE_SIZE / 2;

    // Number of segments to be drawn for each spline segment
    private final int SPLINE_INTERVALS = 32;

    // Our list of handles represented by rectangles, and the current handle
    private final ArrayList<Rectangle> handles;
    private Rectangle currentHandle = null;

    // The current color of the polygon/b-spline
    private Color currentColor = Color.red;

    // Whether to show and/or fill the polygon, if drawn
    private boolean showPolygon = true;
    private boolean fillPolygon = false;

    // Options to show the b-spline, centroid, and handles
    private boolean showSpline = true;
    private boolean showCentroid = true;
    private boolean showHandles = true;

    // The current centroid position
    private int centroidX = 0;
    private int centroidY = 0;

    // Timer and timer task used for animation
    private Timer timer;
    private TimerTask timerTask;

    // Current animation playback & rotation state
    private boolean animating = false;
    private double rotation = 0.0;

    // Constructor for polygon panel
    PolygonPanel() {
        super();

        // Create our handles and our timer
        handles = new ArrayList<>();
        timer = new Timer();

        // Set the background to black and our default size to 256x256
        setBackground(Color.black);
        setPreferredSize(new Dimension(256, 256));

        // Set up our handle press & release
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // If we're currently animating, just ignore any clicks
                if (animating)
                    return;

                // X and Y coordinates of this mouse click
                final int x = e.getX();
                final int y = e.getY();

                // Check if any of the current handle rectangles contain the point this mouse clicked
                for (Rectangle rectangle : handles) {
                    if (rectangle.contains(x, y)) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            // If we left-clicked a handle, set it as our current handle
                            currentHandle = rectangle;
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            // If we right-clicked a handle, remove it from our list of handles
                            handles.remove(rectangle);

                            // Then make sure to recalculate the centroid and repaint
                            calculateCentroid();
                            repaint();
                        }
                        // We clicked a handle, so end this click here
                        return;
                    }
                }

                // If this is a left click, add a new handle/point to our polygon, recalculate the centroid and repaint
                if (SwingUtilities.isLeftMouseButton(e)) {
                    handles.add(new Rectangle(x, y, HANDLE_SIZE, HANDLE_SIZE));

                    // Make sure to recalculate centroid and repaint
                    calculateCentroid();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Whenever the mouse is released, just un-set the current handle
                currentHandle = null;
            }
        });

        // Set up a listener for handle dragging
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // If we have no current handle or are animating, ignore the drag
                if (currentHandle == null || animating)
                    return;

                // Otherwise, set the handle position to the mouse position minus half the size of the handle
                currentHandle.x = e.getX() - HANDLE_OFFSET;
                currentHandle.y = e.getY() - HANDLE_OFFSET;

                // Then recalculate the centroid and repaint
                calculateCentroid();
                repaint();
            }
        });
    }

    // Clears the current polygon of all points
    void clearPolygon() {
        // Clear our handles
        handles.clear();

        // Then simply recalculate the (empty) centroid and repaint
        calculateCentroid();
        repaint();
    }

    void startAnimation() {
        // We have a task already, so we must already be animating, so ignore this command
        if (timerTask != null)
            return;

        // Set up initial animation state
        animating = true;
        rotation = 0.0;

        // Create our timer task that will increment our rotation
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Per tick, add one degree of rotation
                rotation += 2.0 * Math.PI / 360.0;

                // Once we have made a full rotation...
                if (rotation >= 2.0 * Math.PI) {
                    // Stop our animation state
                    animating = false;

                    // Then cancel this task and set it to null
                    timerTask.cancel();
                    timerTask = null;
                }

                // Finally, repaint the panel
                repaint();
            }
        };

        // Schedule for 120 frames a second
        timer.schedule(timerTask, 0, 1000 / 120);
    }

    void stopAnimation() {
        // If we have no timer task, we aren't animating, so ignore this command
        if (timerTask == null)
            return;

        // Cancel the existing timer task and set it to null
        timerTask.cancel();
        timerTask = null;

        // Reset our animation state back to default and repaint
        rotation = 0.0;
        animating = false;
        repaint();
    }

    // Returns the show polygon value
    boolean isShowingPolygon() {
        return showPolygon;
    }

    // Returns the show spline value
    boolean isShowingSpline() {
        return showSpline;
    }

    // Returns the fill polygon value
    boolean isPolygonFilled() {
        return fillPolygon;
    }

    // Returns the show handles value
    boolean isShowingHandles() {
        return showHandles;
    }

    // Returns the show centroid value
    boolean isShowingCentroid() {
        return showCentroid;
    }

    // Returns the current polygon/b-spline color
    Color getCurrentColor() {
        return currentColor;
    }

    // Sets whether to show the polygon and repaints
    void setShowPolygon(boolean value) {
        showPolygon = value;
        repaint();
    }

    // Sets whether to show the b-spline and repaints
    void setShowSpline(boolean value) {
        showSpline = value;
        repaint();
    }

    // Sets whether to fill the polygon if drawn and repaints
    void setPolygonFill(boolean value) {
        fillPolygon = value;
        repaint();
    }

    // Sets the current polygon/b-spline color and repaints
    void setCurrentColor(Color color) {
        currentColor = color;
        repaint();
    }

    // Sets whether to draw the handles and repaints
    void setShowHandles(boolean value) {
        showHandles = value;
        repaint();
    }

    // Sets whether we should draw the centroid and repaints
    void setShowCentroid(boolean value) {
        showCentroid = value;
        repaint();
    }

    // Helper method to calculate the centroid
    private void calculateCentroid() {
        // If we don't have at least one handle, ignore
        if (handles.size() <= 0) {
            return;
        }

        // Reset our centroid to zero
        centroidX = 0;
        centroidY = 0;

        // Then add up each of our handle's positions + center offset
        for (Rectangle rectangle : handles) {
            centroidX += rectangle.x + HANDLE_OFFSET;
            centroidY += rectangle.y + HANDLE_OFFSET;
        }

        // Finally, divide the result by the size of our handles array
        centroidX /= handles.size();
        centroidY /= handles.size();
    }

    // Paint component method, handles drawing based on current panel state
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // If we are animating, make sure to set up our transformation correctly
        if (animating) {
            Graphics2D g2D = (Graphics2D) g;

            // Translate to our centroid, do our rotation, then return to where we were
            g2D.translate(centroidX, centroidY);
            g2D.rotate(rotation);
            g2D.translate(-centroidX, -centroidY);
        }

        g.setColor(currentColor);

        // If show polygon is set, draw our polygon
        if (showPolygon) {
            drawPolygon(g);
        }

        // If show spline is set, draw our b-spline
        if (showSpline)
            drawBSpline(g);

        // Draw our handles & centroid last, since we want them to appear over the polygon/b-spline
        // We only draw handles & centroids if we aren't animating because they get in the way of our animation
        if (!animating) {
            // Use a different color
            g.setColor(Color.cyan);

            // If show handles is set, draw each handle at their respective position
            if (showHandles) {
                for (Rectangle rectangle : handles) {
                    g.drawRect(rectangle.x, rectangle.y, HANDLE_SIZE, HANDLE_SIZE);
                }
            }

            // If show centroid is set and we have at least one handle, draw the centroid
            if (showCentroid && handles.size() > 0) {
                // We offset the location so that the centroid square is properly centered
                g.drawRect(centroidX - 2, centroidY - 2, 4, 4);
            }
        }
    }

    // Helper method that handles drawing our basic polygon
    private void drawPolygon(Graphics g) {
        // If we don't have at least three handles/points, don't draw
        if (handles.size() <= 2)
            return;

        // Create x and y arrays at the same size of our handles array
        // We could try to reuse these arrays across paints, but I am lazy, and it still performs fine
        int[] x = new int[handles.size()];
        int[] y = new int[handles.size()];

        // Then, for each handle, set up the corresponding point
        for (int i = 0; i < handles.size(); i++) {
            Rectangle rectangle = handles.get(i);

            // We use an offset since the handles are supposed to be centered on their "point"
            x[i] = rectangle.x + HANDLE_OFFSET;
            y[i] = rectangle.y + HANDLE_OFFSET;
        }

        // Draw an outline or a filled polygon based on the current fill polygon setting
        if (!fillPolygon)
            g.drawPolygon(x, y, handles.size());
        else
            g.fillPolygon(x, y, handles.size());
    }

    // Helper method that handles drawing our b-spline
    private void drawBSpline(Graphics g) {
        // We want at least 4 points to draw our b-spline
        if (handles.size() < 4)
            return;

        // Set up our arrays for our x and y points like in our polygon draw method
        int[] x = new int[handles.size()];
        int[] y = new int[handles.size()];

        // Then, paint each segment of our b-spline using our helper method
        for (int segment = 0; segment < handles.size(); segment++) {
            for (int count = 0, i = segment; count < handles.size(); count++, i = (i + 1) % handles.size()) {
                x[count] = handles.get(i).x + HANDLE_OFFSET;
                y[count] = handles.get(i).y + HANDLE_OFFSET;
            }
            paintSegment(g, x, y);
        }
    }

    // Helper method for painting one segment of a b-spline
    private void paintSegment(Graphics g, int[] xp, int[] yp) {
        // Code based off practicum week 7 (dancing loop)
        // must have at least 4 points
        if (handles.size() < 4)
            return;

        // Values used for drawing this spline segment
        double x, y, xOld, yOld, A, B, C;
        double t1, t2, t3;
        double deltaX1, deltaX2, deltaX3;
        double deltaY1, deltaY2, deltaY3;

        // This is forward differencing code to draw fast Bspline
        t1 = 1.0 / SPLINE_INTERVALS;
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

        // Set up our line stroke
        Graphics2D g2D = (Graphics2D) g;
        g2D.setStroke(new BasicStroke(3));
        g2D.setColor(currentColor);

        // Draw SPLINE_INTERVALS line segments for this b-spline segment
        for (int i = 0; i < SPLINE_INTERVALS; i++) {
            // Increment x, y, deltas appropriately
            x += deltaX1;
            deltaX1 += deltaX2;
            deltaX2 += deltaX3;

            y += deltaY1;
            deltaY1 += deltaY2;
            deltaY2 += deltaY3;

            // Finally, draw this segment
            g2D.drawLine((int) xOld, (int) yOld, (int) x, (int) y);
            xOld = x;
            yOld = y;
        }

        // Reset our line stroke
        g2D.setStroke(new BasicStroke(1));
    }
}
