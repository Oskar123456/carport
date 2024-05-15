package carport.controllers;

import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {
    public OrderController() {
    }

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/order", ctx -> {
            ctx.render("showorder.html");
        });
        app.post("/save-dimensions", ctx -> {
            saveDimensions(ctx);
        });
        app.get("/calculate", ctx -> {
            calculatePrice(ctx);
        });
    }

    private static void saveDimensions(Context ctx) {
        try {
            int width = Integer.parseInt(ctx.formParam("width"));
            int length = Integer.parseInt(ctx.formParam("length"));


            ctx.sessionAttribute("width", width);
            ctx.sessionAttribute("length", length);


            ctx.redirect("/calculate");
        } catch (NumberFormatException e) {
            ctx.attribute("error", "Ugyldigt. Venligst indtast gyldige tal for bredde og længde.");
            ctx.redirect("/order");
        }
    }

    private static void calculatePrice(Context ctx) {
        Integer width = ctx.sessionAttribute("width");
        Integer length = ctx.sessionAttribute("length");

        if (width == null || length == null) {
            ctx.attribute("error", "Mål ikke fundet i sessionen. Venligst indtast målene igen.");
            ctx.redirect("/order");
        } else {
            int price = calculateTotalPrice(width, length);

            ctx.attribute("price", price);
            ctx.render("price.html");
        }
    }

    private static int calculateTotalPrice(int width, int length) {
    }
}