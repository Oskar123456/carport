package carport.controllers;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import carport.entities.CustomCarport;
import carport.entities.Product;
import carport.exceptions.DatabaseException;
import carport.persistence.CatAndSpecMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * CustomCarportController
 */
public class CustomCarportController {

    public static void addRoutes(Javalin app, ConnectionPool cp) {
        app.get("/customcarport", ctx -> renderCustomCarport(ctx, cp));
        app.get("/customcarportcreate", ctx -> renderCustomCarportCreate(ctx, cp));
    }

    private static void renderCustomCarportCreate(Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("admin") == null)
            return;
        String lStr = ctx.queryParam("length");
        String wStr = ctx.queryParam("width");
        String hStr = ctx.queryParam("height");
        String sbStr = ctx.queryParam("shedbool");
        String slStr = ctx.queryParam("shedlength");
        String swStr = ctx.queryParam("shedwidth");

        String stolpeStr = ctx.queryParam("stolpe");
        String remStr = ctx.queryParam("rem");
        String spaerStr = ctx.queryParam("spaer");
        String sternStr = ctx.queryParam("stern");
        String tagpladerStr = ctx.queryParam("tagplader");

        if (lStr == null ||
            wStr == null){
            ctx.result("invalid input");
            return;
        }

        try {
            CustomCarport cc = new CustomCarport();
            int lInt = Integer.parseInt(lStr) * 10;
            int wInt = Integer.parseInt(wStr) * 10;
            int hInt = Integer.parseInt(hStr) * 10;
            int slInt = (sbStr != null) ? Integer.parseInt(slStr) * 10 : 0;
            int swInt = (sbStr != null) ? Integer.parseInt(swStr) * 10 : 0;

            Product stolpe = ProductMapper.SelectProductsById(cp, Integer.parseInt(stolpeStr)).get(0);
            Product rem = ProductMapper.SelectProductsById(cp, Integer.parseInt(remStr)).get(0);
            Product spaer = ProductMapper.SelectProductsById(cp, Integer.parseInt(spaerStr)).get(0);
            Product stern = ProductMapper.SelectProductsById(cp, Integer.parseInt(sternStr)).get(0);
            Product tagplade = ProductMapper.SelectProductsById(cp, Integer.parseInt(tagpladerStr)).get(0);

            cc.SetStolpe(cp, stolpe);
            cc.SetRem(cp, rem);
            cc.SetSpaer(cp, spaer);
            cc.SetStern(cp, stern);
            cc.SetTagplade(cp, tagplade);

            cc.Make(wInt, lInt, hInt, (sbStr != null), swInt, slInt);

            int success = cc.WriteToDb(cp);

            if (success > 0){
                Product product = ProductMapper.SelectProductsById(cp, success).get(0);
                List<Product> compList = new ArrayList<>();
                if (product.CompIds != null && product.CompIds.length > 0){
                    for (int i = 0; i < product.CompIds.length; ++i){
                        Product comp = ProductMapper.SelectProductsById(cp, product.CompIds[i].intValue()).get(0);
                        compList.add(comp);
                    }
                }
                Product.LoadFullSpecs(cp, compList);
                ctx.attribute("complist", compList);
                ctx.attribute("product", product);
                ctx.attribute("baseprice", product.GetSumOfComponentPrices(cp));
                ctx.redirect("/produkt?id=" + success);
            } else {
                ctx.redirect("/customcarport");
                return;
            }
         }
        catch (NumberFormatException | DatabaseException | NullPointerException e) {
            ctx.result("invalid input " + e.getMessage());
            return;
        }
    }

    private static void renderCustomCarport(Context ctx, ConnectionPool cp) {
        if (ctx.sessionAttribute("admin") == null)
            return;
        try {
            List<Product> stolpeList = ProductMapper.SelectProductsById(cp,
                    ProductMapper.SearchProducts(cp, -1, -1, null, null,
                            CatAndSpecMapper.SearchCategory(cp, "stolpe"), false, null, null));
            Product.LoadFullSpecs(cp, stolpeList);
            List<Product> spaerList = ProductMapper.SelectProductsById(cp,
                    ProductMapper.SearchProducts(cp, -1, -1, null, null,
                            CatAndSpecMapper.SearchCategory(cp, "lægte"), false, null, null));
            Product.LoadFullSpecs(cp, spaerList);
            List<Product> remList = ProductMapper.SelectProductsById(cp,
                    ProductMapper.SearchProducts(cp, -1, -1, null, null,
                            CatAndSpecMapper.SearchCategory(cp, "brædde"), false, null, null));
            Product.LoadFullSpecs(cp, remList);
            List<Product> sternList = ProductMapper.SelectProductsById(cp,
                    ProductMapper.SearchProducts(cp, -1, -1, null, null,
                            CatAndSpecMapper.SearchCategory(cp, "stern"), false, null, null));
            Product.LoadFullSpecs(cp, sternList);
            List<Product> tagpladeList = ProductMapper.SelectProductsById(cp,
                    ProductMapper.SearchProducts(cp, -1, -1, null, null,
                            CatAndSpecMapper.SearchCategory(cp, "tagplade"), false, null, null));
            Product.LoadFullSpecs(cp, tagpladeList);
            ctx.attribute("stolpeList", stolpeList);
            ctx.attribute("spaerList", spaerList);
            ctx.attribute("remList", remList);
            ctx.attribute("sternList", sternList);
            ctx.attribute("tagpladeList", tagpladeList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            ctx.result(e.getMessage());
        }

        ctx.render("products/customcarport.html");
    }
}
