import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;

// Image mesh panel, handles rendering an image with a triangle grid overlay with controllable handles
public class ImageMesh extends JPanel {
    // The vertices and triangle indices of our grid
    public ArrayList<Vertex> vertices;
    public ArrayList<Integer> triangles;

    // The other image, a bit hacky. We could try using a listener in the future
    public ImageMesh other;

    // Our background image we'll be morphing
    private BufferedImage image;

    // The current size of our grid in one dimension
    private int gridSize = 5;

    // The current brightness of our image with 1.0 being no change
    private float brightness = 1.0f;

    // The currently selected point of this panel
    private Vertex selected = null;

    // The currently selected point of our partner panel
    private int otherSelectedIndex = -1;

    // Image mesh constructor
    ImageMesh() {
        super();

        // Create our vertex and triangle arrays
        vertices = new ArrayList<>();
        triangles = new ArrayList<>();

        // Generate our grid
        generateGrid();

        // Set the background color to black and a reasonable starting size
        setBackground(Color.black);
        setPreferredSize(new Dimension(372, 372));

        // Set up our mouse listener for user clicks
        addMouseListener(new MouseAdapter() {
            // Handler for clicks
            @Override
            public void mousePressed(MouseEvent e) {
                // Get the location of the click
                final int x = e.getX();
                final int y = e.getY();

                // Then, for each vertex...
                for (int i = 0; i < vertices.size(); i++) {
                    // Get that vertex
                    Vertex vertex = vertices.get(i);

                    // Scale it to our component size
                    double scaleX = getSize().width;
                    double scaleY = getSize().height;

                    // Create a point using our scaled location
                    Point2D scaled = new Point2D.Double(vertex.x * scaleX, vertex.y * scaleY);

                    // Then see if the distance between our click and this point is less than our point size
                    // We add one to the radius to make points easier to click
                    if (e.getPoint().distance(scaled) <= MorphGridRendering.HANDLE_RADIUS + 1) {
                        // If so, make this vertex our selected vertex and repaint
                        selected = vertex;
                        repaint();

                        // Inform our partner image mesh and repaint them
                        other.otherSelectedIndex = i;
                        other.repaint();

                        // Exit the loop
                        return;
                    }
                }
            }

            // Handler for mouse release
            @Override
            public void mouseReleased(MouseEvent e) {
                // Set our selected point to null and repaint
                selected = null;
                repaint();

                // Inform our partner image mesh and repaint them
                other.otherSelectedIndex = -1;
                other.repaint();
            }
        });

        // Add a listener for mouse movement
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // If no point is selected, ignore mouse movement
                if (selected == null)
                    return;

                // Otherwise, move our point to the mouse's current location
                float newX = (float)(e.getX() / getSize().getWidth());
                float newY = (float)(e.getY() / getSize().getHeight());
                selected.setPosition(newX, newY);

                // Then finally repaint
                repaint();
            }
        });
    }

    public int getWidthXX() { return image.getWidth(); }

    public int getHeightXX() { return image.getHeight(); }

    public BufferedImage getImage() {
        if (image == null)
            return null;

        RescaleOp op = new RescaleOp(brightness, 0, null);
        return op.filter(image, null);
    }

    float getBrightness() {
        return brightness;
    }

    void setBrightness(float value) {
        if (value == brightness)
            return; // ignore

        brightness = value;
        repaint();
    }

    // Getter for our grid size
    int getGridSize() {
        return gridSize;
    }

    // Setter for our grid size
    void setGridSize(int size) {
        // If our grid size already matches, ignore this command
        if (size == gridSize)
            return;

        // Otherwise update our grid size, regenerate the grid, and repaint
        gridSize = size;
        generateGrid();
        repaint();
    }

    // Helper function that generates the grid in this image mesh
    void generateGrid() {
        // Clear the current vertex and triangle data
        vertices.clear();
        triangles.clear();

        // Generate the vertices of our grid iterating over x and y
        for (int x = 0; x <= gridSize; x++) {
            for (int y = 0; y <= gridSize; y++) {
                // Simply create a point at current x and y divided by grid size
                Vertex vertex = new Vertex((float) x / gridSize, (float) y / gridSize);
                vertices.add(vertex);
            }
        }

        // Generate the indices of our grid, over x and y again
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                //  First and second row points
                int firstRow = y * (gridSize + 1);
                int secondRow = (y + 1) * (gridSize + 1);

                // First half of the quad
                triangles.add(firstRow + x);
                triangles.add(firstRow + x + 1);
                triangles.add(secondRow + x + 1);

                // Second half of the quad
                triangles.add(firstRow + x);
                triangles.add(secondRow + x + 1);
                triangles.add(secondRow + x);
            }
        }
    }

    // Setter for the image background of this image mesh
    public void setImage(BufferedImage image) {
        // If the other image is already set, resize this one to match
        if (image != null && other.image != null) {
            // Create a buffered image matching the size of the other image
            BufferedImage copy = new BufferedImage(other.image.getWidth(), other.image.getHeight(), other.image.getType());
            copy.getGraphics();

            // Get scale factors to match the other image
            float scaleX = (float) other.image.getWidth() / image.getWidth();
            float scaleY = (float) other.image.getHeight() / image.getHeight();

            // Scale the image using a bilinear filter
            AffineTransform scale = AffineTransform.getScaleInstance(scaleX, scaleY);
            AffineTransformOp op = new AffineTransformOp(scale, AffineTransformOp.TYPE_BILINEAR);

            // Set our image to the rescaled version of the input
            this.image = op.filter(image, null);

        } else {
            // If the other image isn't set, just use the image as is
            this.image = image;
        }

        // Re-generate our grid
        generateGrid();

        // If the image isn't null, set our size to match, otherwise use the default
        if (this.image != null)
            setPreferredSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
        else
            setPreferredSize(new Dimension(372, 372));

        // Revalidate and repaint once image is set
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension ps = getParent().getSize();

        if (image == null)
            return ps;

            float panelAspect = (float)ps.getWidth() / (float)ps.getHeight();
            float imageAspect = (float)getWidthXX() / (float)getHeightXX();

            float widthScale = (float)ps.getHeight() / (float)getHeightXX() * getWidthXX();
            float heightScale = (float)ps.getWidth() / (float)getWidthXX() * getHeightXX();

            float nw = panelAspect > imageAspect ? (int)widthScale : (int)ps.getWidth();
            float nh = panelAspect > imageAspect ? (int)ps.getHeight() : (int)heightScale;

            System.out.println(nw + " " + nh);

            return new Dimension((int)nw, (int)nh);
    }

    // Overridden paint component method, renders our image, grid and handles
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int nw = getWidth();
        int nh = getHeight();

        int nx = 0;
        int ny = 0;

        // If we have an image, draw it using our getImage method as the source
        if (image != null) {
            g.drawImage(getImage(), nx, ny, nw, nh, null);
        }

        // The size of our rendering area
        Dimension size = new Dimension(nw, nh);

        // Draw our triangle grid
        Vertex offset = new Vertex(nx / (float)size.getWidth(), ny / (float)size.getHeight());
        MorphGridRendering.DrawMesh(g, size, vertices, triangles);

        // Then, for every point in our grid
        for (int i = 0; i < vertices.size(); i++) {
            // Get our corresponding vertex
            Vertex vertex = vertices.get(i).add(offset);

            // Check if it is the selected on either this image mesh or our partner and set color accordingly
            if (vertex == selected || i == otherSelectedIndex)
                g.setColor(Color.orange);
            else
                g.setColor(Color.cyan);

            // Use our helper to draw this handle for consistency
            MorphGridRendering.DrawHandle(g, size, vertex);
        }
    }
}
