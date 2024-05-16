package carport.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import carport.exceptions.DatabaseException;
import carport.persistence.ConnectionPool;

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
    private List<rectangle> spaer;
    private List<rectangle> remme;
    private List<rectangle> stern;
    public List<rectangle> tagplader;
    private rectangle shed;

    private int stolpeLenX = 100;
    private int stolpeLenY = 100;
    private int stolpeLenZ = 3000;
    private int remLenX = 40;
    private int remLenY;
    private int remLenZ = 80;
    private int spaerLenX;
    private int spaerLenY = 60;
    private int spaerLenZ = 60;
    private int sternWidth = 40;
    private int sternHeight = 160;
    private int sternLength;
    private int tagpladeLenX = 1000;
    private int tagpladeLenY = 1000;
    private int tagpladeLenZ;

    private Product stolpeProd;
    private Product remProd;
    private Product spaerProd;
    private Product sternProd;
    private Product tagpladeProd;

    public Product GetStolpeProd() {
        return stolpeProd;
    }
    public int GetNStolpeProd(){
        if (stolper == null)
            return 0;
        return stolper.size() * (int) (Math.ceil((double)LenZ / (double)stolpeLenZ));
    }
    public Product GetRemProd() {
        return remProd;
    }
    public int GetNRemProd(){
        if (remme == null)
            return 0;
        return remme.size() * (int) ((Math.ceil(((double)LenY + (double)UDHÆNG_LENGTH_Y) / (double)remLenY)));
    }
    public Product GetSpaerProd() {
        return spaerProd;
    }
    public int GetNSpaerProd(){
        if (spaer == null)
            return 0;
        return spaer.size() * (int) ((Math.ceil(((double)LenX + (double)UDHÆNG_LENGTH_X * 2.0) / (double)spaerLenX)));
    }
    public Product GetSternProd() {
        return sternProd;
    }
    public int GetNSternProd(){
        if (stern == null || shed == null)
            return 0;
        int factorH = ((int) (Math.ceil((double)shed.LenX / ((double)sternLength))) + 1) / 2;
        int factorV = ((int) (Math.ceil((double)shed.LenY / ((double)sternLength))) + 1) / 2;
        return stern.size() * (factorH + factorV);
    }
    public Product GetTagpladeProd() {
        return tagpladeProd;
    }
    public int GetNTagpladeProd(){
        if (tagplader == null)
            return 0;
        return tagplader.size();
    }

    private boolean made;

    public void SetStolpe(ConnectionPool cp, Product s) throws DatabaseException{
        System.err.println(s.toString());
        stolpeProd = s;
        List<ProductSpecification> specs = s.GetSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs){
            System.err.println(ps.Id + " " + ps.Name + " " + ps.Details + " " + ps.Unit);
            if (ps.Name.equals("height"))
                lenYStr = ps.Details;
            if (ps.Name.equals("width"))
                lenXStr = ps.Details;
            if (ps.Name.equals("length"))
                lenZStr = ps.Details;
        }
        if (lenXStr == null || lenYStr == null || lenZStr == null)
            return;
        try {
            stolpeLenX = Integer.parseInt(lenXStr);
            stolpeLenY = Integer.parseInt(lenYStr);
            stolpeLenZ = Integer.parseInt(lenZStr);
        } catch (NumberFormatException e){
            System.err.println("SET STOLPE EROROREOROEOROEOR");
            return;
        }
    }
    public void SetSpaer(ConnectionPool cp, Product s) throws DatabaseException{
        spaerProd = s;
        List<ProductSpecification> specs = s.GetSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs){
            if (ps.Name.equals("width"))
                lenYStr = ps.Details;
            if (ps.Name.equals("length"))
                lenXStr = ps.Details;
            if (ps.Name.equals("height"))
                lenZStr = ps.Details;
        }
        if (lenXStr == null || lenYStr == null || lenZStr == null)
            return;
        try {
            spaerLenX = Integer.parseInt(lenXStr);
            spaerLenY = Integer.parseInt(lenYStr);
            spaerLenZ = Integer.parseInt(lenZStr);
        } catch (NumberFormatException e){
            return;
        }
    }
    public void SetRem(ConnectionPool cp, Product r) throws DatabaseException{
        remProd = r;
        List<ProductSpecification> specs = r.GetSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs){
            if (ps.Name.equals("length"))
                lenYStr = ps.Details;
            if (ps.Name.equals("width"))
                lenXStr = ps.Details;
            if (ps.Name.equals("height"))
                lenZStr = ps.Details;
        }
        if (lenXStr == null || lenYStr == null || lenZStr == null)
            return;
        try {
            remLenX = Integer.parseInt(lenXStr);
            remLenY = Integer.parseInt(lenYStr);
            remLenZ = Integer.parseInt(lenZStr);
        } catch (NumberFormatException e){
            return;
        }
    }
    public void SetStern(ConnectionPool cp, Product s) throws DatabaseException{
        sternProd = s;
        List<ProductSpecification> specs = s.GetSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs){
            if (ps.Name.equals("length"))
                lenYStr = ps.Details;
            if (ps.Name.equals("width"))
                lenXStr = ps.Details;
            if (ps.Name.equals("height"))
                lenZStr = ps.Details;
        }
        if (lenXStr == null || lenYStr == null || lenZStr == null)
            return;
        try {
            sternWidth = Integer.parseInt(lenXStr);
            sternLength = Integer.parseInt(lenYStr);
            sternHeight = Integer.parseInt(lenZStr);
        } catch (NumberFormatException e){
            return;
        }
    }
    public void SetTagplade(ConnectionPool cp, Product t) throws DatabaseException{
        tagpladeProd = t;
        List<ProductSpecification> specs = t.GetSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs){
            if (ps.Name.equals("length"))
                lenYStr = ps.Details;
            if (ps.Name.equals("width"))
                lenXStr = ps.Details;
            if (ps.Name.equals("height"))
                lenZStr = ps.Details;
        }
        if (lenXStr == null || lenYStr == null || lenZStr == null)
            return;
        try {
            tagpladeLenX = Integer.parseInt(lenXStr);
            tagpladeLenY = Integer.parseInt(lenYStr);
            tagpladeLenZ = Integer.parseInt(lenZStr);
        } catch (NumberFormatException e){
            return;
        }
    }

    public int GetNParkingSpots(){
        if (rectangles == null)
            return 0;
        int n = 0;
        for (rectangle r : rectangles)
            if (r.IsParkingArea())
                ++n;
        return n;
    }

    // TODO: sanity checking
    public boolean Make(int lenX, int lenY, int lenZ, boolean enableShed, int shedLenX, int shedLenY) {
        LenX = lenX;
        LenY = lenY;
        LenZ = lenZ;

        shed = (enableShed && shedLenX > 0 && shedLenY > 0)
                ? new rectangle(new point(stolpeLenX, stolpeLenY), shedLenX, shedLenY)
                : null;

        if (shed != null)
            if (shedLenX > LenX - 2 * stolpeLenX || shedLenY > LenY - 2 * stolpeLenY) {
                made = false;
                return made;
            }
        if (LenX < STOLPE_DISTANCE_MAX + 2 * stolpeLenX ||
                LenY < STOLPE_DISTANCE_MAX + 2 * stolpeLenY) {
            made = false;
            return made;
        }
        /* FILL LISTS WITH ABSTRACT CARPORT COMPONENTS */
        int x, y, z, i, j;
        rectangles = new ArrayList<>();
        stolper = new ArrayList<>();
        remme = new ArrayList<>();
        spaer = new ArrayList<>();
        stern = new ArrayList<>();
        tagplader = new ArrayList<>();

        /* ÅBNE OMRÅDER / MULIGE PARKERINGSOMRÅDER / REMME */
        for (x = stolpeLenX; x < LenX; x += STOLPE_DISTANCE_MAX + stolpeLenX) {
            int rectangleLenX = (x + stolpeLenX + STOLPE_DISTANCE_MAX >= LenX) ? LenX - x - stolpeLenX
                    : STOLPE_DISTANCE_MAX;
            rectangles.add(new rectangle(new point(x, stolpeLenY), rectangleLenX, LenY));
            remme.add(new rectangle(x - stolpeLenX, 0, remLenX, LenY + UDHÆNG_LENGTH_Y));
        }
        remme.add(new rectangle(LenX - stolpeLenX, 0, remLenX, LenY + UDHÆNG_LENGTH_Y));
        adjustRectangles();
        /* STOLPER */
        for (rectangle r : rectangles) {
            List<rectangle> s = getStolper(r);
            for (rectangle rs : s)
                rectangleListAddOnlyUnique(rs, stolper);
        }
        /* SKUR */
        if (shed != null) { // TODO: SHED NEEDS STOLPER INSIDE TO MATCH
            List<rectangle> s = getStolper(shed);
            for (rectangle rs : s)
                rectangleListAddOnlyUnique(rs, stolper);
        }
        /* SPÆR */
        for (y = 0; y <= LenY + UDHÆNG_LENGTH_Y - spaerLenY;) {
            rectangle r = new rectangle(0 - UDHÆNG_LENGTH_X, y, LenX + 2 * UDHÆNG_LENGTH_X, spaerLenY);
            spaer.add(r);
            int posY = y + SPÆR_DISTANCE_MAX + spaerLenY;
            if (posY > LenY + UDHÆNG_LENGTH_Y - spaerLenY &&
                    y < LenY + UDHÆNG_LENGTH_Y - spaerLenY)
                y = LenY + UDHÆNG_LENGTH_Y - spaerLenY;
            else
                y = posY;
        }
        /* STERN */
        for (z = 0; shed != null && z < LenZ; z += sternHeight) {
            stern.add(new rectangle(shed.Corners[0].x - stolpeLenX, shed.Corners[0].y - stolpeLenY,
                    shedLenX + 2 * stolpeLenX,
                    sternWidth));
            stern.add(new rectangle(shed.Corners[0].x - stolpeLenX, shed.Corners[0].y - stolpeLenY,
                    sternWidth,
                    shedLenY + 2 * stolpeLenY));
            stern.add(new rectangle(shed.Corners[1].x + stolpeLenX / 2, shed.Corners[1].y - stolpeLenY,
                    sternWidth,
                    shedLenY + 2 * stolpeLenY));
            stern.add(new rectangle(shed.Corners[2].x - stolpeLenX, shed.Corners[2].y + stolpeLenY / 2,
                    shedLenX + 2 * stolpeLenX,
                    sternWidth));
        }
        /* TAGPLADER */
        for (x = 0 - UDHÆNG_LENGTH_X; x < LenX + 2 * UDHÆNG_LENGTH_X; x += tagpladeLenX){
            for (y = 0; y < LenY + UDHÆNG_LENGTH_Y; y += tagpladeLenY){
                tagplader.add(new rectangle(x, y, tagpladeLenX, tagpladeLenY));
            }
        }

        System.err.printf(
                "\t>>customcarport numbers:%n\t\trects : %d, stolper: %d, remme: %d, spær: %d, stern: %d, tagplader: %d, shed: %b%n",
                rectangles.size(), stolper.size(),
                remme.size(), spaer.size(), stern.size(),
                tagplader.size(), shed != null);

        made = true;
        return made;
    }

    private void rectangleListAddOnlyUnique(rectangle r, List<rectangle> l) {
        if (l.contains(r))
            return;
        l.add(r);
    }

    /* SVG */
    public String svgDraw() {
        if (!made)
            return "svgDraw: nothing to draw";
        int defaultStrokeWidth = 4;
        String defaultStrokeColor = "black";
        int defaultShedStrokeWidth = 8;
        String defaultShedStrokeColor = "red";

        List<String> markings = new ArrayList<>(); // MARKS PARKING/SHED AREAS

        String svg = "<svg></svg>";

        if (shed != null) {
            String shedSvg = (shed == null) ? "" : svgObjectAddAttributes(shed.toSvg(svg), "class=\"shed\"");
            svg = svgAddObject(svg, shedSvg);
            markings.add(svgText("marking", shed.LenX / 2, shed.LenY / 2, "Skur"));
        }

        String[] rectangleSvgs = new String[rectangles.size()];
        String[] stolpeSvgs = new String[stolper.size()];
        String[] spaerSvgs = new String[spaer.size()];
        String[] remmeSvgs = new String[remme.size()];
        String[] sternSvgs = new String[stern.size()];
        String[] tagpladeSvgs = new String[tagplader.size()];
        for (int i = 0; i < rectangleSvgs.length; ++i) {
            rectangle r = rectangles.get(i);
            String bSvg = r.toSvg("");
            bSvg = svgObjectAddAttributes(bSvg, "class=\"rectangle\"");
            if (r.IsParkingArea()) {
                markings.add(svgText("marking", r.Corners[0].x + r.LenX / 2, r.Corners[0].y + r.LenY / 2, "P"));
            }
            rectangleSvgs[i] = bSvg;
        }
        for (int i = 0; i < stolpeSvgs.length; ++i) {
            String bSvg = stolper.get(i).toSvg("");
            bSvg = svgObjectAddAttributes(bSvg, "class=\"stolpe\"");
            stolpeSvgs[i] = bSvg;
        }
        for (int i = 0; i < spaerSvgs.length; ++i) {
            String bSvg = spaer.get(i).toSvg("");
            bSvg = svgObjectAddAttributes(bSvg, "class=\"spaer\"");
            spaerSvgs[i] = bSvg;
        }
        for (int i = 0; i < remmeSvgs.length; ++i) {
            String bSvg = remme.get(i).toSvg("");
            bSvg = svgObjectAddAttributes(bSvg, "class=\"rem\"");
            remmeSvgs[i] = bSvg;
        }
        for (int i = 0; i < sternSvgs.length; ++i) {
            String bSvg = stern.get(i).toSvg("");
            bSvg = svgObjectAddAttributes(bSvg, "class=\"stern\"");
            sternSvgs[i] = bSvg;
        }
        for (int i = 0; i < tagpladeSvgs.length; ++i) {
            String bSvg = tagplader.get(i).toSvg("");
            bSvg = svgObjectAddAttributes(bSvg, "class=\"tagplade\"");
            tagpladeSvgs[i] = bSvg;
        }
        /* MARKINGS */
        String[] markingsArr = new String[markings.size()];
        for (int i = 0; i < markingsArr.length; ++i)
            markingsArr[i] = markings.get(i);

        svg = svgAddObject(svg, rectangleSvgs);
        svg = svgAddObject(svg, spaerSvgs);
        svg = svgAddObject(svg, remmeSvgs);
        svg = svgAddObject(svg, sternSvgs);
        svg = svgAddObject(svg, stolpeSvgs);
        svg = svgAddObject(svg, tagpladeSvgs);
        svg = svgAddObject(svg, markingsArr);

        svg = svgSetViewBox(svg, 0 - UDHÆNG_LENGTH_X, 0, LenX + 2 * UDHÆNG_LENGTH_X, LenY + 2 * UDHÆNG_LENGTH_Y);

        svg = svgAddLenX(svg, 0 - UDHÆNG_LENGTH_X, LenX + 2 * UDHÆNG_LENGTH_X, true);
        svg = svgAddLenX(svg, 0, LenX, true);
        svg = svgAddLenY(svg, 0, LenY + UDHÆNG_LENGTH_Y, true);

        for (int x = stolpeLenX; x < LenX; x += STOLPE_DISTANCE_MAX + stolpeLenX)
            svg = (x + STOLPE_DISTANCE_MAX > LenX) ? svgAddLenX(svg, x, LenX - x - stolpeLenX, false)
                    : svgAddLenX(svg, x, STOLPE_DISTANCE_MAX, false);
        for (int y = spaerLenY; y < LenY + UDHÆNG_LENGTH_Y; y += SPÆR_DISTANCE_MAX + spaerLenY)
            svg = (y + SPÆR_DISTANCE_MAX > LenY + UDHÆNG_LENGTH_Y)
                    ? svgAddLenY(svg, y, LenY + UDHÆNG_LENGTH_Y - y - spaerLenY, false)
                    : svgAddLenY(svg, y, SPÆR_DISTANCE_MAX, false);

        svg = svgSetClassStyle(svg, "rectangle", "fill:lightgrey", "stroke:black", "stroke-width:6", "opacity:0.3");
        svg = svgSetClassStyle(svg, "stolpe", "fill:dimgray", "stroke:black", "stroke-width:0");
        svg = svgSetClassStyle(svg, "spaer", "fill:none", "stroke:black", "stroke-width:8");
        svg = svgSetClassStyle(svg, "rem", "fill:none", "stroke:black", "stroke-width:8");
        svg = svgSetClassStyle(svg, "stern", "fill:black", "stroke:black", "stroke-width:0", "opacity:0.5");
        svg = svgSetClassStyle(svg, "shed", "fill:gray", "stroke:black", "stroke-width:12", "opacity:0.3");
        svg = svgSetClassStyle(svg, "tagplade", "fill:none", "stroke:black", "stroke-width:10", "opacity:0.3");
        svg = svgSetClassStyle(svg, "textDescr", "font: italic 80px sans-serif");
        svg = svgSetClassStyle(svg, "marking", "font: italic 280px sans-serif");
        svg = svgSetClassStyle(svg, "polyArrow", "fill:none", "stroke:black", "stroke-width:6");

        rectangle viewBox = svgGetDims(svg);
        svg = svgSetViewBox(svg, viewBox.Corners[0].x - 200, viewBox.Corners[0].y, viewBox.LenX + 200,
                viewBox.LenY + 200);

        return svg;
    }

    private String svgDrawPolygon(String className, point... points) {
        String poly = "";
        for (point p : points)
            poly += String.format(" %d, %d ", p.x, p.y);
        return String.format("<polygon class=\"%s\" points=\"%s\"/>", className, poly);
    }

    private String svgText(String className, int x, int y, String text) {
        return String.format("<text class=\"%s\" x=\"%d\" y=\"%d\">%s</text>",
                className, x, y, text);
    }

    private String svgSetClassStyle(String svg, String className, String... args) {
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

    private String svgAddLenX(String svg, int startX, int lenX, boolean moveRuler) {
        int h = 20;
        int arrowLen = 40;

        rectangle viewBox = svgGetDims(svg);

        rectangle ruler = new rectangle(startX + arrowLen, viewBox.Corners[2].y, lenX - 2 * arrowLen, h);
        String text = String.format("<text class=\"textDescr\" x=\"%d\" y=\"%d\">%3.3f m</text>",
                ruler.Corners[0].x + ruler.LenX / 2,
                ruler.Corners[0].y - h, ((float) lenX) / 1000.0f);
        String lArrow = svgDrawPolygon("polyArrow", ruler.Corners[0],
                ruler.Corners[2],
                new point(ruler.Corners[0].x - arrowLen,
                        ruler.Corners[0].y + ruler.LenY / 2));
        String rArrow = svgDrawPolygon("polyArrow", ruler.Corners[1],
                ruler.Corners[3],
                new point(ruler.Corners[1].x + arrowLen,
                        ruler.Corners[1].y + ruler.LenY / 2));

        svg = svgAddObject(svg, ruler.toSvg(null), text, lArrow, rArrow);
        if (moveRuler)
            svg = svgSetViewBox(svg, viewBox.Corners[0].x, viewBox.Corners[0].y, viewBox.LenX, viewBox.LenY + 10 * h);

        return svg;
    }

    private String svgAddLenY(String svg, int startY, int lenY, boolean moveRuler) {
        int h = 20;
        int arrowLen = 40;

        rectangle viewBox = svgGetDims(svg);

        rectangle ruler = new rectangle(viewBox.Corners[0].x - 10 * h, startY + arrowLen, h, lenY - 2 * arrowLen);
        String text = String.format("<text class=\"textDescr\" x=\"%d\" y=\"%d\">%3.3f m</text>",
                ruler.Corners[0].x + ruler.LenX * 2,
                ruler.Corners[0].y + ruler.LenY / 2, ((float) lenY) / 1000.0f);
        String tArrow = svgDrawPolygon("polyArrow", ruler.Corners[0], ruler.Corners[1],
                new point(ruler.Corners[0].x + ruler.LenX / 2,
                        ruler.Corners[0].y - arrowLen));
        String bArrow = svgDrawPolygon("polyArrow", ruler.Corners[2], ruler.Corners[3],
                new point(ruler.Corners[2].x + ruler.LenX / 2,
                        ruler.Corners[2].y + arrowLen));
        svg = svgAddObject(svg, ruler.toSvg(null), text, tArrow, bArrow);
        if (moveRuler)
            svg = svgSetViewBox(svg, viewBox.Corners[0].x - 20 * h, viewBox.Corners[0].y, viewBox.LenX + 20 * h,
                    viewBox.LenY);

        return svg;
    }

    private rectangle svgGetDims(String svg) {
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

    private String svgSetViewBox(String svg, int x, int y, int lenX, int lenY) {
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
                if (b.LenY - shed.LenY - stolpeLenY > 0) // TODO FIX THIS, IT ONLY CROPS FIRST RECTANGLE ATM
                    rectangles.add(new rectangle(b.Corners[0].x, shed.Corners[2].y + stolpeLenY,
                            b.LenX, b.LenY - shed.LenY - stolpeLenY));
                if (b.Corners[1].x - shed.Corners[1].x - stolpeLenX > 0) // TODO: might need to fine tune ?
                    rectangles.add(new rectangle(shed.Corners[1].x + stolpeLenX, stolpeLenY,
                            b.Corners[1].x - shed.Corners[1].x - stolpeLenX, shed.LenY));
                rectangles.remove(b);
                --i;
            }
        }
    }

    private void addSpaer() {
        spaer = new ArrayList<>();

    }

    private void addRemme() {
        remme = new ArrayList<>();

    }

    private void addStern() {
        stern = new ArrayList<>();

    }

    private List<rectangle> getStolper(rectangle r) {
        List<rectangle> stolper = new ArrayList<>();

        int x, y;

        // add to corners
        stolper.add(new rectangle(r.Corners[0].x - stolpeLenX,
                r.Corners[0].y - stolpeLenY,
                stolpeLenX,
                stolpeLenY));
        stolper.add(new rectangle(r.Corners[1].x,
                r.Corners[1].y - stolpeLenY,
                stolpeLenX,
                stolpeLenY));
        stolper.add(new rectangle(r.Corners[2].x - stolpeLenX,
                r.Corners[2].y,
                stolpeLenX,
                stolpeLenY));
        stolper.add(new rectangle(r.Corners[3].x,
                r.Corners[3].y,
                stolpeLenX,
                stolpeLenY));
        // --> sides
        // north
        for (x = r.Corners[0].x + STOLPE_DISTANCE_MAX; x < r.Corners[1].x - stolpeLenX; x += STOLPE_DISTANCE_MAX
                + stolpeLenX)
            stolper.add(new rectangle(x,
                    r.Corners[0].y - stolpeLenY,
                    stolpeLenX,
                    stolpeLenY));
        // east
        for (y = r.Corners[1].y + STOLPE_DISTANCE_MAX; y < r.Corners[3].y - stolpeLenY; y += STOLPE_DISTANCE_MAX
                + stolpeLenY)
            stolper.add(new rectangle(r.Corners[1].x,
                    y,
                    stolpeLenX,
                    stolpeLenY));
        // south
        for (x = r.Corners[2].x + STOLPE_DISTANCE_MAX; x < r.Corners[3].x - stolpeLenX; x += STOLPE_DISTANCE_MAX
                + stolpeLenX)
            stolper.add(new rectangle(x,
                    r.Corners[2].y,
                    stolpeLenX,
                    stolpeLenY));
        // west
        for (y = r.Corners[0].y + STOLPE_DISTANCE_MAX; y < r.Corners[2].y - stolpeLenY; y += STOLPE_DISTANCE_MAX
                + stolpeLenY)
            stolper.add(new rectangle(r.Corners[0].x - stolpeLenX,
                    y,
                    stolpeLenX,
                    stolpeLenY));

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

        @Override
        public boolean equals(Object o) {
            rectangle r = (rectangle) o;
            return this.Corners[0].x == r.Corners[0].x &&
                    this.Corners[0].y == r.Corners[0].y &&
                    this.LenX == r.LenX &&
                    this.LenY == r.LenY;

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
        public int z;

        public point(int x, int y) {
            this.x = x;
            this.y = y;
            this.z = 0;
        }

        public point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "point [x=" + x + ", y=" + y + "]";
        }

        public point AdjustNew(int x, int y) {
            return new point(this.x + x, this.y + y);
        }
    }

    @Override
    public String toString() {
        return "CustomCarport [SPÆR_DISTANCE_MAX=" + SPÆR_DISTANCE_MAX + ", STOLPE_DISTANCE_MAX=" + STOLPE_DISTANCE_MAX
                + ", PARKING_WIDTH_MIN=" + PARKING_WIDTH_MIN + ", PARKING_LENGTH_MIN=" + PARKING_LENGTH_MIN
                + ", UDHÆNG_LENGTH_X=" + UDHÆNG_LENGTH_X + ", UDHÆNG_LENGTH_Y=" + UDHÆNG_LENGTH_Y + ", LenX=" + LenX
                + ", LenY=" + LenY + ", LenZ=" + LenZ
                + "stolpeLenX=" + stolpeLenX + ", stolpeLenY=" + stolpeLenY + ", stolpeLenZ=" + stolpeLenZ
                + ", remLenX=" + remLenX + ", remLenY=" + remLenY + ", remLenZ=" + remLenZ + ", spaerLenX=" + spaerLenX
                + ", spaerLenY=" + spaerLenY + ", spaerLenZ=" + spaerLenZ + ", sternWidth=" + sternWidth
                + ", sternHeight=" + sternHeight + ", sternLength=" + sternLength + ", tagpladeLenX=" + tagpladeLenX
                + ", tagpladeLenY=" + tagpladeLenY + ", tagpladeLenZ=" + tagpladeLenZ + ", stolpeProd=" + stolpeProd.Name
                + ", remProd=" + remProd.Name + ", spaerProd=" + spaerProd.Name + ", sternProd=" + sternProd.Name + ", tagpladeProd="
                + tagpladeProd.Name + ", made=" + made + "]";
    }


}
