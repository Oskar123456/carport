package carport.controllers;

import java.util.List;

import carport.entities.*;
import carport.exceptions.DatabaseException;
import carport.persistence.*;
import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /* GET */
        app.get("/login", ctx -> ctx.render("/user/logind.html"));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createuser", ctx -> ctx.render("/user/brugeroprettelse.html"));
        app.get("/profil", ctx -> renderProfile(ctx, cp));
        /* POST */
        app.post("/login", ctx -> login(ctx, cp));
        app.post("/createuser", ctx -> createUser(ctx, cp));
        app.post("/customerapprovependingorder", ctx -> approveOrder(ctx, cp));
        app.post("/customerremoveorderproduct", ctx -> removeOrderProduct(ctx, cp));
        app.post("/customerremoveorder", ctx -> removeOrder(ctx, cp));
    }

    private static void createUser(Context ctx, ConnectionPool cp) {
        String name = ctx.formParam("name");
        String surname = ctx.formParam("surname");
        String email = ctx.formParam("mail");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        String street = ctx.formParam("street");
        int number = Integer.parseInt(ctx.formParam("number"));
        int floor = Integer.parseInt(ctx.formParam("floor"));
        String info = ctx.formParam("info");
        int zip = Integer.parseInt(ctx.formParam("zip"));

        Address address = new Address(street, number, floor, info, zip);

        if (password1.equals(password2)) {
            try {
                int addressId = AddressMapper.saveAddress(address, cp); // Her har jeg gjort at man kan Save address og get ID
                UserMapper.createuser(name, surname, addressId, email, password1, "user", cp);
                ctx.attribute("message", "Du er nu oprettet: " + name + ". Nu kan du logge ind.");
                ctx.render("index.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Der findes allerede en bruger med denne email. Prøv igen eller log ind.");
                ctx.render("createuser.html");
            }
        } else {
            ctx.attribute("message", "Dine to passwords er ikke ens! Prøv igen.");
            ctx.render("createuser.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.render("index.html");
    }

    public static void login(Context ctx, ConnectionPool cp) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("psw");

        try {
            User user = UserMapper.login(email, password, cp);

            if (user == null){
                ctx.render("user/logind.html");
                return;
            }
            ctx.sessionAttribute("currentUser", user);
            if (user.getRole().equals("admin"))
                ctx.sessionAttribute("admin", true);
        }
            catch (DatabaseException e) {
                ctx.attribute("message", "Login fejlede, tjek din email og adgangskode og prøv igen:)");
                ctx.render("index.html");
        }
        renderProfile(ctx, cp);
    }

    private static void renderProfile(Context ctx, ConnectionPool cp){
        User user = ctx.sessionAttribute("currentUser");
        if (user == null){
            ctx.render("user/logind.html");
            return;
        }
        try {
            List<Order> pendingOrders = OrderMapper.SelectAllOrders(cp, user.getId(),-1, 4);
            List<Order> validatedOrders = OrderMapper.SelectAllOrders(cp, user.getId(),-1,3);
            List<Order> confirmedOrders = OrderMapper.SelectAllOrders(cp, user.getId(),-1,2);
            List<Order> doneOrders = OrderMapper.SelectAllOrders(cp, user.getId(),-1,1);

            Order.LoadList(cp, pendingOrders);
            Order.LoadList(cp, confirmedOrders);
            Order.LoadList(cp, validatedOrders);
            Order.LoadList(cp, doneOrders);

            ctx.attribute("pending", pendingOrders);
            ctx.attribute("confirmed", confirmedOrders);
            ctx.attribute("validated", validatedOrders);
            ctx.attribute("done", doneOrders);
        } catch (NumberFormatException | DatabaseException e) {
            ctx.render("index.html");
            return;
        }
        ctx.render("user/profile.html");
    }

    private static void approveOrder(Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.render("index.html");
            return;
        }
        String id = ctx.formParam("id");
        try {
            OrderMapper.ApproveOrder(cp, Integer.parseInt(id));
        } catch (NumberFormatException | DatabaseException e) {
        }
        renderProfile(ctx, cp);
    }

    private static void removeOrderProduct(Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.render("index.html");
            return;
        }
        String oid = ctx.formParam("oid");
        String pid = ctx.formParam("pid");
        try {
            OrderMapper.DeleteOrderProduct(cp,
                    Integer.parseInt(oid),
                    Integer.parseInt(pid));
        } catch (NumberFormatException | DatabaseException e) {
        }
        renderProfile(ctx, cp);
    }

    private static void removeOrder(Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.render("index.html");
            return;
        }
        String oid = ctx.formParam("oid");
        try {
            OrderMapper.DeleteOrder(cp, Integer.parseInt(oid));
        } catch (NumberFormatException | DatabaseException e) {
        }
        renderProfile(ctx, cp);
    }
}
