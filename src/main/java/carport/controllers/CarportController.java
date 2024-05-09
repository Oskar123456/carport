package carport.controllers;

import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.entities.Product;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import carport.tools.ProductImageFactory;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class CarportController {
    public static void addRoutes(Javalin app, ConnectionPool cp) {
        CarportMapper.Init(cp);
        /*
         * get
         */
        app.get("/", ctx -> renderIndex(ctx, cp));
        app.get("/index", ctx -> renderIndex(ctx, cp));
        app.get("/index.html", ctx -> renderIndex(ctx, cp));

        app.get("/soegning", ctx -> renderSearch(ctx, cp));
        app.get("/soegning.html", ctx -> renderSearch(ctx, cp));

        app.get("/bygselv", ctx -> renderBygSelv(ctx, cp));
        app.get("/bygselvcarport", ctx -> renderBygSelv(ctx, cp));

        app.get("/admin", ctx -> renderAdminPage(ctx, cp));
        app.get("/administrator", ctx -> renderAdminPage(ctx, cp));

        app.get("/uploadimage", ctx -> renderUploadImage(ctx, cp));
        // TODO: KATEGORI SØGNING OG ALT DEN LOGIK (f.eks. dropdown med
        // COMMONDENOMINATOR kategorier som søger efter kategori x)
        /*
         * get custom
         */
        app.get("/produkt", ctx -> renderProduct(ctx, cp));
        app.get("/billeder", ctx -> sendImage(ctx, cp));
        app.get("/kategori", ctx -> renderCategorySearch(ctx, cp));
        /*
         * post
         */
        app.post("uploadimage", ctx -> storeImage(ctx, cp));
        /*
         * testing
         * */
        app.beforeMatched(ctx -> ctx.sessionAttribute("admin", true));
    }

    private static void storeImage(@NotNull Context ctx, ConnectionPool cp) {
        String imgUrl = ctx.formParam("imageURL");
        ProductImage img = ProductImageFactory.FromURL(imgUrl);
        if (img != null) {
            try {
                int uploadedImgId = CarportMapper.InsertProductImage(cp, img, false, 0);
                int uploadedImgIdDownscaled = CarportMapper.InsertProductImage(cp, img, true, 200);
                ctx.attribute("message", "success");
                ctx.attribute("uploadedImgId", uploadedImgId);
                ctx.attribute("uploadedImgIdDownscaled", uploadedImgIdDownscaled);
            } catch (DatabaseException e) {
                ctx.attribute("message", "fejl ved upload af billede");
            }
        } else {ctx.attribute("message", "fejl ved upload af billede");}

        renderUploadImage(ctx, cp);
    }

    private static void renderUploadImage(@NotNull Context ctx, ConnectionPool cp) {
        ctx.render("uploadimage.html");
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

    private static void renderSearch(Context ctx, ConnectionPool cp) {
        String searchString = ctx.queryParam("searchString");
        searchString = (searchString == null) ? "" : searchString;
        String pageString = ctx.queryParam("page");
        int pageNumber = -1;
        if (pageString != null) {
            try {
                pageNumber = Integer.parseInt(pageString);
            } catch (NumberFormatException ignored) {
            }
        }

        ctx.attribute("searchStringPrev", searchString);
        try {
            searchString = searchString.toLowerCase();
            String[] searchStringSplit = searchString.split(" ");

            List<Product> productList = CarportMapper.SelectProductsById(cp,
                    pageNumber,
                    CarportMapper.SelectProductIdsByStringMatch(cp,
                            pageNumber,
                            true, true, true,
                            searchStringSplit));
            int[] commonSpecIds = Product.MapProductsToCommonSpecIds(productList);
            if (commonSpecIds != null){
                List<ProductSpecification> commonSpecs = CarportMapper.SelectSpecificationsById(cp, commonSpecIds);
                List<List<String>> commonSpecUniqueDetails = new ArrayList<>();
                for (int i = 0; i < commonSpecIds.length; ++i)
                    commonSpecUniqueDetails.add(Product.MapProductsToUniqueSpecDetails(productList, commonSpecIds[i]));
                ctx.attribute("commonSpecList", commonSpecs);
                ctx.attribute("commonSpecListOptions", commonSpecUniqueDetails);
            }

            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.render("soegning.html");
    }

    private static void renderCategorySearch(Context ctx, ConnectionPool cp) {
        String searchString = ctx.queryParam("category");
        searchString = (searchString == null) ? "" : searchString;
        String pageString = ctx.queryParam("page");
        int pageNumber = -1;
        if (pageString != null) {
            try {
                pageNumber = Integer.parseInt(pageString);
            } catch (NumberFormatException ignored) {
            }
        }

        try {
            List<Product> productList = CarportMapper.SelectProductsById(cp,
                    pageNumber,
                    CarportMapper.SelectProductIdsByStringMatch(cp,
                            pageNumber,
                            false, false, true,
                            searchString));

            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.render("soegning.html");
    }

    private static void renderIndex(Context ctx, ConnectionPool cp) {
        ctx.render("index.html");
    }
}
