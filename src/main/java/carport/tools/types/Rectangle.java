package carport.tools.types;

import java.util.Arrays;

public class Rectangle
{

    public Point[] Corners; // NW, NE, SW, SE
    public int LenX;
    public int LenY;

    public Rectangle(Point corner, int lenX, int lenY)
    {
        Corners = new Point[4];
        Corners[0] = corner;
        Corners[1] = new Point(corner.x + lenX, corner.y);
        Corners[2] = new Point(corner.x, corner.y + lenY);
        Corners[3] = new Point(corner.x + lenX, corner.y + lenY);
        LenX = lenX;
        LenY = lenY;

    }

    public Rectangle(int cornerx, int cornery, int lenX, int lenY)
    {
        Corners = new Point[4];
        Corners[0] = new Point(cornerx, cornery);
        Corners[1] = new Point(cornerx + lenX, cornery);
        Corners[2] = new Point(cornerx, cornery + lenY);
        Corners[3] = new Point(cornerx + lenX, cornery + lenY);
        LenX = lenX;
        LenY = lenY;
    }

    public boolean IsParkingArea(int PARKING_WIDTH_MIN, int PARKING_LENGTH_MIN)
    {
        return (LenX >= PARKING_WIDTH_MIN && LenY >= PARKING_LENGTH_MIN);
    }

    public boolean Overlaps(Rectangle b)
    {
        return !(this.Corners[0].x > b.Corners[3].x ||
                this.Corners[0].y > b.Corners[3].y ||
                this.Corners[3].x < b.Corners[0].x ||
                this.Corners[3].y < b.Corners[0].y);
    }

    @Override
    public String toString()
    {
        return "rectangle [Corners=" + Arrays.toString(Corners) + ", LenX=" + LenX + ", LenY=" + LenY + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        Rectangle r = (Rectangle) o;
        return this.Corners[0].x == r.Corners[0].x &&
                this.Corners[0].y == r.Corners[0].y &&
                this.LenX == r.LenX &&
                this.LenY == r.LenY;

    }

    public String toSvg(String style)
    {
        if (style == null)
            style = "fill:none;stroke:black;stroke-width:6";
        return String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"%s\"/>",
                Corners[0].x, Corners[0].y, LenX, LenY, style);
    }
}
