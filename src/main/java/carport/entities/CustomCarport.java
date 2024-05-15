package carport.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomCarport {
    /* ALL MEASUREMENTS IN MM */
    public int SPÆR_DISTANCE_MAX = 600;
    public int STOLPE_DISTANCE_MAX = 3000;
    public int PARKING_WIDTH_MIN = 2000;
    public int PARKING_LENGTH_MIN = 4000;
    public int UDHÆNG_LENGTH_X = 500;
    public int UDHÆNG_LENGTH_Y = 500;

    public int LenX;
    public int LenY;
    public int LenZ;

    private List<rectangle> rectangles;
    private List<rectangle> stolper;
    private rectangle shed;

    private int sLenX;
    private int sLenY;

    // TODO: sanity checking
    public boolean Make(int stolpeLenX, int stolpeLenY,
            int remLenX, int remLenY,
            int spærLenX, int spærLenY,
            int shedLenX, int shedLenY) {
        sLenX = stolpeLenX;
        sLenY = stolpeLenY;
        shed = (shedLenX > 0 && shedLenY > 0) ? new rectangle(new point(sLenX, sLenY), shedLenX, shedLenY) : null;

        int x, y, i, j;
        rectangles = new ArrayList<>();
        stolper = new ArrayList<>();
        x = stolpeLenX;
        while (x < LenX) {
            int rectangleLenX = (x + sLenX + STOLPE_DISTANCE_MAX >= LenX) ? LenX - x - sLenX : STOLPE_DISTANCE_MAX;
            rectangles.add(new rectangle(new point(x, sLenY), rectangleLenX, LenY));
            x += STOLPE_DISTANCE_MAX + sLenX;
        }
        System.err.println("Rectangles : " + rectangles.toString());
        adjustRectangles();
        if (shed != null) {
            System.err.println("Shed : " + shed.toString());
            System.err.println("Adj. rectangles : " + rectangles.toString());
        }
        for (rectangle r : rectangles){
            List<rectangle> s = getStolper(r);
            for (rectangle rs : s)
                stolper.add(rs);
        }
        List<rectangle> s = getStolper(shed);
        for (rectangle rs : s)
            stolper.add(rs);

        return true;
    }

    /* SVG */
    public String svgDraw() {
        int defaultStrokeWidth = 4;
        String defaultStrokeColor = "black";
        int defaultShedStrokeWidth = 8;
        String defaultShedStrokeColor = "red";

        String svg = "<svg></svg>";

        String shedSvg = shed.toSvg(null);
        String[] rectangleSvgs = new String[rectangles.size()];
        String[] stolpeSvgs = new String[stolper.size()];
        for (int i = 0; i < rectangleSvgs.length; ++i) {
            String bSvg = rectangles.get(i).toSvg(null);
            rectangleSvgs[i] = bSvg;
        }
        for (int i = 0; i < stolpeSvgs.length; ++i) {
            String bSvg = stolper.get(i).toSvg(null);
            stolpeSvgs[i] = bSvg;
        }

        svg = svgAddObject(svg, shedSvg);
        svg = svgAddObject(svg, rectangleSvgs);
        svg = svgAddObject(svg, stolpeSvgs);

        svg = svgSetViewBox(svg, 0 - UDHÆNG_LENGTH_X, 0, LenX + 2 * UDHÆNG_LENGTH_X, LenY + UDHÆNG_LENGTH_Y);

        svg = svgAddLenX(svg, 0 - UDHÆNG_LENGTH_X, LenX + 2 * UDHÆNG_LENGTH_X);
        svg = svgAddLenX(svg, 0, LenX);
        svg = svgAddLenX(svg, 500, 150);

        return svg;
    }

    private String svgDrawPolygon(String style, point... points){
        if (style == null)
            style = "fill:none;stroke:black;stroke-width:6";
        String poly = "";
        for (point p : points)
            poly += String.format(" %d, %d ", p.x, p.y);
        return String.format("<polygon points=\"%s\" style=\"%s\"/>", poly, style);
    }

    private String svgSetTextStyle(String svg, String className, String... args){
        String classStyle = "";
        for (String s : args)
            classStyle += String.format("%n%s;", s);
        if (svg.contains("<style>") && svg.contains("</style>")) {
            String svgStart = svg.substring(0, svg.indexOf("</style>"));
            String svgEnd = svg.substring(svg.indexOf("</style>"));
            svg = String.format("%s .%s { %s } %s", svgStart, className, classStyle, svgEnd);
        }
        else{
            String svgStart = svg.substring(0, svg.indexOf(">") + 1);
            String svgEnd = svg.substring(svgStart.length());
            svg = String.format("%s <style> .%s { %s } </style> %s",
                                svgStart, className, classStyle, svgEnd);
        }

        return svg;
    }

    private String svgAddLenX(String svg, int startX, int lenX){
        int h = 20;
        int arrowLen = 40;

        rectangle viewBox = svgGetDims(svg);

        rectangle ruler = new rectangle(startX + arrowLen, viewBox.Corners[2].y, lenX - 2 * arrowLen, h);
        String text = String.format("<text x=\"%d\" y=\"%d\" class=\"svgTextClass\">%3.3f m</text>",
                                    ruler.Corners[0].x + ruler.LenX / 2,
                                    ruler.Corners[0].y - h, ((float) lenX) / 1000.0f);
        String lArrow = svgDrawPolygon(null, ruler.Corners[0],
                                       ruler.Corners[2],
                                       new point(ruler.Corners[0].x - arrowLen,
                                                 ruler.Corners[0].y + ruler.LenY / 2));
        String rArrow = svgDrawPolygon(null, ruler.Corners[1],
                                       ruler.Corners[3],
                                       new point(ruler.Corners[1].x + arrowLen,
                                                 ruler.Corners[1].y + ruler.LenY / 2));

        svg = svgAddObject(svg, ruler.toSvg(null), text, lArrow, rArrow);
        svg = svgSetViewBox(svg, viewBox.Corners[0].x, viewBox.Corners[0].y, viewBox.LenX, viewBox.LenY + 10 * h);
        svg = svgSetTextStyle(svg, "svgTextClass", String.format("font: italic %dpx sans-serif", 4 * h));

        return svg;
    }

    private rectangle svgGetDims(String svg){
        int viewBoxIndexStart = svg.indexOf("\"");
        int viewBoxIndexEnd = svg.substring(viewBoxIndexStart + 1).indexOf("\"") + viewBoxIndexStart + 1;
        String dimsStr = svg.substring(viewBoxIndexStart + 1, viewBoxIndexEnd);
        String[] dimsStrSplit = dimsStr.split(" ");
        rectangle b = new rectangle(Integer.parseInt(dimsStrSplit[0]),
                            Integer.parseInt(dimsStrSplit[1]),
                            Integer.parseInt(dimsStrSplit[2]),
                            Integer.parseInt(dimsStrSplit[3]));
        return b;
    }

    private String svgSetViewBox(String svg, int x, int y, int lenX, int lenY){
        svg = svg.substring(svg.indexOf(">") + 1);
        return String.format("<svg viewBox=\"%d %d %d %d\">", x, y, lenX, lenY) + svg;
    }

    private String svgAddObject(String svg, String... objs) {
        return svg.replace("</svg>", " ") + String.join(" ", objs) + "</svg>";
    }

    private String svgObjectAddAttributes(String obj, String... args) {
        return obj.replace("/>", " ") + String.join(" ", args) + "/>";
    }
    /* INTERNAL HELPERS */

    private void adjustRectangles() {
        if (shed == null || rectangles == null || rectangles.size() < 1)
            return;
        for (int i = 0; i < rectangles.size(); ++i) {
            rectangle b = rectangles.get(i);
            if (shed.Overlaps(b)) {
                if (b.LenY - shed.LenY - sLenY > 0) // TODO FIX THIS, IT ONLY CROPS FIRST RECTANGLE ATM
                    rectangles.add(new rectangle(b.Corners[0].x, shed.Corners[2].y + sLenY,
                                                 b.LenX, b.LenY - shed.LenY - sLenY));
                if (b.Corners[1].x - shed.Corners[1].x - sLenX > 0) // TODO: might need to fine tune ?
                    rectangles.add(new rectangle(shed.Corners[1].x + sLenX, sLenY,
                                                 b.Corners[1].x - shed.Corners[1].x - sLenX, shed.LenY));
                rectangles.remove(b);
            }
        }
    }

    private List<rectangle> getStolper(rectangle r){
        List<rectangle> stolper = new ArrayList<>();

        int x, y;
        for (x = r.Corners[0].x; x <= r.LenX + r.Corners[0].x; x += STOLPE_DISTANCE_MAX + sLenX){
            stolper.add(new rectangle(x - sLenX, r.Corners[0].y - sLenY, sLenX, sLenY));
            if (x + STOLPE_DISTANCE_MAX + sLenX > r.Corners[1].x &&
                x < r.LenX)
                x = r.LenX + r.Corners[0].x;
        }

        return stolper;
    }

    private class rectangle {
        public point[] Corners; // NW, NE, SW, SE
        public int LenX;
        public int LenY;

        public rectangle(point corner, int lenX, int lenY) {
            Corners = new point[4];
            Corners[0] = corner;
            Corners[1] = new point(corner.x + lenX, corner.y);
            Corners[2] = new point(corner.x, corner.y + lenY);
            Corners[3] = new point(corner.x + lenX, corner.y + lenY);
            LenX = lenX;
            LenY = lenY;

        }

        public rectangle(int cornerx, int cornery, int lenX, int lenY) {
            Corners = new point[4];
            Corners[0] = new point(cornerx, cornery);
            Corners[1] = new point(cornerx + lenX, cornery);
            Corners[2] = new point(cornerx, cornery + lenY);
            Corners[3] = new point(cornerx + lenX, cornery + lenY);
            LenX = lenX;
            LenY = lenY;
        }



        public boolean IsParkingArea() {
            return (LenX >= PARKING_WIDTH_MIN && LenY >= PARKING_LENGTH_MIN);
        }

        public boolean Overlaps(rectangle b) {
            return !(this.Corners[0].x > b.Corners[3].x ||
                    this.Corners[0].y > b.Corners[3].y ||
                    this.Corners[3].x < b.Corners[0].x ||
                    this.Corners[3].y < b.Corners[0].y);
        }

        @Override
        public String toString() {
            return "rectangle [Corners=" + Arrays.toString(Corners) + ", LenX=" + LenX + ", LenY=" + LenY + "]";
        }

        public String toSvg(String style) {
            if (style == null)
                style = "fill:none;stroke:black;stroke-width:6";
            return String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"%s\"/>",
                                 Corners[0].x, Corners[0].y, LenX, LenY, style);
        }
    }

    private class point {
        public int x;
        public int y;

        public point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "point [x=" + x + ", y=" + y + "]";
        }
        public point AdjustNew(int x, int y){
            return new point(this.x + x, this.y + y);
        }
    }

}
