package carport.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.exceptions.DatabaseException;
import carport.tools.ProductImageFactory;

public class CarportMapper {
    static private int SEARCH_PAGE_SIZE = 25;
    // TODO: maybe this is overcomplication
    static private String SQL_PRODUCT_FULL_SELECTOR;
    static private String SQL_SPECS_OF_CATS;
    static private String SQL_CATS_OF_PRODS;
    static private String SQL_PREDICATE_INJECTION_POINT;
    static private String SQL_ORDERBY_INJECTION_POINT;

    public static void SetSearchPageSize(int n) {
        if (n > 0 && n < 100)
            SEARCH_PAGE_SIZE = n;
    }

    /*
     * Call once
     * TODO: Reset table sequences
     */
    public static void Init() {
        InputStream sqlSpecsOfCatsStream = CarportMapper.class.getResourceAsStream(
                "/sql/select-specs-of-categories.sql");
        InputStream sqlCatsOfProdsStream = CarportMapper.class.getResourceAsStream(
                "/sql/select-categories-of-products.sql");
        InputStream sqlProdFullSelStream = CarportMapper.class.getResourceAsStream(
                "/sql/select-full-product-description.sql");
        try {
            SQL_PRODUCT_FULL_SELECTOR = new String(sqlProdFullSelStream.readAllBytes());
            SQL_CATS_OF_PRODS = new String(sqlCatsOfProdsStream.readAllBytes());
            SQL_SPECS_OF_CATS = new String(sqlSpecsOfCatsStream.readAllBytes());
            sqlSpecsOfCatsStream.close();
            sqlProdFullSelStream.close();
            sqlCatsOfProdsStream.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        SQL_PREDICATE_INJECTION_POINT = "predicate_injection";
        SQL_ORDERBY_INJECTION_POINT = "orderby_injection";
    }

    private static void setSQLPage(PreparedStatement ps, int pageNum, int argNum) throws SQLException {
        ps.setInt(argNum++, SEARCH_PAGE_SIZE);
        ps.setInt(argNum, (pageNum >= 0) ? pageNum * SEARCH_PAGE_SIZE : 0);
    }

    private static String setSQLPredicate(String sql, String predicate) {
        if (sql == null || !sql.contains(SQL_PREDICATE_INJECTION_POINT))
            return sql;
        String[] sqlSplit = sql.split(SQL_PREDICATE_INJECTION_POINT);
        String sqlUpdated = sqlSplit[0] + System.lineSeparator()
                + predicate + sqlSplit[1] + System.lineSeparator();
        return sqlUpdated;
    }

    private static String setSQLOrderBy(String sql, String orderby) {
        if (sql == null || !sql.contains(SQL_ORDERBY_INJECTION_POINT))
            return sql;
        String[] sqlSplit = sql.split(SQL_ORDERBY_INJECTION_POINT);
        String sqlUpdated = sqlSplit[0] + System.lineSeparator()
                + orderby + sqlSplit[1] + System.lineSeparator();
        return sqlUpdated;
    }

    private static String setSQLColumns(String sql, String... columns) {
        if (sql == null || columns == null || columns.length < 1)
            return sql;
        String sqlSubStr = sql.substring(sql.indexOf("FROM"));
        String sqlSelect = "SELECT ";
        for (String s : columns) {
            sqlSelect += s + System.lineSeparator();
        }
        return sqlSelect + sqlSubStr;
    }

    /*
     * User
     */

    /*
     * Order
     */

    /*
     * Product
     */
    private static Product importProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getArray("links"),
                rs.getArray("image_ids"),
                rs.getArray("image_downscaled_ids"),
                rs.getArray("spec_ids"),
                rs.getArray("spec_names"),
                rs.getArray("spec_details"),
                rs.getArray("spec_units"),
                rs.getArray("categories"),
                rs.getArray("doc_ids"),
                rs.getArray("comp_ids"),
                rs.getArray("comp_quantities"));
    }

    /*
     * Main search function for products. Simple substring search
     * through optionally name, description & categories.
     */
    public static int[] SelectProductIdsByStringMatch(ConnectionPool cp,
            int page,
            boolean searchName,
            boolean searchDescription,
            boolean searchCategories,
            String... needles) throws DatabaseException {
        List<Integer> ids = new ArrayList<>();
        if (needles == null || needles.length < 1)
            return null;
        String sql = SQL_PRODUCT_FULL_SELECTOR.replace("ARRAY_AGG(category.name) category_names",
                "STRING_AGG(category.name, ', ') category_names");
        // sql = setSQLColumns(sql, "product.id as id");
        String sqlPredicate = " WHERE ";

        int searchNameIntVal = (searchName) ? 1 : 0;
        int searchDescriptionIntVal = (searchDescription) ? 1 : 0;
        int searchCategoriesIntVal = (searchCategories) ? 1 : 0;
        int searchColNum = searchNameIntVal + searchDescriptionIntVal + searchCategoriesIntVal;
        for (int i = 0; i < needles.length; ++i) {
            if (searchName)
                sqlPredicate += " name ILIKE ? OR ";
            if (searchDescription)
                sqlPredicate += " description ILIKE ? OR ";
            if (searchCategories)
                sqlPredicate += " category_names ILIKE ? OR ";
            if (i == needles.length - 1)
                sqlPredicate = sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR"));
        }
        sql = setSQLPredicate(sql, sqlPredicate);
        /* DEBUG PRINTINT */
        String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.err.printf(
                "[debug::logging]%n\t%s(page %d, searchName %b, searchDescription %b, searchCategories %b, needles: {%s})%n",
                thisMethodName, page, searchName, searchDescription, searchCategories, String.join(", ", needles));
        /* DEBUG PRINTINT */
        try (Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            for (int i = 0; i < needles.length * searchColNum; ++i)
                ps.setString(argNum++, "%" + needles[i / searchColNum] + "%");
            sql = setSQLOrderBy(sql, "ORDER BY product.id ASC");
            setSQLPage(ps, page, argNum);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                ids.add(rs.getInt("id"));
        } catch (SQLException e) {
            throw new DatabaseException("error in database search::" + e.getMessage());
        }
        if (ids.isEmpty())
            return null;
        int[] idsResult = new int[ids.size()];
        for (int i = 0; i < idsResult.length; ++i) {
            idsResult[i] = ids.get(i).intValue();
        }
        return idsResult;
    }

    public static List<Product> SelectProductsById(ConnectionPool cp,
            int page,
            int... ids) throws DatabaseException {
        List<Product> productList = new ArrayList<>();
        if (ids == null || ids.length < 1)
            return productList;
        int argNum = 1;
        String sql = SQL_PRODUCT_FULL_SELECTOR;
        String sqlPredicate = "WHERE ";
        for (int i = 0; i < ids.length; ++i) {
            sqlPredicate += " id = ? ";
            if (i < ids.length - 1)
                sqlPredicate += " OR ";
        }
        sql = setSQLPredicate(sql, sqlPredicate);

        try (Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            for (; argNum <= ids.length; ++argNum)
                ps.setInt(argNum, ids[argNum - 1]);

            sql = setSQLOrderBy(sql, "ORDER BY product.id ASC");
            setSQLPage(ps, page, argNum);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                productList.add(importProduct(rs));
        } catch (SQLException e) {
            throw new DatabaseException("error in database search::" + e.getMessage());
        }
        return productList;
    }

    /*
     * ProductImage
     */
    public static int InsertProductImage(ConnectionPool cp,
            ProductImage img,
            boolean downscaled,
            int newWidth) throws DatabaseException {
        int retval = 0;

        String sql = "INSERT INTO image (id, data, source, name, format) VALUES (DEFAULT, ?, ?, ?, ?)";

        if (downscaled) {
            img = ProductImageFactory.Resize(img, newWidth);
        }

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setBytes(1, img.Data());
            ps.setString(2, img.Source());
            ps.setString(3, img.Name());
            ps.setString(4, img.Format());
            retval = ps.executeUpdate();
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
        return retval;
    }

    /*
     * Category and Specification
     */
    public static List<ProductSpecification> SelectSpecificationsById(ConnectionPool cp,
            int... ids) throws DatabaseException {
        List<ProductSpecification> specs = new ArrayList<>();
        if (ids == null || ids.length < 1)
            return specs;

        String sql = "SELECT * FROM specification WHERE ";
        for (int i = 0; i < ids.length; ++i) {
            sql += " id = ? ";
            if (i < ids.length - 1)
                sql += " OR ";
        }

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            for (int i = 0; i < ids.length; ++i)
                ps.setInt(i + 1, ids[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                specs.add(new ProductSpecification(rs.getInt("id"),
                        rs.getString("name"),
                        null,
                        rs.getString("unit")));
            }
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + "):" + e.getMessage());
        }
        return specs;
    }

    public static ProductCategory SelectCategoryByName(ConnectionPool cp,
            String needle) throws DatabaseException {
        ProductCategory category = null;
        String sql = "SELECT * FROM category WHERE name ILIKE ?";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, needle);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Array sqlCommonSpecIds = rs.getArray("common_specifications");
                int[] commonSpecIds = null;
                if (sqlCommonSpecIds != null) {
                    Long[] sqlCommonSpecIdsObj = (Long[]) sqlCommonSpecIds.getArray();
                    commonSpecIds = new int[sqlCommonSpecIdsObj.length];
                    for (int i = 0; i < commonSpecIds.length; ++i)
                        commonSpecIds[i] = sqlCommonSpecIdsObj[i].intValue();
                }
                category = new ProductCategory(rs.getInt("id"),
                        rs.getString("name"),
                        SelectSpecificationsById(cp, commonSpecIds));
            }
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + "):" + e.getMessage());
        }
        return category;
    }
    /*
     * Selects unique category ids based on provided array of product ids.
     * Will select only common denominators if desired.
     * @param cp
     * @param commonDenominatorOnly
     * @param ids
     * @return
     * @throws DatabaseException
     */
    public static int[] SelectCategoryIdsFromProductIds(ConnectionPool cp,
                                                        boolean commonDenominatorOnly,
                                                        int... ids) throws DatabaseException{
        if (ids == null || ids.length < 1)
            return null;
        List<Integer> catIdsList = new ArrayList<>();
        boolean firstIter = true;

        String sql = SQL_CATS_OF_PRODS;
        String sqlPredicate = " WHERE ";
        for (int i = 0; i < ids.length; ++i){
            sqlPredicate += " id = ? ";
            if (i < ids.length - 1)
                sqlPredicate += " OR ";
        }
        sql = setSQLPredicate(sql, sqlPredicate);

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            for (int i = 0; i < ids.length; ++i)
                ps.setInt(i + 1, ids[i]);
            System.err.println(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Array sqlRowCats = rs.getArray("category_ids");
                if (sqlRowCats == null){
                    if (commonDenominatorOnly)
                        return null; // if one product has no specs, no common denominators exist
                    else
                        continue;
                }
                Long[] RowCats = (Long[]) sqlRowCats.getArray();
                if (commonDenominatorOnly){
                    if (firstIter){
                        for (int i = 0; i < RowCats.length; ++i){
                            catIdsList.add(RowCats[i].intValue());
                        }
                    }
                    else {
                        for (int i = 0; i < catIdsList.size(); ++i){
                            boolean exists = false;
                            for (int j = 0; j < RowCats.length; ++j){
                                if (catIdsList.get(i) == RowCats[i].intValue())
                                    exists = true;
                            }
                            if (!exists)
                                catIdsList.remove(i);
                        }
                    }
                    firstIter = false;
                }
                else {
                    for (int i = 0; i < RowCats.length; ++i){
                        if (!catIdsList.contains(RowCats[i].intValue()))
                            catIdsList.add(RowCats[i].intValue());
                    }
                }

            }
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }
        if (catIdsList.size() < 1)
            return null;
        int[] catIds = new int[catIdsList.size()];
        for (int i = 0; i < catIds.length; ++i)
            catIds[i] = catIdsList.get(i);
        return catIds;
    }

    /*
     * Return array of unique spec ids from provided array of category ids
     * @param cp
     * @param ids
     * @return
     * @throws DatabaseException
     */
    public static int[] SelectSpecIdsFromCategoryIds(ConnectionPool cp,
                                                     int... ids) throws DatabaseException{
        if (ids == null || ids.length < 1)
            return null;
        List<Integer> specIdsList = new ArrayList<>();

        String sql = SQL_CATS_OF_PRODS;
        String sqlPredicate = " WHERE ";
        for (int i = 0; i < ids.length; ++i){
            sqlPredicate += " category_id = ? ";
            if (i < ids.length - 1)
                sqlPredicate += " OR ";
        }
        sql = setSQLPredicate(sql, sqlPredicate);

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            for (int i = 0; i < ids.length; ++i)
                ps.setInt(i + 1, ids[i]);
            System.err.println(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Array sqlRowCats = rs.getArray("spec_id");
                if (sqlRowCats == null)
                    continue;
                Long[] RowCats = (Long[]) sqlRowCats.getArray();
                for (int i = 0; i < RowCats.length; ++i){
                    if (!specIdsList.contains(RowCats[i].intValue()))
                        specIdsList.add(RowCats[i].intValue());
                }
            }
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }
        if (specIdsList.size() < 1)
            return null;
        int[] specIds = new int[specIdsList.size()];
        for (int i = 0; i < specIds.length; ++i)
            specIds[i] = specIdsList.get(i);
        return specIds;
    }

    /*
     * Images
     */
    public static ProductImage SelectProductImageById(ConnectionPool cp,
            int id) throws DatabaseException {
        ProductImage img = null;
        String sql = "SELECT * FROM image WHERE id = ?";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                img = new ProductImage(id,
                        rs.getString("name"),
                        rs.getString("source"),
                        rs.getBytes("data"),
                        rs.getString("format"));
            }
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }
        return img;
    }
}
