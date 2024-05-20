package carport.entities;

import carport.exceptions.DatabaseException;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
import carport.tools.types.Point;
import carport.tools.types.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static carport.tools.SvgHelper.*;

public class CustomCarport
{
    /* ALL MEASUREMENTS IN MM */
    public int SPAER_DISTANCE_MAX = 600;
    public int STOLPE_DISTANCE_MAX = 3000;
    public int PARKING_WIDTH_MIN = 2000;
    public int PARKING_LENGTH_MIN = 4000;
    public int UDHAENG_LENGTH_X = 500;
    public int UDHAENG_LENGTH_Y = 500;

    public int LenX;
    public int LenY;
    public int LenZ;

    private List<Rectangle> rectangles;
    private List<Rectangle> stolper;
    private List<Rectangle> spaer;
    private List<Rectangle> remme;
    private List<Rectangle> stern;
    private List<Rectangle> tagplader;
    private Rectangle shed;

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

    private boolean made;

    public Product GetStolpeProd()
    {
        return stolpeProd;
    }

    public int GetNStolpeProd()
    {
        if (stolper == null)
            return 0;
        return stolper.size() * (int) (Math.ceil((double) LenZ / (double) stolpeLenZ));
    }

    public Product GetRemProd()
    {
        return remProd;
    }

    public int GetNRemProd()
    {
        if (remme == null)
            return 0;
        return remme.size() * (int) ((Math.ceil(((double) LenY + (double) UDHAENG_LENGTH_Y) / (double) remLenY)));
    }

    public Product GetSpaerProd()
    {
        return spaerProd;
    }

    public int GetNSpaerProd()
    {
        if (spaer == null)
            return 0;
        return spaer.size() * (int) ((Math.ceil(((double) LenX + (double) UDHAENG_LENGTH_X * 2.0) / (double) spaerLenX)));
    }

    public Product GetSternProd()
    {
        return sternProd;
    }

    public int GetNSternProd()
    {
        if (stern == null || shed == null)
            return 0;
        int factorH = ((int) (Math.ceil((double) shed.LenX / ((double) sternLength)))) * 2;
        int factorV = ((int) (Math.ceil((double) shed.LenY / ((double) sternLength)))) * 2;
        return (int) (Math.ceil((double) LenZ / (double) sternHeight)) * (factorH + factorV);
    }

    public Product GetTagpladeProd()
    {
        return tagpladeProd;
    }

    public int GetNTagpladeProd()
    {
        if (tagplader == null)
            return 0;
        return tagplader.size();
    }

