// Vertex class used in place of Point2D for a less obtuse interface
public class Vertex {
    public float x;
    public float y;

    // Default constructor
    Vertex() {
    }

    // Constructor with initial values
    Vertex(float x, float y) {
        setPosition(x, y);
    }

    // Sets the position of both points in the vertex
    void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    Vertex copy() { return new Vertex(x, y); }

    // Returns a scaled copy of this vertex
    Vertex scale(float scalarX, float scalarY) {
        return new Vertex(x * scalarX, y * scalarY);
    }
}
