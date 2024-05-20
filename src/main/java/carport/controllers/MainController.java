package carport.controllers;

import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class MainController
{

    public static void addRoutes(Javalin app, ConnectionPool cp)
    {
        /*
         * Set up
         */
        CarportMapper.Init();
        AdminFunctionController.addRoutes(app, cp);
        SearchController.addRoutes(app, cp);
        BasketController.addRoutes(app, cp);
        CustomCarportController.addRoutes(app, cp);
        UserController.addRoutes(app, cp);
        /*
         * get
         */
        app.get("/", ctx -> renderIndex(ctx, cp));
        app.get("/index", ctx -> renderIndex(ctx, cp));
        app.get("/index.html", ctx -> renderIndex(ctx, cp));
        app.get("/produkt", ctx -> renderProduct(ctx, cp));
        app.get("/billeder", ctx -> sendImage(ctx, cp));
    }


    private static void sendImage(Context ctx, ConnectionPool cp)
    {
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
        ctx.result("not found");
    }

    private static void renderProduct(Context ctx, ConnectionPool cp)
    {
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

    private static void renderIndex(Context ctx, ConnectionPool cp)
    {
        ctx.render("index.html");
    }
}
