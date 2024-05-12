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

public class CarportController {
    private static int PAGE_SIZE = 16;

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
        app.get("/filter", ctx -> renderSearch(ctx, cp));

        app.get("/bygselv", ctx -> renderBygSelv(ctx, cp));
        app.get("/bygselvcarport", ctx -> renderBygSelv(ctx, cp));

        app.get("/admin", ctx -> renderAdminPage(ctx, cp));
        app.get("/administrator", ctx -> renderAdminPage(ctx, cp));

        app.get("/uploadimage", ctx -> renderUploadImage(ctx, cp));
        app.get("/newproduct", ctx -> renderNewProduct(ctx, cp));
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
        app.post("createproductdetails", ctx -> createProductDetailsDone(ctx, cp));
        app.post("createproductselectspecs", ctx -> createProductSpecsDone(ctx, cp));
        app.post("createproductselectimages", ctx -> createProductImagesDone(ctx, cp));

        /*
         * testing only stuff
         */
        if (System.getenv("test") != null && System.getenv("test").equals("true"))
            app.beforeMatched(ctx -> ctx.sessionAttribute("admin", true));
    }

    private static void createProductImagesDone(@NotNull Context ctx, ConnectionPool cp) {
        Product product = ctx.sessionAttribute("productinmaking");
        if (product == null)
            return;
        String regularImgStr = ctx.formParam("regular");
        String downscaledImgStr = ctx.formParam("downscaled");
        if (regularImgStr == null || downscaledImgStr == null)
            return;
        try {
            int regularImgId = Integer.parseInt(regularImgStr);
            int downscaledImgId = Integer.parseInt(downscaledImgStr);
            product.AddImages(false, regularImgId);
            product.AddImages(true, downscaledImgId);
            CarportMapper.InsertProduct(cp, product);
        }
        catch (NumberFormatException | DatabaseException e){
            System.err.println(e.getMessage());
            return;
        }
        ctx.sessionAttribute("productinmaking", null);
        ctx.result("success creating product " + product.Name);
    }

    private static void createProductSpecsDone(@NotNull Context ctx, ConnectionPool cp) {
        Product product = ctx.sessionAttribute("productinmaking");
        if (product == null)
            return;
        try {
            List<Integer> imageIds = CarportMapper.SelectProductImageIds(cp, false);
            List<Integer> imageDownscaledIds = CarportMapper.SelectProductImageIds(cp, true);
            List<ProductCategory> pCats = new ArrayList<>();
            for (int i = 0; i < product.SpecIds.length; ++i){
                List<ProductSpecification> pSpec =
                    CarportMapper.SelectSpecificationsById(cp, product.SpecIds[i].intValue());
                if (pSpec == null || pSpec.size() != 1)
                    return;
                String pDetails = ctx.formParam(pSpec.get(0).Name());
                if (pDetails == null || pDetails.length() < 1)
                    return;
                product.SpecNames[i] = pSpec.get(0).Name();
                product.SpecDetails[i] = pDetails;
                product.SpecUnits[i] = pSpec.get(0).Unit();
                pCats.add(CarportMapper.SelectCategoryById(cp, product.SpecIds[i].intValue()));
            }
            ctx.attribute("pcats", pCats);
            ctx.attribute("imageids", imageIds);
            ctx.attribute("imagedownscaledids", imageDownscaledIds);
        }
        catch (DatabaseException e) {System.err.println(e.getMessage()); return;}
        ctx.sessionAttribute("productinmaking", product);
        ctx.render("createproductselectimage.html");
    }

    private static void createProductDetailsDone(@NotNull Context ctx, ConnectionPool cp) {
        // TODO: Clean this stuff up LOL
        String name = ctx.formParam("name");
        String description = ctx.formParam("description");
        String price = ctx.formParam("price");
        String links = ctx.formParam("links");
        String categories = ctx.formParam("categories");

        if (name == null || description == null || categories == null || price == null
            || name.length() < 1 || description.length() < 1 || categories.length() < 1
            || price.length() < 1) {
            ctx.attribute("message", "ugyldige værdier ved produktoprettelse");
            ctx.render("createproduct.html");
            return;
        }

        BigDecimal priceBigDecimal = null;
        List<Integer> catIds = null;
        List<Long> specIds = null;
        List<ProductCategory> cats = new ArrayList<>();
        try {
            String[] categoriesSplit = categories.split(",");
            for (int i = 0; i < categoriesSplit.length; ++i)
                categoriesSplit[i] = categoriesSplit[i].trim();
            catIds = CarportMapper.SearchCategory(cp, Arrays.asList(categoriesSplit));
            if (catIds != null)
                specIds = ProductCategory.GetCommonSpecIdsFromCategoryIdList(cp, catIds);
            if (specIds != null){
                for (Integer cId : catIds){
                    cats.add(CarportMapper.SelectCategoryById(cp, cId.intValue()));
                }
            }
            priceBigDecimal = new BigDecimal(price);
        }
        catch (DatabaseException | NumberFormatException e) {
            System.err.println(e.getMessage());
            ctx.attribute("message", "ugyldige værdier ved produktoprettelse");
            ctx.render("createproduct.html");
            return;
        }

        if (catIds != null && priceBigDecimal != null){
            String[] linksSplit = links.split(",");
            for (int i = 0; i < linksSplit.length; ++i)
                linksSplit[i] = linksSplit[i].trim();
            Long[] catIdsLongArray = new Long[catIds.size()];
            Long[] specIdsLongArray = new Long[specIds.size()];
            for (int i = 0; i < catIds.size(); ++i)
                catIdsLongArray[i] = Long.valueOf(catIds.get(i));
            for (int i = 0; i < specIds.size(); ++i)
                specIdsLongArray[i] = Long.valueOf(specIds.get(i));
            Product product = new Product(name, description,
                                          priceBigDecimal, linksSplit,
                                          catIdsLongArray, specIdsLongArray);
            List<ProductSpecification> requiredSpecs =
                ProductCategory.GetCommonSpecsFromCategoryIdList(cp, catIds);
            ctx.attribute("cats", cats);
            ctx.attribute("requiredspecs", requiredSpecs);
            ctx.sessionAttribute("productinmaking", product);
        } else {
            ctx.attribute("message", "ugyldige værdier ved produktoprettelse");
            ctx.render("createproduct.html");
            return;
        }
        System.err.println(cats.toString());
        ctx.render("createproductselectspecs.html");
    }

    private static void renderNewProduct(@NotNull Context ctx, ConnectionPool cp) {
        ctx.render("createproduct.html");
    }

    private static void storeImage(@NotNull Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("admin") == null)
            return;
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
        } else {
            ctx.attribute("message", "fejl ved upload af billede");
        }

        renderUploadImage(ctx, cp);
    }

    private static void renderUploadImage(@NotNull Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("admin") == null)
            return;
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
        int pageNumber = 0;
        if (pageString != null) {
            try {
                pageNumber = Integer.parseInt(pageString);
            } catch (NumberFormatException ignored) {
            }
        }

        /* filter params */
        boolean searchWithFilters = true;
        List<String> queryFilterSpecIds = ctx.queryParams("specIds");
        List<Integer> filterSpecIds = new ArrayList<>();
        List<List<String>> filterSpecDetails = new ArrayList<>();
        if (queryFilterSpecIds.size() > 0) {
            for (String s : queryFilterSpecIds) {
                int sId = -1;
                try {
                    sId = Integer.parseInt(s);
                } catch (NumberFormatException | NullPointerException e) {
                    searchWithFilters = false;
                    break;
                }
                List<String> sDetails = ctx.queryParams(s);
                if (sDetails.size() > 0 && sId >= 0) {
                    filterSpecDetails.add(sDetails);
                    filterSpecIds.add(sId);
                }
            }
        } else {
            searchWithFilters = false;
        }

        try {
            searchString = searchString.toLowerCase();
            String[] searchStringSplit = searchString.split(" ");
            List<Integer> searchCategories = CarportMapper.SearchCategory(cp, Arrays.asList(searchStringSplit));
            List<Product> productList = null;
            productList = CarportMapper.SelectProductsById(cp,
                    pageNumber, CarportMapper.SearchProducts(cp,
                            pageNumber, PAGE_SIZE,
                            Arrays.asList(searchStringSplit), Arrays.asList(searchStringSplit),
                            searchCategories, searchWithFilters,
                            filterSpecIds, filterSpecDetails));
            List<Long> commonSpecIds = Product.MapProductsToCommonSpecIds(productList);
            List<ProductSpecification> commonSpecs = CarportMapper.SelectSpecificationsById(cp, commonSpecIds);
            List<List<String>> commonSpecUniqueDetails = new ArrayList<>();
            if (commonSpecs != null)
                for (ProductSpecification commonSpec : commonSpecs)
                    commonSpecUniqueDetails.add(Product.MapProductsToUniqueSpecDetails(productList, commonSpec.Id()));
            ctx.attribute("commonSpecList", commonSpecs);
            ctx.attribute("commonSpecListOptions", commonSpecUniqueDetails);
            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.attribute("searchStringPrev", searchString);
        ctx.render("soegning.html");
    }

    private static void renderCategorySearch(Context ctx, ConnectionPool cp) {
        String searchString = ctx.queryParam("category");
        searchString = (searchString == null) ? "" : searchString;
        String pageString = ctx.queryParam("page");
        int pageNumber = 0;
        if (pageString != null) {
            try {
                pageNumber = Integer.parseInt(pageString);
            } catch (NumberFormatException ignored) {
            }
        }
        try {
            List<Integer> catIds = CarportMapper.SearchCategory(cp, searchString);
            List<Product> productList = CarportMapper.SelectProductsById(cp,
                    pageNumber, CarportMapper.SearchProducts(cp, pageNumber, PAGE_SIZE,
                                                             null, null,
                                                             catIds, false,
                                                             null, null));

            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.attribute("searchStringPrev", searchString);
        ctx.render("soegning.html");
    }

    private static void renderIndex(Context ctx, ConnectionPool cp) {
        ctx.render("index.html");
    }
}
