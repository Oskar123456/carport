package carport.controllers;

import carport.entities.ProductImage;
import carport.entities.Product;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CarportController {
    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /*
         * get
         */
        app.get("/", ctx -> renderIndex(ctx, cp));
        app.get("/index", ctx -> renderIndex(ctx, cp));
        app.get("/index.html", ctx -> renderIndex(ctx, cp));

        app.get("/soegning", ctx -> renderSearch(ctx, cp));
        app.get("/soegning.html", ctx -> renderSearch(ctx, cp));

        /*
         * get custom
         */
        app.get("/produkt*", ctx -> renderProduct(ctx, cp));
        app.get("/billeder*", ctx -> sendImage(ctx, cp));
        /*
         * post
         */
    }

    private static void sendImage(Context ctx, ConnectionPool cp) {
        String qParamImgId = ctx.queryParam("id");
        int imgId = 0;
        if (qParamImgId != null) {
            try {
                imgId = Integer.parseInt(qParamImgId);
                ProductImage img = CarportMapper.SelectProductImageById(cp, imgId);
                ctx.contentType("image/" + img.Format());
                ctx.result(img.Data());
            } catch (NumberFormatException | DatabaseException e) {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void renderProduct(Context ctx, ConnectionPool cp) {
        System.out.println("renderProduct::qparams:\t" + ctx.queryParam("name") + " / " + ctx.queryParam("id"));
        ctx.render("soegning.html");
    }

    private static void renderSearch(Context ctx, ConnectionPool cp) {
        String searchString = ctx.queryParam("searchString");
        searchString = (searchString == null) ? "" : searchString;
        String pageString = ctx.queryParam("page");
        int pageNumber = -1;
        if (pageString != null) {
            try {
                pageNumber = Integer.parseInt(pageString);
            } catch (NumberFormatException ignored) {}
        }

        ctx.attribute("searchString", searchString);
        try {
            String[] searchStringSplit = searchString.split(" ");
            List<Product> productList = CarportMapper.
                SelectProductsStringMatch(cp,
                                          pageNumber,
                                          searchStringSplit,
                                          null,
                                          null,
                                          null);
            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.render("soegning.html");
    }

    private static void renderIndex(Context ctx, ConnectionPool cp) {
        ctx.render("index.html");
    }
}
