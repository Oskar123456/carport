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

    private List<block> blocks;
    private block shed;

    private int sLenX;
    private int sLenY;

    // TODO: sanity checking
    public boolean Make(int stolpeLenX, int stolpeLenY,
            int remLenX, int remLenY,
            int spærLenX, int spærLenY,
            int shedLenX, int shedLenY) {
        shed = (shedLenX > 0 && shedLenY > 0) ? new block(new point(0, 0), shedLenX, shedLenY) : null;
        sLenX = stolpeLenX;
        sLenY = stolpeLenY;

        int x, y, i, j;
        blocks = new ArrayList<>();
        x = stolpeLenX;
        while (x < LenX) {
            int blockLenX = (x + sLenX + STOLPE_DISTANCE_MAX >= LenX) ? LenX - x - sLenX : STOLPE_DISTANCE_MAX;
            blocks.add(new block(new point(x, 0), blockLenX, LenY));
            x += STOLPE_DISTANCE_MAX + sLenX;
        }
        System.err.println("Blocks : " + blocks.toString());
        adjustBlocks();
        if (shed != null) {
            System.err.println("Shed : " + shed.toString());
            System.err.println("Adj. blocks : " + blocks.toString());
        }

        return true;
    }

    /* SVG */
    public String svgDraw() {
        int defaultStrokeWidth = 4;
        String defaultStrokeColor = "black";
        int defaultShedStrokeWidth = 8;
        String defaultShedStrokeColor = "red";

        String svg = "<svg></svg>";

        String shedSvg = shed.toSvg();
        shedSvg = svgObjectAddAttributes(shedSvg, String.format("stroke=\"%s\"", defaultShedStrokeColor),
                                         String.format("stroke-width=\"%d\"", defaultShedStrokeWidth),
                                         "fill=\"none\"");
        String[] blockSvgs = new String[blocks.size()];
        for (int i = 0; i < blockSvgs.length; ++i) {
            String bSvg = blocks.get(i).toSvg();
            bSvg = svgObjectAddAttributes(bSvg, String.format("stroke=\"%s\"", defaultStrokeColor),
                                          String.format("stroke-width=\"%d\"", defaultStrokeWidth),
                                          "fill=\"none\"");
            blockSvgs[i] = bSvg;
        }

        svg = svgAddObject(svg, shedSvg);
        svg = svgAddObject(svg, blockSvgs);

        svg = svgSetViewBox(svg, 0 - UDHÆNG_LENGTH_X, 0, LenX + 2 * UDHÆNG_LENGTH_X, LenY + UDHÆNG_LENGTH_Y);

        svg = svgAddLenX(svg);

        return svg;
    }

    private String svgAddLenX(String svg){
        block viewBox = svgGetDims(svg);



        return svg;
    }

    private block svgGetDims(String svg){
        int viewBoxIndexStart = svg.indexOf("\"");
        int viewBoxIndexEnd = svg.substring(viewBoxIndexStart + 1).indexOf("\"") + viewBoxIndexStart + 1;
        String dimsStr = svg.substring(viewBoxIndexStart + 1, viewBoxIndexEnd);
        String[] dimsStrSplit = dimsStr.split(" ");
        block b = new block(Integer.parseInt(dimsStrSplit[0]),
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

    private void adjustBlocks() {
        if (shed == null || blocks == null || blocks.size() < 1)
            return;
        for (int i = 0; i < blocks.size(); ++i) {
            block b = blocks.get(i);
            if (shed.Overlaps(b)) {
                if (b.LenY - shed.LenY > 0)
                    blocks.add(new block(b.Corners[0].x, shed.LenY, b.LenX, b.LenY - shed.LenY));
                if (b.Corners[1].x - shed.LenX > 0) // TODO: might need to fine tune ?
                    blocks.add(new block(shed.LenX, 0, b.Corners[1].x - shed.LenX, shed.LenY));
                blocks.remove(b);
            }
        }
    }

    private class block {
        public point[] Corners; // NW, NE, SW, SE
        public int LenX;
        public int LenY;

        public block(point corner, int lenX, int lenY) {
            Corners = new point[4];
            Corners[0] = corner;
            Corners[1] = new point(corner.x + lenX, corner.y);
            Corners[2] = new point(corner.x, corner.y + lenY);
            Corners[3] = new point(corner.x + lenX, corner.y + lenY);
            LenX = lenX;
            LenY = lenY;

        }

        public block(int cornerx, int cornery, int lenX, int lenY) {
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

        public boolean Overlaps(block b) {
            return !(this.Corners[0].x >= b.Corners[3].x &&
                    this.Corners[0].y >= b.Corners[3].y &&
                    this.Corners[3].x <= b.Corners[0].x &&
                    this.Corners[3].y <= b.Corners[0].y);
        }

        @Override
        public String toString() {
            return "block [Corners=" + Arrays.toString(Corners) + ", LenX=" + LenX + ", LenY=" + LenY + "]";
        }

        public String toSvg() {
            return String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\"/>", Corners[0].x, Corners[0].y,
                    LenX, LenY);
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

    }

}
