package carport.controllers;

import carport.entities.CustomCarport;
import carport.entities.Product;
import carport.entities.User;
import carport.exceptions.DatabaseException;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
import carport.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {
    public OrderController() {
    }

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/order", ctx -> ctx.render("showorder.html"));
        app.get("/orderPage", ctx -> ctx.render("orderFlow/byg-carport.html"));
        app.post("/save-dimensions", ctx -> saveDimensions(ctx));
        app.get("/sendrequest", ctx -> sendRequest(ctx, connectionPool));
        app.get("/calculate", ctx -> calculatePrice(ctx));
        app.post("/sendcustomcarportrequest", ctx -> showResult(ctx, connectionPool));
    }

    private static void saveDimensions(Context ctx) {
        try {
            int width = Integer.parseInt(ctx.formParam("width"));
            int length = Integer.parseInt(ctx.formParam("length"));

            ctx.sessionAttribute("width", width);
            ctx.sessionAttribute("length", length);

            ctx.redirect("/calculate");
        } catch (NumberFormatException e) {
            ctx.attribute("error", "Ugyldigt. Vælg venligst både bredde og længde.");
            ctx.render("byg-selv.html");
        }
    }

    private static void sendRequest(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int width = ctx.sessionAttribute("width");
        int length = ctx.sessionAttribute("length");

        try {
            ProductMapper.InsertCarportCustomBase(connectionPool, length, width, 200);

            // TODO: Giv kunde besked om at forespørgslen er sendt

        } catch (DatabaseException e) {
            ctx.attribute("message", "Fejl ved afsendelse af forespørgsel. Prøv igen. " +
                    "Hvis fejlen fortsætter, så kontakt en sælger.");
            ctx.render("svg.html");
        }
    }

    private static void calculatePrice(Context ctx) {
        Integer width = ctx.sessionAttribute("width");
        Integer length = ctx.sessionAttribute("length");

        if (width == null || length == null) {
            ctx.attribute("error", "Mål ikke fundet i sessionen. Venligst indtast målene igen.");
            ctx.render("/order");
        } else {
            int price = calculateTotalPrice(width, length);

            ctx.attribute("price", price);
            ctx.render("price.html");
        }
    }

    private static int calculateTotalPrice(int width, int length) {
        return width * length * 5; // Det bare lige en hurtig logik, skal ændres.
        // Spørg jon i morgen, om vi skal lave den også?
    }

    private static void showResult(Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.render("user/logind.html");
            return;
        }

        String wStr = ctx.formParam("width");
        String lStr = ctx.formParam("length");
        String shedStr = ctx.formParam("shed");
        String swStr = ctx.formParam("shedwidth");
        String slStr = ctx.formParam("shedlength");
        try {
            int w = Integer.parseInt(wStr);
            int l = Integer.parseInt(lStr);
            boolean shed = (shedStr != null && shedStr.equals("on")) ? true : false;
            int sw = Integer.parseInt(swStr);
            int sl = Integer.parseInt(slStr);

            CustomCarport cc = new CustomCarport();

            Product stolpe = ProductMapper.SelectProductsById(cp,15).get(0);
            Product rem = ProductMapper.SelectProductsById(cp,11).get(0);
            Product spaer = ProductMapper.SelectProductsById(cp, 12).get(0);
            Product stern = ProductMapper.SelectProductsById(cp,21).get(0);
            Product tagplade = ProductMapper.SelectProductsById(cp,14).get(0);

            cc.SetStolpe(cp, stolpe);
            cc.SetRem(cp, rem);
            cc.SetSpaer(cp, spaer);
            cc.SetStern(cp, stern);
            cc.SetTagplade(cp, tagplade);

            boolean made = cc.Make(w * 10, l * 10, 2800, shed, sw * 10, sl * 10);

            if (!made){
                ctx.render("byg-carport.html");
                return;
            }

            int success = cc.WriteToDb(cp);
            if (success > 0) {
                ctx.redirect("/produkt?id=" + success);
                return;
            }
        } catch (NumberFormatException | DatabaseException e) {
            ctx.redirect("orderPage");
            return;
        }
    }
}
