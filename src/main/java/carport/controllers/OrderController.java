package carport.controllers;

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
            // TODO: Der skal vel et user objekt eller user_id som argument i InsertCarportCustomBase ellers kan kunden ikke se sin ordre?
            // TODO: Eller er det først når admin/sælger har godkendt forespørgslen at den gemmes på user_id og kunden kan se ordren?

            /*User user = UserMapper.login(email, password, cp);
            ctx.sessionAttribute("currentUser", user);*/

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
        return width * length * 5; //Det bare lige en hurtig logik, skal ændres.
        //Spørg jon i morgen, om vi skal lave den også?
    }
}