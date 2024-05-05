package carport.persistence;

import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.entities.ProductComponent;
import carport.exceptions.DatabaseException;
import carport.tools.ProductImageFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class CarportMapper {
    static private int SEARCH_PAGE_SIZE = 25;
    // TODO: maybe this is overcomplication
    static private String SQL_PRODUCT_FULL_SELECTOR;
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
        InputStream sqlInputStream = CarportMapper.class.getResourceAsStream(
                "/sql/select-full-product-description.sql");
        try {
            SQL_PRODUCT_FULL_SELECTOR = new String(sqlInputStream.readAllBytes());
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


    private static String setSQLPredicate(String sql, String predicate){
        if (sql == null || !sql.contains(SQL_PREDICATE_INJECTION_POINT))
            return sql;
        String[] sqlSplit = sql.split(SQL_PREDICATE_INJECTION_POINT);
        String sqlUpdated = sqlSplit[0] + System.lineSeparator()
            + predicate + sqlSplit[1] + System.lineSeparator();
        return sqlUpdated;
    }

    private static String setSQLOrderBy(String sql, String orderby){
        if (sql == null || !sql.contains(SQL_ORDERBY_INJECTION_POINT))
            return sql;
        String[] sqlSplit = sql.split(SQL_ORDERBY_INJECTION_POINT);
        String sqlUpdated = sqlSplit[0] + System.lineSeparator()
            + orderby + sqlSplit[1] + System.lineSeparator();
        return sqlUpdated;
    }

    private static String setSQLColumns(String sql, String... columns){
        if (sql == null || columns == null || columns.length < 1)
            return sql;
        String sqlSubStr = sql.substring(sql.indexOf("FROM"));
        String sqlSelect = "SELECT ";
        for (String s : columns){
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
                rs.getArray("image_ids_mini"),
                rs.getArray("spec_names"),
                rs.getArray("spec_details"),
                rs.getArray("spec_units"),
                rs.getArray("categories"),
                rs.getArray("doc_ids"),
                rs.getArray("comp_ids"),
                rs.getArray("comp_quantities"));
    }

    public static int[] SelectProductIdsByStringMatch(ConnectionPool cp,
                                                      int page,
                                                      String... needles) throws DatabaseException{
        List<Integer> ids = new ArrayList<>();
        if (needles == null || needles.length < 1)
            return null;

        String sql = SQL_PRODUCT_FULL_SELECTOR.
            replace("ARRAY_AGG(category.name) category_names",
                    "STRING_AGG(category.name, ', ') category_names");
        //sql = setSQLColumns(sql, "product.id as id");
        String sqlPredicate = " WHERE ";
        for (int i = 0; i < needles.length; ++i){
            sqlPredicate += " name ILIKE ? OR ";
            sqlPredicate += " description ILIKE ? OR ";
            sqlPredicate += " category_names ILIKE ? ";
            if (i < needles.length - 1)
                sqlPredicate += " OR ";
        }
        sql = setSQLPredicate(sql, sqlPredicate);

        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            for (int i = 0; i < needles.length; ++i){
                ps.setString(argNum++, "%" + needles[i] + "%");
                ps.setString(argNum++, "%" + needles[i] + "%");
                ps.setString(argNum++, "%" + needles[i] + "%");
            }
            sql = setSQLOrderBy(sql, "ORDER BY product.id ASC");
            setSQLPage(ps, page, argNum);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                ids.add(rs.getInt("id"));
        }
        catch (SQLException e) {
            throw new DatabaseException("error in database search::" + e.getMessage());
        }
        if (ids.isEmpty())
            return null;
        int[] idsResult = new int[ids.size()];
        for (int i = 0; i < idsResult.length; ++i){
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
        }
        catch (SQLException e) {
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
