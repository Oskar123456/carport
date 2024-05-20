package carport.tools;

import carport.tools.types.Point;
import carport.tools.types.Rectangle;

public class SvgHelper
{


    public static String svgDrawPolygon(String className, Point... points)
    {
        String poly = "";
        for (Point p : points)
            poly += String.format(" %d, %d ", p.x, p.y);
        return String.format("<polygon class=\"%s\" points=\"%s\"/>", className, poly);
    }

    public static String svgText(String className, int x, int y, String text)
    {
        return String.format("<text class=\"%s\" x=\"%d\" y=\"%d\">%s</text>",
                className, x, y, text);
    }

    public static String svgSetClassStyle(String svg, String className, String... args)
    {
        String classStyle = "";
        for (String s : args)
            classStyle += String.format("%n%s;", s);
        if (svg.contains("<style>") && svg.contains("</style>")) {
            String svgStart = svg.substring(0, svg.indexOf("</style>"));
            String svgEnd = svg.substring(svg.indexOf("</style>"));
            svg = String.format("%s .%s { %s } %s", svgStart, className, classStyle, svgEnd);
        } else {
            String svgStart = svg.substring(0, svg.indexOf(">") + 1);
            String svgEnd = svg.substring(svgStart.length());
            svg = String.format("%s <style> .%s { %s } </style> %s",
                    svgStart, className, classStyle, svgEnd);
        }

        return svg;
    }

    public static String svgAddLenX(String svg, int startX, int lenX, boolean moveRuler)
    {
        int h = 20;
        int arrowLen = 40;

        Rectangle viewBox = svgGetDims(svg);

        Rectangle ruler = new Rectangle(startX + arrowLen, viewBox.Corners[2].y, lenX - 2 * arrowLen, h);
        String text = String.format("<text class=\"textDescr\" x=\"%d\" y=\"%d\">%3.3f m</text>",
                ruler.Corners[0].x + ruler.LenX / 2,
                ruler.Corners[0].y - h, ((float) lenX) / 1000.0f);
        String lArrow = svgDrawPolygon("polyArrow", ruler.Corners[0],
                ruler.Corners[2],
                new Point(ruler.Corners[0].x - arrowLen,
                        ruler.Corners[0].y + ruler.LenY / 2));
        String rArrow = svgDrawPolygon("polyArrow", ruler.Corners[1],
                ruler.Corners[3],
                new Point(ruler.Corners[1].x + arrowLen,
                        ruler.Corners[1].y + ruler.LenY / 2));

        svg = svgAddObject(svg, ruler.toSvg(null), text, lArrow, rArrow);
        if (moveRuler)
            svg = svgSetViewBox(svg, viewBox.Corners[0].x, viewBox.Corners[0].y, viewBox.LenX, viewBox.LenY + 10 * h);

        return svg;
    }

    public static String svgAddLenY(String svg, int startY, int lenY, boolean moveRuler)
    {
        int h = 20;
        int arrowLen = 40;

        Rectangle viewBox = svgGetDims(svg);

        Rectangle ruler = new Rectangle(viewBox.Corners[0].x - 10 * h, startY + arrowLen, h, lenY - 2 * arrowLen);
        String text = String.format("<text class=\"textDescr\" x=\"%d\" y=\"%d\">%3.3f m</text>",
                ruler.Corners[0].x + ruler.LenX * 2,
                ruler.Corners[0].y + ruler.LenY / 2, ((float) lenY) / 1000.0f);
        String tArrow = svgDrawPolygon("polyArrow", ruler.Corners[0], ruler.Corners[1],
                new Point(ruler.Corners[0].x + ruler.LenX / 2,
                        ruler.Corners[0].y - arrowLen));
        String bArrow = svgDrawPolygon("polyArrow", ruler.Corners[2], ruler.Corners[3],
                new Point(ruler.Corners[2].x + ruler.LenX / 2,
                        ruler.Corners[2].y + arrowLen));
        svg = svgAddObject(svg, ruler.toSvg(null), text, tArrow, bArrow);
        if (moveRuler)
            svg = svgSetViewBox(svg, viewBox.Corners[0].x - 20 * h, viewBox.Corners[0].y, viewBox.LenX + 20 * h,
                    viewBox.LenY);

        return svg;
    }

    public static Rectangle svgGetDims(String svg)
    {
        int viewBoxIndexStart = svg.indexOf("\"");
        int viewBoxIndexEnd = svg.substring(viewBoxIndexStart + 1).indexOf("\"") + viewBoxIndexStart + 1;
        String dimsStr = svg.substring(viewBoxIndexStart + 1, viewBoxIndexEnd);
        String[] dimsStrSplit = dimsStr.split(" ");
        Rectangle b = new Rectangle(Integer.parseInt(dimsStrSplit[0]),
                Integer.parseInt(dimsStrSplit[1]),
                Integer.parseInt(dimsStrSplit[2]),
                Integer.parseInt(dimsStrSplit[3]));
        return b;
    }

    public static String svgSetViewBox(String svg, int x, int y, int lenX, int lenY)
    {
        svg = svg.substring(svg.indexOf(">") + 1);
        return String.format("<svg viewBox=\"%d %d %d %d\">", x, y, lenX, lenY) + svg;
    }

    public static String svgAddObject(String svg, String... objs)
    {
        return svg.replace("</svg>", " ") + String.join(" ", objs) + "</svg>";
    }

    public static String svgObjectAddAttributes(String obj, String... args)
    {
        return obj.replace("/>", " ") + String.join(" ", args) + "/>";
    }
}
