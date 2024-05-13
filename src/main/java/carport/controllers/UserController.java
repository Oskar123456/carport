package carport.controllers;

import carport.entities.*;
import carport.exceptions.DatabaseException;
import carport.persistence.*;
import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool cp) {
        app.post("/login", ctx -> login(ctx, cp));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createuser", ctx -> ctx.render("brugeroprettelse.html"));
        app.post("/createuser", ctx -> createUser(ctx, cp));
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
        ctx.redirect("/");
    }

    public static void login(Context ctx, ConnectionPool cp) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(email, password, cp);
            ctx.sessionAttribute("currentUser", user);

            if (user.getRole().equals("admin")) {
                ctx.render("adminProfile.html");  // Render til admin profil siden hvis useren er en admin
            } else {
                ctx.render("svg.html");  // ellers Render til en standard user side
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", "Login fejlede, tjek din email og adgangskode og prøv igen:)");
            ctx.render("index.html");
        }
    }
}