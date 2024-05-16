package carport.controllers;

import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.CatAndSpecMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
import carport.tools.ProductImageFactory;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminFunctionController // TODO: ADD ADMIN RESTRICTIONS FOR DEPLOYMENT; NOT NECESSARY WHEN TESTING
{
    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /* GET */
        app.get("/uploadimage", ctx -> renderUploadImage(ctx, cp));
        app.get("/newproduct", ctx -> renderNewProduct(ctx, cp));
        /* POST */
        app.post("createproductdetails", ctx -> createProductDetailsDone(ctx, cp));
        app.post("createproductselectspecs", ctx -> createProductSpecsDone(ctx, cp));
        app.post("createproductselectimages", ctx -> createProductImagesDone(ctx, cp));
        app.post("uploadimage", ctx -> storeImage(ctx, cp));
    }

    private static void renderNewProduct(@NotNull Context ctx, ConnectionPool cp) {
        try {
            List<ProductCategory> categoryList = CatAndSpecMapper.SelectAllCategories(cp);
            ctx.attribute("categorylist", categoryList);
        }
        catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.render("products/createproduct.html");
    }

    private static void renderUploadImage(@NotNull Context ctx, ConnectionPool cp) {
        // if (ctx.sessionAttribute("admin") == null)
        //     return;
        ctx.render("products/uploadimage.html");
    }

    private static void storeImage(@NotNull Context ctx, ConnectionPool cp) {
        // if (ctx.sessionAttribute("admin") == null)
        //     return;
        String imgUrl = ctx.formParam("imageURL");
        ProductImage img = ProductImageFactory.FromURL(imgUrl);
        if (img != null) {
            try {
                int uploadedImgId = ProductMapper.InsertProductImage(cp, img, false, 0);
                int uploadedImgIdDownscaled = ProductMapper.InsertProductImage(cp, img, true, 200);
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
    private static void createProductDetailsDone(@NotNull Context ctx, ConnectionPool cp) {
        // TODO: Clean this stuff up LOL
        String name = ctx.formParam("name");
        String description = ctx.formParam("description");
        String price = ctx.formParam("price");
        String links = ctx.formParam("links");
        List<String> categories = ctx.formParams("categories");
        System.err.println(categories.toString());

        if (name == null || description == null || categories == null || price == null
                || name.length() < 1 || description.length() < 1 || categories.size() < 1
                || price.length() < 1) {
            ctx.attribute("message", "ugyldige værdier ved produktoprettelse");
            ctx.render("createproduct.html");
            return;
        }

        BigDecimal priceBigDecimal = null;
        List<Integer> catIds = new ArrayList<>();
        List<Long> specIds = null;
        List<ProductCategory> cats = new ArrayList<>();
        try {
            for (String s : categories)
                catIds.add(Integer.parseInt(s));
            specIds = ProductCategory.GetCommonSpecIdsFromCategoryIdList(cp, catIds);
            if (specIds != null){
                for (Integer cId : catIds){
                    cats.add(CatAndSpecMapper.SelectCategoryById(cp, cId.intValue()));
                }
            }
            priceBigDecimal = new BigDecimal(price);
        }
        catch (DatabaseException | NumberFormatException e) {
            System.err.println(e.getMessage());
            ctx.attribute("message", "ugyldige værdier ved produktoprettelse");
            ctx.render("products/createproduct.html");
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
        ctx.render("products/createproductselectspecs.html");
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
            ProductMapper.InsertProduct(cp, product);
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
                        CatAndSpecMapper.SelectSpecificationsById(cp, product.SpecIds[i].intValue());
                if (pSpec == null || pSpec.size() != 1)
                    return;
                String pDetails = ctx.formParam(pSpec.get(0).Name);
                if (pDetails == null || pDetails.length() < 1)
                    return;
                product.SpecNames[i] = pSpec.get(0).Name;
                product.SpecDetails[i] = pDetails;
                product.SpecUnits[i] = pSpec.get(0).Unit;
                pCats.add(CatAndSpecMapper.SelectCategoryById(cp, product.SpecIds[i].intValue()));
            }
            ctx.attribute("pcats", pCats);
            ctx.attribute("imageids", imageIds);
            ctx.attribute("imagedownscaledids", imageDownscaledIds);
        }
        catch (DatabaseException e) {System.err.println(e.getMessage()); return;}
        ctx.sessionAttribute("productinmaking", product);
        ctx.render("products/createproductselectimage.html");
    }
}
