package carport.controllers;

import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class CupcakeController {
    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /*
         * get
         */
        app.get("/", ctx -> renderIndex(ctx, cp));
        app.get("/index", ctx -> renderIndex(ctx, cp));
        app.get("/index.html", ctx -> renderIndex(ctx, cp));
        /*
         * post
         */
    }

    private static void renderIndex(Context ctx, ConnectionPool cp){
        ctx.render("index.html");
    }
}
