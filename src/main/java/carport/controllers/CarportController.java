package carport.controllers;

import carport.entities.Product;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CarportController
{
    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /*
         * get
         */
        app.get("/", ctx -> renderIndex(ctx, cp));
        app.get("/index", ctx -> renderIndex(ctx, cp));
        app.get("/index.html", ctx -> renderIndex(ctx, cp));

        app.get("/soegning", ctx -> renderSearch(ctx, cp));
        app.get("/soegning.html", ctx -> renderSearch(ctx, cp));

        app.get("/produkt*", ctx -> renderProduct(ctx, cp));
        /*
         * post
         */
    }

    private static void renderProduct(Context ctx, ConnectionPool cp)
    {
        System.out.println("renderProduct::qparams:\t" + ctx.queryParam("name") + " / " + ctx.queryParam("id"));
        ctx.render("soegning.html");
    }

    // TODO: maybe think about adding 'before' handlers for session/user stuff

    private static void renderSearch(Context ctx, ConnectionPool cp)
    {
        String searchString = ctx.queryParam("searchString");
        // TODO: limit search to pages and deal with left-right navigation logic in search
        String pageString = ctx.queryParam("page");
        int pageNumber = 0;
        if (pageString != null){
            try {
                pageNumber = Integer.parseInt(pageString);
            }
            catch (NumberFormatException ignored) {
            }
        }

        ctx.attribute("searchString", searchString);
        try {
            List<Product> productList = CarportMapper.ListProductsByName(cp, false, "%");
            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.render("soegning.html");
    }

    private static void renderIndex(Context ctx, ConnectionPool cp){
        ctx.render("index.html");
    }
}
