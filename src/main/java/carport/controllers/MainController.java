package carport.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import carport.entities.Order;
import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.entities.User;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.CatAndSpecMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.OrderMapper;
import carport.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class MainController {

    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /*
         * Set up
         */
        CarportMapper.Init();
        AdminFunctionController.addRoutes(app, cp);
        SearchController.addRoutes(app, cp);
        CustomCarportController.addRoutes(app, cp);
        UserController.addRoutes(app, cp);
        /*
         * get
         */
        app.get("/", ctx -> renderIndex(ctx, cp));
        app.get("/index", ctx -> renderIndex(ctx, cp));
        app.get("/index.html", ctx -> renderIndex(ctx, cp));
        app.get("/kurv", ctx -> renderBasket(ctx, cp));

        /*
         * get custom
         */
        app.get("/produkt", ctx -> renderProduct(ctx, cp));
        app.get("/billeder", ctx -> sendImage(ctx, cp));
        app.post("/putinbasket", ctx -> putInBasket(ctx, cp));
        app.post("/removefrombasket", ctx -> removeFromBasket(ctx, cp));
        app.post("/orderbasket", ctx -> orderBasket(ctx, cp));
        /*
         * post
         */
    }

    private static void orderBasket(Context ctx, ConnectionPool cp) {
        User user = ctx.sessionAttribute("currentUser");
        List<Product> basket = ctx.sessionAttribute("basket");
        if (user == null || basket == null || basket.size() < 1){
            ctx.redirect("/");
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
            ctx.redirect("/login");
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
            ctx.redirect("/");
            return;
        }
        renderBasket(ctx, cp);
    }

    private static void renderBasket(Context ctx, ConnectionPool cp) {
        User user = ctx.sessionAttribute("currentUser");
        List<Product> basket = ctx.sessionAttribute("basket");
        if (user == null) {
            ctx.redirect("/login");
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

    private static void sendImage(Context ctx, ConnectionPool cp) {
        String qParamImgId = ctx.queryParam("id");
        int imgId = 0;
        if (qParamImgId != null) {
            try {
                imgId = Integer.parseInt(qParamImgId);
                ProductImage img = CarportMapper.SelectProductImageById(cp, imgId);
                if (img != null) {
                    ctx.contentType("image/" + img.Format());
                    ctx.result(img.Data());
                    return;
                }
            } catch (NumberFormatException | DatabaseException ignored) {
            }
        }
        ctx.redirect("/notfound");
    }

    private static void renderProduct(Context ctx, ConnectionPool cp) {
        String idStr = ctx.queryParam("id");
        try {
            int id = Integer.parseInt(idStr);
            Product product = ProductMapper.SelectProductsById(cp, id).get(0);
            List<ProductSpecification> fullSpecs = product.GetFullSpecs(cp);
            List<ProductCategory> cats = product.GetCategories(cp);
            List<Product> compList = new ArrayList<>();
            if (product.CompIds != null && product.CompIds.length > 0) {
                for (int i = 0; i < product.CompIds.length; ++i) {
                    Product comp = ProductMapper.SelectProductsById(cp, product.CompIds[i].intValue()).get(0);
                    compList.add(comp);
                }
            }
            Product.LoadFullSpecs(cp, compList);
            ctx.attribute("product", product);
            ctx.attribute("fullspecs", fullSpecs);
            ctx.attribute("cats", cats);
            if (ctx.sessionAttribute("admin") != null) {
                ctx.attribute("complist", compList);
                ctx.attribute("baseprice", product.GetSumOfComponentPrices(cp));
                ctx.attribute("iscarport", product.IsType("carport"));
            }
            ctx.render("products/viewproduct.html");
        } catch (DatabaseException | NumberFormatException e) {
            ctx.result(e.getMessage());
            return;
        }
        ctx.render("products/viewproduct.html");
    }

    private static void renderIndex(Context ctx, ConnectionPool cp) {
        ctx.render("index.html");
    }
}
