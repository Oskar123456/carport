package carport.controllers;

import carport.entities.Product;
import carport.entities.ProductSpecification;
import carport.exceptions.DatabaseException;
import carport.persistence.CatAndSpecMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchController {
    private static final int PAGE_SIZE = 100;

    public static void addRoutes(Javalin app, ConnectionPool cp) {
        /* GET */
        app.get("/soegning", ctx -> renderSearch(ctx, cp));
        app.get("/soegning.html", ctx -> renderSearch(ctx, cp));
        app.get("/filter", ctx -> renderSearch(ctx, cp));
        /* POST */
        app.get("/kategori", ctx -> renderCategorySearch(ctx, cp));
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
            List<Integer> searchCategories = CatAndSpecMapper.SearchCategory(cp, Arrays.asList(searchStringSplit));
            List<Product> productList = null;
            productList = ProductMapper.SelectProductsById(cp,
                    ProductMapper.SearchProducts(cp,
                            pageNumber, PAGE_SIZE,
                            Arrays.asList(searchStringSplit), Arrays.asList(searchStringSplit),
                            searchCategories, searchWithFilters,
                            filterSpecIds, filterSpecDetails));
            List<Long> commonSpecIds = Product.MapProductsToCommonSpecIds(productList);
            List<ProductSpecification> commonSpecs = CatAndSpecMapper.SelectSpecificationsById(cp, commonSpecIds);
            List<List<String>> commonSpecUniqueDetails = new ArrayList<>();
            if (commonSpecs != null)
                for (ProductSpecification commonSpec : commonSpecs)
                    commonSpecUniqueDetails.add(Product.MapProductsToUniqueSpecDetails(productList, commonSpec.Id));
            ctx.attribute("commonSpecList", commonSpecs);
            ctx.attribute("commonSpecListOptions", commonSpecUniqueDetails);
            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.attribute("searchStringPrev", searchString);
        ctx.render("products/soegning.html");
    }

    private static void renderCategorySearch(Context ctx, ConnectionPool cp) {
        // TODO: merge this with regular search, so you have filters as well. Should be
        // simple
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
            List<Integer> catIds = CatAndSpecMapper.SearchCategory(cp, searchString);
            List<Product> productList = ProductMapper.SelectProductsById(cp,
                    ProductMapper.SearchProducts(cp, pageNumber, PAGE_SIZE,
                            null, null,
                            catIds, false,
                            null, null));
            List<Long> commonSpecIds = Product.MapProductsToCommonSpecIds(productList);
            List<ProductSpecification> commonSpecs = CatAndSpecMapper.SelectSpecificationsById(cp, commonSpecIds);
            List<List<String>> commonSpecUniqueDetails = new ArrayList<>();
            if (commonSpecs != null)
                for (ProductSpecification commonSpec : commonSpecs)
                    commonSpecUniqueDetails.add(Product.MapProductsToUniqueSpecDetails(productList, commonSpec.Id));
            ctx.attribute("commonSpecList", commonSpecs);
            ctx.attribute("commonSpecListOptions", commonSpecUniqueDetails);
            ctx.attribute("productList", productList);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        }
        ctx.attribute("searchStringPrev", searchString);
        ctx.render("products/soegning.html");
    }
}