    public void SetStolpe(ConnectionPool cp, Product s) throws DatabaseException
    {
        stolpeProd = s;
        List<ProductSpecification> specs = s.GetFullSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs) {
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
            stolpeLenX = Integer.parseInt(lenXStr);
            stolpeLenY = Integer.parseInt(lenYStr);
            stolpeLenZ = Integer.parseInt(lenZStr);
        } catch (NumberFormatException e) {
            return;
        }
    }

    public void SetSpaer(ConnectionPool cp, Product s) throws DatabaseException
    {
        spaerProd = s;
        List<ProductSpecification> specs = s.GetFullSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs) {
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
        } catch (NumberFormatException e) {
            return;
        }
    }

    public void SetRem(ConnectionPool cp, Product r) throws DatabaseException
    {
        remProd = r;
        List<ProductSpecification> specs = r.GetFullSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs) {
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
        } catch (NumberFormatException e) {
            return;
        }
    }

    public void SetStern(ConnectionPool cp, Product s) throws DatabaseException
    {
        sternProd = s;
        List<ProductSpecification> specs = s.GetFullSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs) {
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
        } catch (NumberFormatException e) {
            return;
        }
    }

    public void SetTagplade(ConnectionPool cp, Product t) throws DatabaseException
    {
        tagpladeProd = t;
        List<ProductSpecification> specs = t.GetFullSpecs(cp);
        String lenXStr = null;
        String lenYStr = null;
        String lenZStr = null;
        for (ProductSpecification ps : specs) {
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
        } catch (NumberFormatException e) {
            return;
        }
    }

    public int GetNParkingSpots()
    {
        if (rectangles == null)
            return 0;
        int n = 0;
        for (Rectangle r : rectangles)
            if (r.IsParkingArea(PARKING_WIDTH_MIN, PARKING_LENGTH_MIN))
                ++n;
        return n;
    }

    // TODO: sanity checking
    public boolean Make(int lenX, int lenY, int lenZ, boolean enableShed, int shedLenX, int shedLenY)
    {
        LenX = lenX;
        LenY = lenY;
        LenZ = lenZ;

        shed = (enableShed && shedLenX > 0 && shedLenY > 0)
                ? new Rectangle(new Point(stolpeLenX, stolpeLenY), shedLenX, shedLenY)
                : null;

        if (shed != null)
            if (shedLenX > LenX - 2 * stolpeLenX || shedLenY > LenY - 2 * stolpeLenY) {
                made = false;
                return false;
            }
        if (LenX < PARKING_WIDTH_MIN + 2 * stolpeLenX ||
                LenY < PARKING_LENGTH_MIN + 2 * stolpeLenY) {
            made = false;
            return false;
        }
        /* FILL LISTS WITH ABSTRACT CARPORT COMPONENTS */
        int x, y, z;
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
            rectangles.add(new Rectangle(new Point(x, stolpeLenY), rectangleLenX, LenY));
            remme.add(new Rectangle(x - stolpeLenX, 0, remLenX, LenY + UDHAENG_LENGTH_Y));
        }
        remme.add(new Rectangle(LenX - stolpeLenX, 0, remLenX, LenY + UDHAENG_LENGTH_Y));
        /* STOLPER */
        for (Rectangle r : rectangles) {
            List<Rectangle> s = getStolper(r);
            for (Rectangle rs : s)
                rectangleListAddOnlyUnique(rs, stolper);
        }
        adjustRectangles();
        /* SKUR */
        if (shed != null) { // TODO: SHED NEEDS STOLPER INSIDE TO MATCH
            List<Rectangle> s = getStolper(shed);
            for (Rectangle rs : s)
                rectangleListAddOnlyUnique(rs, stolper);
        }
        /* SPÆR */
        for (y = 0; y <= LenY + UDHAENG_LENGTH_Y - spaerLenY; ) {
            Rectangle r = new Rectangle(-UDHAENG_LENGTH_X, y, LenX + 2 * UDHAENG_LENGTH_X, spaerLenY);
            spaer.add(r);
            int posY = y + SPAER_DISTANCE_MAX + spaerLenY;
            if (posY > LenY + UDHAENG_LENGTH_Y - spaerLenY &&
                    y < LenY + UDHAENG_LENGTH_Y - spaerLenY)
                y = LenY + UDHAENG_LENGTH_Y - spaerLenY;
            else
                y = posY;
        }
        /* STERN */
        for (z = 0; shed != null && z < LenZ; z += sternHeight) {
            stern.add(new Rectangle(shed.Corners[0].x - stolpeLenX, shed.Corners[0].y - stolpeLenY,
                    shedLenX + 2 * stolpeLenX,
                    sternWidth));
            stern.add(new Rectangle(shed.Corners[0].x - stolpeLenX, shed.Corners[0].y - stolpeLenY,
                    sternWidth,
                    shedLenY + 2 * stolpeLenY));
            stern.add(new Rectangle(shed.Corners[1].x + stolpeLenX / 2, shed.Corners[1].y - stolpeLenY,
                    sternWidth,
                    shedLenY + 2 * stolpeLenY));
            stern.add(new Rectangle(shed.Corners[2].x - stolpeLenX, shed.Corners[2].y + stolpeLenY / 2,
                    shedLenX + 2 * stolpeLenX,
                    sternWidth));
        }
        /* TAGPLADER */
        for (x = -UDHAENG_LENGTH_X; x < LenX + 2 * UDHAENG_LENGTH_X; x += tagpladeLenX) {
            for (y = 0; y < LenY + UDHAENG_LENGTH_Y; y += tagpladeLenY) {
                tagplader.add(new Rectangle(x, y, tagpladeLenX, tagpladeLenY));
            }
        }

        made = true;
        return true;
    }

    private void rectangleListAddOnlyUnique(Rectangle r, List<Rectangle> l)
    {
        if (l.contains(r))
            return;
        l.add(r);
    }

    /* DATABASE INTERACTION */
    public int WriteToDb(ConnectionPool cp)
    {
        int retval = -1;
        if (!made)
            return retval;

        try {
            String svg = svgDraw();
            ProductImage svgImg = new ProductImage(-1, "customcarportSvg", "cphbusiness", svg.getBytes(), "svg+xml", false);
            int svgImgId = ProductMapper.InsertProductImage(cp, svgImg, false, 0);
            if (svgImgId <= 0)
                return retval;
            Long[] catIds = {1L};
            Long[] specIds = {1L, 2L, 3L, 8L, 9L};
            Long[] imgIds = {(long) svgImgId};
            Product thisAsProduct = new Product("customcarport", String.format("custom made carport w %d l %d h %d", LenX, LenY, LenZ),
                    null, null, catIds, specIds);
            thisAsProduct.SpecDetails[0] = String.valueOf(LenX);
            thisAsProduct.SpecDetails[1] = String.valueOf(LenY);
            thisAsProduct.SpecDetails[2] = String.valueOf(LenZ); // TODO: function for this stuff in product ?
            thisAsProduct.SpecDetails[3] = String.valueOf(GetNParkingSpots());
            thisAsProduct.SpecDetails[4] = String.valueOf(shed != null);

            thisAsProduct.AddComps(stolpeProd.Id, GetNStolpeProd());
            thisAsProduct.AddComps(remProd.Id, GetNRemProd());
            thisAsProduct.AddComps(spaerProd.Id, GetNSpaerProd());
            thisAsProduct.AddComps(sternProd.Id, GetNSternProd());
            thisAsProduct.AddComps(tagpladeProd.Id, GetNTagpladeProd());

            thisAsProduct.ImageIds = imgIds;
            thisAsProduct.ImageDownscaledIds = imgIds;

            thisAsProduct.Price = thisAsProduct.GetSumOfComponentPrices(cp);

            retval = ProductMapper.InsertProduct(cp, true, thisAsProduct);
        } catch (DatabaseException e) {
            return retval;
        }


        return retval;
    }

    /* SVG */
    public String svgDraw()
    {
        if (!made)
            return "svgDraw: nothing to draw";

        List<String> markings = new ArrayList<>(); // MARKS PARKING/SHED AREAS

        String svg = "<svg  ></svg>";

        if (shed != null) {
            String shedSvg = (shed == null) ? "" : svgObjectAddAttributes(shed.toSvg(""), "class=\"shed\"");
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
            Rectangle r = rectangles.get(i);
            String bSvg = r.toSvg("");
            bSvg = svgObjectAddAttributes(bSvg, "class=\"rectangle\"");
            if (r.IsParkingArea(PARKING_WIDTH_MIN, PARKING_LENGTH_MIN)) {
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

        svg = svgSetViewBox(svg, -UDHAENG_LENGTH_X, 0, LenX + 2 * UDHAENG_LENGTH_X, LenY + 2 * UDHAENG_LENGTH_Y);

        svg = svgAddLenX(svg, -UDHAENG_LENGTH_X, LenX + 2 * UDHAENG_LENGTH_X, true);
        svg = svgAddLenX(svg, 0, LenX, true);
        svg = svgAddLenY(svg, 0, LenY + UDHAENG_LENGTH_Y, true);

        for (int x = stolpeLenX; x < LenX; x += STOLPE_DISTANCE_MAX + stolpeLenX)
            svg = (x + STOLPE_DISTANCE_MAX > LenX) ? svgAddLenX(svg, x, LenX - x - stolpeLenX, false)
                    : svgAddLenX(svg, x, STOLPE_DISTANCE_MAX, false);
        for (int y = spaerLenY; y < LenY + UDHAENG_LENGTH_Y; y += SPAER_DISTANCE_MAX + spaerLenY)
            svg = (y + SPAER_DISTANCE_MAX > LenY + UDHAENG_LENGTH_Y)
                    ? svgAddLenY(svg, y, LenY + UDHAENG_LENGTH_Y - y - spaerLenY, false)
                    : svgAddLenY(svg, y, SPAER_DISTANCE_MAX, false);

        svg = svgSetClassStyle(svg, "rectangle", "fill:lightgrey", "stroke:black", "stroke-width:6", "opacity:0.3");
        svg = svgSetClassStyle(svg, "stolpe", "fill:dimgray", "stroke:black", "stroke-width:0");
        svg = svgSetClassStyle(svg, "spaer", "fill:none", "stroke:black", "stroke-width:8");
        svg = svgSetClassStyle(svg, "rem", "fill:none", "stroke:black", "stroke-width:12");
        svg = svgSetClassStyle(svg, "stern", "fill:black", "stroke:black", "stroke-width:0", "opacity:0.5");
        svg = svgSetClassStyle(svg, "shed", "fill:gray", "stroke:black", "stroke-width:12", "opacity:0.3");
        svg = svgSetClassStyle(svg, "tagplade", "fill:none", "stroke:black", "stroke-width:10", "opacity:0.3");
        svg = svgSetClassStyle(svg, "textDescr", "font: italic 80px sans-serif");
        svg = svgSetClassStyle(svg, "marking", "font: italic 280px sans-serif");
        svg = svgSetClassStyle(svg, "polyArrow", "fill:none", "stroke:black", "stroke-width:6");

        Rectangle viewBox = svgGetDims(svg);
        svg = svgSetViewBox(svg, viewBox.Corners[0].x - 200, viewBox.Corners[0].y, viewBox.LenX + 200,
                viewBox.LenY + 200);

        viewBox = svgGetDims(svg);
        svg = svg.substring(0, svg.indexOf("viewBox")) +
                String.format(" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"  width=\"%d\" height=\"%d\" ",
                        viewBox.LenX / 10, viewBox.LenY / 10) +
                svg.substring(svg.indexOf("viewBox"));

        return svg;
    }

    /* INTERNAL HELPERS */

    private void adjustRectangles()
    {
        if (shed == null || rectangles == null || rectangles.size() < 1)
            return;
        for (int i = 0; i < rectangles.size(); ++i) {
            Rectangle b = rectangles.get(i);
            if (shed.Overlaps(b)) {
                if (b.LenY - shed.LenY - stolpeLenY > 0) // TODO FIX THIS, IT ONLY CROPS FIRST RECTANGLE ATM
                    rectangles.add(new Rectangle(b.Corners[0].x, shed.Corners[2].y + stolpeLenY,
                            b.LenX, b.LenY - shed.LenY - stolpeLenY));
                if (b.Corners[1].x - shed.Corners[1].x - stolpeLenX > 0) // TODO: might need to fine tune ?
                    rectangles.add(new Rectangle(shed.Corners[1].x + stolpeLenX, stolpeLenY,
                            b.Corners[1].x - shed.Corners[1].x - stolpeLenX, shed.LenY));
                rectangles.remove(b);
                --i;
            }
        }
    }

    private List<Rectangle> getStolper(Rectangle r)
    {
        List<Rectangle> stolper = new ArrayList<>();

        int x, y;

        // add to corners
        stolper.add(new Rectangle(r.Corners[0].x - stolpeLenX,
                r.Corners[0].y - stolpeLenY,
                stolpeLenX,
                stolpeLenY));
        stolper.add(new Rectangle(r.Corners[1].x,
                r.Corners[1].y - stolpeLenY,
                stolpeLenX,
                stolpeLenY));
        stolper.add(new Rectangle(r.Corners[2].x - stolpeLenX,
                r.Corners[2].y,
                stolpeLenX,
                stolpeLenY));
        stolper.add(new Rectangle(r.Corners[3].x,
                r.Corners[3].y,
                stolpeLenX,
                stolpeLenY));
        // --> sides
        // north
        for (x = r.Corners[0].x + STOLPE_DISTANCE_MAX; x < r.Corners[1].x - stolpeLenX; x += STOLPE_DISTANCE_MAX
                + stolpeLenX)
            stolper.add(new Rectangle(x,
                    r.Corners[0].y - stolpeLenY,
                    stolpeLenX,
                    stolpeLenY));
        // east
        for (y = r.Corners[1].y + STOLPE_DISTANCE_MAX; y < r.Corners[3].y - stolpeLenY; y += STOLPE_DISTANCE_MAX
                + stolpeLenY)
            stolper.add(new Rectangle(r.Corners[1].x,
                    y,
                    stolpeLenX,
                    stolpeLenY));
        // south
        for (x = r.Corners[2].x + STOLPE_DISTANCE_MAX; x < r.Corners[3].x - stolpeLenX; x += STOLPE_DISTANCE_MAX
                + stolpeLenX)
            stolper.add(new Rectangle(x,
                    r.Corners[2].y,
                    stolpeLenX,
                    stolpeLenY));
        // west
        for (y = r.Corners[0].y + STOLPE_DISTANCE_MAX; y < r.Corners[2].y - stolpeLenY; y += STOLPE_DISTANCE_MAX
                + stolpeLenY)
            stolper.add(new Rectangle(r.Corners[0].x - stolpeLenX,
                    y,
                    stolpeLenX,
                    stolpeLenY));

        return stolper;
    }
}
