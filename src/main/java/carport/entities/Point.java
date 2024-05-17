package carport.entities;

public class Point
{
    public int x;
    public int y;
    public int z;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "point [x=" + x + ", y=" + y + "]";
    }

    public Point AdjustNew(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }
}
