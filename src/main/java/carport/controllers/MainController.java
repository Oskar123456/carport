package carport.controllers;

import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import carport.tools.ProductImageFactory;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class MainController {

    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /*
        * Set up
        * */
        CarportMapper.Init();
        AdminFunctionController.addRoutes(app, cp);
        SearchController.addRoutes(app, cp);
        /*
         * get
         */
        app.get("/", ctx -> renderIndex(ctx, cp));
        app.get("/index", ctx -> renderIndex(ctx, cp));
        app.get("/index.html", ctx -> renderIndex(ctx, cp));


        app.get("/bygselv", ctx -> renderBygSelv(ctx, cp));
        app.get("/bygselvcarport", ctx -> renderBygSelv(ctx, cp));

        app.get("/admin", ctx -> renderAdminPage(ctx, cp));
        app.get("/administrator", ctx -> renderAdminPage(ctx, cp));

        /*
         * get custom
         */
        app.get("/produkt", ctx -> renderProduct(ctx, cp));
        app.get("/billeder", ctx -> sendImage(ctx, cp));
        /*
         * post
         */


        /*
         * testing only stuff
         */
        if (System.getenv("test") != null && System.getenv("test").equals("true"))
            app.beforeMatched(ctx -> ctx.sessionAttribute("admin", true));
    }



    private static void renderAdminPage(@NotNull Context ctx, ConnectionPool cp) {
    }

    private static void renderBygSelv(@NotNull Context ctx, ConnectionPool cp) {
        ctx.result("lol");
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
        System.out.println("renderProduct::qparams:\t" + ctx.queryParam("name") + " / " + ctx.queryParam("id"));
        ctx.render("soegning.html");
    }
    private static void renderIndex(Context ctx, ConnectionPool cp) {
        ctx.render("index.html");
    }
}
