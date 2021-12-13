public class Vertex {
    public float x;
    public float y;

    Vertex() {
    }

    Vertex(float x, float y) {
        setPosition(x, y);
    }

    void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    Vertex add(Vertex other) {
        return new Vertex(this.x + other.x, this.y + other.y);
    }
}
