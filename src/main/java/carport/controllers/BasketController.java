package carport.controllers;

import carport.entities.Order;
import carport.entities.Product;
import carport.entities.User;
import carport.exceptions.DatabaseException;
import carport.persistence.ConnectionPool;
import carport.persistence.OrderMapper;
import carport.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BasketController
{
    public static void addRoutes(Javalin app, ConnectionPool cp) {
        app.get("/kurv", ctx -> renderBasket(ctx, cp));
        app.post("/putinbasket", ctx -> putInBasket(ctx, cp));
        app.post("/removefrombasket", ctx -> removeFromBasket(ctx, cp));
        app.post("/orderbasket", ctx -> orderBasket(ctx, cp));
    }
    private static void orderBasket(Context ctx, ConnectionPool cp) {
        User user = ctx.sessionAttribute("currentUser");
        List<Product> basket = ctx.sessionAttribute("basket");
        if (user == null || basket == null || basket.size() < 1){
            ctx.render("index.html");
            return;
        }
        try {
            Order order = new Order(0, 0, user.getId(), 4,
                    "", 1, Product.GetSumOfProductPrices(basket),
                    "", "");
            OrderMapper.InsertOrder(cp, order, 4, basket);
            ctx.sessionAttribute("basket", null);
        }
        catch (DatabaseException e) {}
        ctx.redirect("/profil");
    }

    private static void removeFromBasket(Context ctx, ConnectionPool cp) {
        String productIdStr = ctx.formParam("id");
        List<Product> basket = ctx.sessionAttribute("basket");
        if (basket != null && productIdStr != null) {
            int productId = Integer.parseInt(productIdStr);
            for (int i = 0; i < basket.size(); ++i) {
                Product p = basket.get(i);
                if (p.Id == productId) {
                    basket.remove(p);
                    break;
                }
            }
            ctx.sessionAttribute("basket", basket);
        }
        renderBasket(ctx, cp);
    }

    private static void putInBasket(Context ctx, ConnectionPool cp) {
        User user = ctx.sessionAttribute("currentUser");
        List<Product> basket = ctx.sessionAttribute("basket");
        if (user == null) {
            ctx.render("user/logind.html");
            return;
        }
        if (basket == null)
            basket = new ArrayList<>();
        String productAsString = ctx.formParam("id");
        int productId = (productAsString == null) ? -1 : Integer.parseInt(productAsString);
        try {
            List<Product> products = ProductMapper.SelectProductsById(cp, productId);
            if (products != null && products.size() > 0) {
                Product.LoadFullSpecs(cp, products);
                basket.add(products.get(0));
            }
            ctx.sessionAttribute("basket", basket);
        } catch (DatabaseException e) {
            ctx.render("index.html");
            return;
        }
        renderBasket(ctx, cp);
    }

    private static void renderBasket(Context ctx, ConnectionPool cp) {
        User user = ctx.sessionAttribute("currentUser");
        List<Product> basket = ctx.sessionAttribute("basket");
        if (user == null) {
            ctx.render("user/logind.html");
            return;
        }
        if (basket == null) {
            basket = new ArrayList<>();
        }
        BigDecimal fullprice = new BigDecimal(0);
        for (Product p : basket)
            fullprice = fullprice.add(p.Price);
        ctx.attribute("fullprice", fullprice);
        ctx.sessionAttribute("basket", basket);
        ctx.render("products/viewbasket.html");
    }
}
