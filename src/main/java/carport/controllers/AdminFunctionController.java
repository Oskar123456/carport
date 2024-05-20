package carport.controllers;

import carport.entities.*;
import carport.exceptions.DatabaseException;
import carport.persistence.*;
import carport.tools.ProductImageFactory;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminFunctionController // TODO: ADD ADMIN RESTRICTIONS FOR DEPLOYMENT; NOT NECESSARY WHEN TESTING
{
    public static void addRoutes(Javalin app, ConnectionPool cp)
    {
        /* GET */
        app.get("/uploadimage", ctx -> renderUploadImage(ctx, cp));
        app.get("/newproduct", ctx -> renderNewProduct(ctx, cp));
        app.get("/deleteproduct", ctx -> deleteProduct(ctx, cp));
        app.get("/showorders", ctx -> showOrders(ctx, cp));
        /* POST */
        app.post("createproductdetails", ctx -> createProductDetailsDone(ctx, cp));
        app.post("createproductselectspecs", ctx -> createProductSpecsDone(ctx, cp));
        app.post("createproductselectimages", ctx -> createProductImagesDone(ctx, cp));
        app.post("uploadimage", ctx -> storeImage(ctx, cp));
        app.post("/approvependingorder", ctx -> approveOrder(ctx, cp));
        app.post("/removeorder", ctx -> removeOrder(ctx, cp));
        app.post("/removeorderproduct", ctx -> removeOrderProduct(ctx, cp));
        app.post("/updateordercarport", ctx -> updateOrderCarport(ctx, cp));
    }

    private static void updateOrderCarport(Context ctx, ConnectionPool cp)
    {
        String updateOrderCpPid = ctx.formParam("updateordercarportPID");
        String updateOrderCpOid = ctx.formParam("updateordercarportOID");
        if (ctx.sessionAttribute("admin") == null ||
                updateOrderCpPid == null ||
                updateOrderCpOid == null) {
            ctx.redirect("/");
            return;
        }
        ctx.sessionAttribute("updateordercarportPID", updateOrderCpPid);
        ctx.sessionAttribute("updateordercarportOID", updateOrderCpOid);
        ctx.redirect("/customcarport");
    }

    private static void approveOrder(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null) {
            ctx.redirect("/");
            return;
        }
        String id = ctx.formParam("id");
        try {
            OrderMapper.ApproveOrder(cp, Integer.parseInt(id));
        } catch (NumberFormatException | DatabaseException e) {
        }
        ctx.redirect("/showorders");
    }

    private static void removeOrderProduct(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null) {
            ctx.redirect("/");
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
        ctx.redirect("/showorders");
    }

    private static void removeOrder(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null) {
            ctx.redirect("/");
            return;
        }
        String id = ctx.formParam("id");
        try {
            OrderMapper.DeleteOrder(cp, Integer.parseInt(id));
        } catch (NumberFormatException | DatabaseException e) {
        }
        ctx.redirect("/showorders");
    }

    private static void showOrders(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null) {
            ctx.redirect("/");
            return;
        }
        try {
            List<Order> pendingOrders = OrderMapper.SelectAllOrders(cp, -1, -1, 4);
            List<Order> validatedOrders = OrderMapper.SelectAllOrders(cp, -1, -1, 3);
            List<Order> confirmedOrders = OrderMapper.SelectAllOrders(cp, -1, -1, 2);
            List<Order> doneOrders = OrderMapper.SelectAllOrders(cp, -1, -1, 1);

            Order.LoadList(cp, pendingOrders);
            Order.LoadList(cp, confirmedOrders);
            Order.LoadList(cp, validatedOrders);
            Order.LoadList(cp, doneOrders);

            ctx.attribute("pending", pendingOrders);
            ctx.attribute("confirmed", confirmedOrders);
            ctx.attribute("validated", validatedOrders);
            ctx.attribute("done", doneOrders);
        } catch (NumberFormatException | DatabaseException e) {
            System.err.println(e.getMessage());
            ctx.redirect("/");
            return;
        }
        ctx.render("orders/showallordersadmin.html");
    }

    private static void deleteProduct(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null)
            return;
        try {
            String idStr = ctx.queryParam("id");
            int id = Integer.parseInt(idStr);
            ProductMapper.DeleteProduct(cp, id);
            ctx.result("successfully delete product [ID:" + id + "]");
            return;
        } catch (NumberFormatException | DatabaseException e) {
            ctx.result("could not delete " + e.getMessage());
            return;
        }
    }

    private static void renderNewProduct(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null)
            return;
        try {
            List<ProductCategory> categoryList = CatAndSpecMapper.SelectAllCategories(cp);
            ctx.attribute("categorylist", categoryList);
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
        }
        ctx.render("products/createproduct.html");
    }

    private static void renderUploadImage(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null)
            return;
        ctx.render("products/uploadimage.html");
    }

    private static void storeImage(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null)
            return;
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

    private static void createProductDetailsDone(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null)
            return;
        // TODO: Clean this stuff up LOL
        String name = ctx.formParam("name");
        String description = ctx.formParam("description");
        String price = ctx.formParam("price");
        String links = ctx.formParam("links");
        List<String> categories = ctx.formParams("categories");

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
            if (specIds != null) {
                for (Integer cId : catIds) {
                    cats.add(CatAndSpecMapper.SelectCategoryById(cp, cId.intValue()));
                }
            }
            priceBigDecimal = new BigDecimal(price);
        } catch (DatabaseException | NumberFormatException e) {
            ctx.attribute("message", "ugyldige værdier ved produktoprettelse " + e.getMessage());
            ctx.render("products/createproduct.html");
            return;
        }

        if (catIds != null && priceBigDecimal != null) {
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
            List<ProductSpecification> requiredSpecs = ProductCategory.GetCommonSpecsFromCategoryIdList(cp, catIds);
            ctx.attribute("cats", cats);
            ctx.attribute("requiredspecs", requiredSpecs);
            ctx.sessionAttribute("productinmaking", product);
        } else {
            ctx.attribute("message", "ugyldige værdier ved produktoprettelse");
            ctx.render("createproduct.html");
            return;
        }
        ctx.render("products/createproductselectspecs.html");
    }

    private static void createProductImagesDone(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null)
            return;
        Product product = ctx.sessionAttribute("productinmaking");
        if (product == null) {
            ctx.result("something went wrong");
            return;
        }
        String regularImgStr = ctx.formParam("regular");
        String downscaledImgStr = ctx.formParam("downscaled");
        try {
            int regularImgId = (regularImgStr != null) ? Integer.parseInt(regularImgStr)
                    : Product.GetPlaceHolderImageId()[0];
            int downscaledImgId = (regularImgStr != null) ? Integer.parseInt(downscaledImgStr)
                    : Product.GetPlaceHolderImageId()[1];
            product.AddImages(false, regularImgId);
            product.AddImages(true, downscaledImgId);
            ProductMapper.InsertProduct(cp, false, product);
        } catch (NumberFormatException | DatabaseException e) {
            ctx.result(e.getMessage());
            return;
        }
        ctx.sessionAttribute("productinmaking", null);
        ctx.result("success creating product " + product.Name);
    }

    private static void createProductSpecsDone(Context ctx, ConnectionPool cp)
    {
        if (ctx.sessionAttribute("admin") == null)
            return;
        Product product = ctx.sessionAttribute("productinmaking");
        if (product == null)
            return;
        try {
            List<Integer> imageIds = CarportMapper.SelectProductImageIds(cp, false);
            List<Integer> imageDownscaledIds = CarportMapper.SelectProductImageIds(cp, true);
            List<ProductCategory> pCats = new ArrayList<>();
            for (int i = 0; i < product.SpecIds.length; ++i) {
                List<ProductSpecification> pSpec = CatAndSpecMapper.SelectSpecificationsById(cp,
                        product.SpecIds[i].intValue());
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
            ctx.attribute("cats", pCats);
            ctx.attribute("imageids", imageIds);
            ctx.attribute("imagedownscaledids", imageDownscaledIds);
        } catch (DatabaseException e) {
            ctx.result(e.getMessage());
            return;
        }
        ctx.sessionAttribute("productinmaking", product);
        ctx.render("products/createproductselectimage.html");
    }
}
