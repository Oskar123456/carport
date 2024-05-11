package carport.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.entities.ProductImage;
import carport.entities.ProductSpecification;
import carport.exceptions.DatabaseException;
import carport.tools.ProductImageFactory;

public class CarportMapper {
    static private String SQL_SELECT_PRODUCTS_BY_ID;

    static private int PRODUCT_IMG_NOTFOUND_PLACEHOLDER = 46;
    static private int PRODUCT_IMG_NOTFOUND_PLACEHOLDER_DOWNSCALED = 47;

    /*
     * Call once
     * TODO: Reset table sequences
     */
    public static void Init(ConnectionPool cp) {
        InputStream sqlSelProdsStream = CarportMapper.class.getResourceAsStream("/sql/select-products-by-id.sql");
        try {
            SQL_SELECT_PRODUCTS_BY_ID = new String(sqlSelProdsStream.readAllBytes());
        } catch (IOException e) {
            System.err.println("CarportMapper failed to load sql statements from file");
        }
        Product.SetPlaceholderImgs(PRODUCT_IMG_NOTFOUND_PLACEHOLDER, PRODUCT_IMG_NOTFOUND_PLACEHOLDER_DOWNSCALED);
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
    /*
     * Main search function for products. Simple substring search
     * through optionally name, description & categories. Can filter
     * based on specid-specdetails.
     */
    // TODO: spec search either in new func or this one
    public static List<Integer> SearchProducts(ConnectionPool cp,
            int pageNum,
            int pageSize,
            List<String> nameNeedles,
            List<String> descriptionNeedles,
            List<Integer> categoryIds,
            boolean shouldFilter,
            List<Integer> specIds,
            List<List<String>> specDetails) throws DatabaseException {
        List<Integer> ids = new ArrayList<>();

        String sql = """
                SELECT p.id FROM (SELECT *
                    FROM product
                ) as p
                INNER JOIN (SELECT *
                    FROM product_category
                ) as pcat
                ON p.id = pcat.product_id
                -- should_filter
                INNER JOIN (SELECT *
                    FROM product_specification
                    -- predicate_position_specification
                ) as pspec
                ON p.id = pspec.product_id
                -- should_filter
                -- predicate_position_product
                GROUP BY p.id
                ORDER BY p.id
                LIMIT ? OFFSET ?""";

        String sqlPredicate = "";
        String sqlSpecPredicate = "";
        if (nameNeedles != null && nameNeedles.size() > 0)
            for (int i = 0; i < nameNeedles.size(); ++i)
                sqlPredicate += " name ILIKE ? OR ";
        if (descriptionNeedles != null && descriptionNeedles.size() > 0)
            for (int i = 0; i < descriptionNeedles.size(); ++i)
                sqlPredicate += " description ILIKE ? OR ";
        if (categoryIds != null && categoryIds.size() > 0)
            for (int i = 0; i < categoryIds.size(); ++i)
                sqlPredicate += " pcat.category_id = ? OR ";
        if (shouldFilter && specIds != null) {
            for (int i = 0; i < specIds.size() ; ++i){
                if (specDetails.get(i) != null)
                    for (int j = 0; j < specDetails.get(i).size(); ++j)
                        sqlSpecPredicate += " (specification_id = ? AND details = ?) OR ";            }
        } else
            sql = sql.replaceAll("should_filter(.|\s)*should_filter", System.lineSeparator());
        if (sqlPredicate.length() > 0) {
            sqlPredicate = sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR"));
            sqlPredicate = System.lineSeparator() + " WHERE " + sqlPredicate + System.lineSeparator();
        }
        if (sqlSpecPredicate.length() > 0) {
            sqlSpecPredicate = sqlSpecPredicate.substring(0, sqlSpecPredicate.lastIndexOf("OR"));
            sqlSpecPredicate = System.lineSeparator() + " WHERE " + sqlSpecPredicate + System.lineSeparator();
        }

        sql = sql.replace("predicate_position_product", sqlPredicate);
        sql = sql.replace("predicate_position_specification", sqlSpecPredicate);

        /* DEBUG PRINTING */
        // String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        // System.err.printf("%s::%n%s%n%n%n%n%n%n",
        //         thisMethodName,
        //      sql);
        /* DEBUG PRINTING */

        try (Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            if (shouldFilter)
                for (int i = 0; i < specIds.size(); ++i) {
                    for (String detail : specDetails.get(i)) {
                        ps.setInt(argNum++, specIds.get(i));
                        ps.setString(argNum++, detail);
                    }
                }
            if (nameNeedles != null)
                for (int i = 0; i < nameNeedles.size(); ++i)
                    ps.setString(argNum++, '%' + nameNeedles.get(i) + '%');
            if (descriptionNeedles != null)
                for (int i = 0; i < descriptionNeedles.size(); ++i)
                    ps.setString(argNum++, '%' + descriptionNeedles.get(i) + '%');
            if (categoryIds != null)
                for (int i = 0; i < categoryIds.size(); ++i)
                    ps.setInt(argNum++, categoryIds.get(i));
            ps.setInt(argNum++, pageSize);
            ps.setInt(argNum++, pageNum);
            System.err.println(ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                ids.add(rs.getInt("id"));
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return ids;
    }

    public static List<Product> SelectProductsById(ConnectionPool cp,
            int page,
            List<Integer> ids) throws DatabaseException {
        if (ids == null || ids.size() < 1)
            return null;
        int[] idsArray = new int[ids.size()];
        for (int i = 0; i < idsArray.length; ++i)
            idsArray[i] = ids.get(i);
        return SelectProductsById(cp, page, idsArray);
    }

    public static List<Product> SelectProductsById(ConnectionPool cp,
            int page,
            int... ids) throws DatabaseException {
        List<Product> productList = new ArrayList<>();

        String sql = SQL_SELECT_PRODUCTS_BY_ID;
        String sqlPredicate = "";
        if (ids != null) {
            for (int i = 0; i < ids.length; ++i)
                sqlPredicate += " id = ? OR ";
        }
        if (sqlPredicate.length() > 0)
            sqlPredicate = System.lineSeparator() + " WHERE "
                    + sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR")) + System.lineSeparator();
        sql = sql.replace("predicate_position_product", sqlPredicate);

        try (Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            if (ids != null)
                for (int i = 0; i < ids.length; ++i)
                    ps.setInt(argNum++, ids[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                productList.add(Product.ImportFromDB(rs));
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
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
        int dbGeneratedImgId = 0;

        String sql = "INSERT INTO image (id, data, source, name, format) VALUES (DEFAULT, ?, ?, ?, ?)";

        if (downscaled) {
            img = ProductImageFactory.Resize(img, newWidth);
        }

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            ps.setBytes(1, img.Data());
            ps.setString(2, img.Source());
            ps.setString(3, img.Name());
            ps.setString(4, img.Format());
            int success = ps.executeUpdate();
            if (success < 1)
                return -1;
            ResultSet generatedId = ps.getGeneratedKeys();
            if (generatedId.next())
                dbGeneratedImgId = (int) generatedId.getLong(1);
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
        return dbGeneratedImgId;
    }

    /*
     * Category and Specification
     */
    public static List<Integer> SearchCategory(ConnectionPool cp,
                                               String needle) throws DatabaseException {
        if (needle == null)
            return null;
        List<String> needleAsList = new ArrayList<>();
        needleAsList.add(needle);
        return SearchCategory(cp, needleAsList);
    }
    public static List<Integer> SearchCategory(ConnectionPool cp,
            List<String> needles) throws DatabaseException {
        List<Integer> ids = new ArrayList<>();

        String sql = """
                SELECT id
                FROM category
                -- predicate_position_category
                ORDER BY id""";

        String sqlCatPredicate = "";
        if (needles != null && needles.size() > 0)
            for (int i = 0; i < needles.size(); ++i)
                sqlCatPredicate += " name ILIKE ? OR ";
        if (sqlCatPredicate.length() > 0) {
            sqlCatPredicate = sqlCatPredicate.substring(0, sqlCatPredicate.lastIndexOf("OR"));
            sqlCatPredicate = System.lineSeparator() + " WHERE " + sqlCatPredicate + System.lineSeparator();
            sql = sql.replace("predicate_position_category", sqlCatPredicate);
        }

        /* DEBUG PRINTING */
        // String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        // System.err.printf("%s::%n\tsqlCatPredicate : %s%n\tFULL:%n%s%n%n",
        //         thisMethodName,
        //         sqlCatPredicate,
        //         sql);
        /* DEBUG PRINTING */

        try (Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            if (needles != null)
                for (String s : needles)
                    ps.setString(argNum++, s);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                ids.add(rs.getInt("id"));
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }

        return ids;
    }

    public static List<ProductSpecification> SelectSpecificationsById(ConnectionPool cp,
            List<Long> ids) throws DatabaseException {
        if (ids == null || ids.size() < 1)
            return null;
        int[] idsArray = new int[ids.size()];
        for (int i = 0; i < ids.size(); ++i)
            idsArray[i] = ids.get(i).intValue();
        return SelectSpecificationsById(cp, idsArray);
    }

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

    /*
     * Selects unique category ids based on provided array of product ids.
     * Will select only common denominators if desired.
     *
     * @param cp
     *
     * @param commonDenominatorOnly
     *
     * @param ids
     *
     * @return
     *
     * @throws DatabaseException
     */
    public static int[] SelectCategoryIdsFromProductIds(ConnectionPool cp,
            boolean commonDenominatorOnly,
            int... ids) throws DatabaseException {
        if (ids == null || ids.length < 1)
            return null;
        List<Integer> catIdsList = new ArrayList<>();
        boolean firstIter = true;

        String sql = """
                SELECT DISTINCT category_ids

                FROM product

                LEFT JOIN
                (
                    SELECT product_category.product_id as pid,
                        ARRAY_AGG(product_category.category_id) category_ids
                    FROM product_category
                    INNER JOIN
                    category
                    on category.id = product_category.category_id
                    GROUP BY product_category.product_id
                ) as pCats
                ON product.id = pCats.pid
                -- predicate_position_product """;
        String sqlPredicate = "";
        for (int i = 0; i < ids.length; ++i)
            sqlPredicate += " id = ? OR ";
        if (sqlPredicate.length() > 0)
            sqlPredicate = System.lineSeparator() + " WHERE " + sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR")) + System.lineSeparator();

        sql = sql.replace("predicate_position_product", sqlPredicate);

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            for (int i = 0; i < ids.length; ++i)
                ps.setInt(i + 1, ids[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Array sqlRowCats = rs.getArray("category_ids");
                if (sqlRowCats == null) {
                    if (commonDenominatorOnly)
                        return null; // if one product has no specs, no common denominators exist
                    else
                        continue;
                }
                Long[] RowCats = (Long[]) sqlRowCats.getArray();
                if (commonDenominatorOnly) {
                    if (firstIter) {
                        for (int i = 0; i < RowCats.length; ++i) {
                            catIdsList.add(RowCats[i].intValue());
                        }
                    } else {
                        for (int i = 0; i < catIdsList.size(); ++i) {
                            boolean exists = false;
                            for (int j = 0; j < RowCats.length; ++j) {
                                if (catIdsList.get(i) == RowCats[i].intValue())
                                    exists = true;
                            }
                            if (!exists)
                                catIdsList.remove(i);
                        }
                    }
                    firstIter = false;
                } else {
                    for (int i = 0; i < RowCats.length; ++i) {
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
     *
     * @param cp
     *
     * @param ids
     *
     * @return
     *
     * @throws DatabaseException
     */
    public static int[] SelectSpecIdsFromCategoryIds(ConnectionPool cp,
            int... ids) throws DatabaseException {
        if (ids == null || ids.length < 1)
            return null;
        List<Integer> specIdsList = new ArrayList<>();

        String sql = """
                SELECT category.id as category_id,
                category.name as category_name,
                catSpecs.spec_name,
                catSpecs.spec_unit,
                catSpecs.spec_id
                FROM category
                LEFT JOIN
                (
                SELECT category_specification.category_id as category_id,
                    ARRAY_AGG(specification.name) spec_name,
                    ARRAY_AGG(specification.unit) spec_unit,
                    ARRAY_AGG(specification.id) spec_id
                FROM category_specification
                INNER JOIN
                specification
                ON category_specification.specification_id = specification.id
                GROUP BY category_specification.category_id
                ) as catSpecs
                ON catSpecs.category_id = category.id
                -- predicate_position_category """;

        String sqlPredicate = "";
        for (int i = 0; i < ids.length; ++i)
            sqlPredicate += " category_id = ? OR ";
        if (sqlPredicate.length() > 0)
            sqlPredicate = System.lineSeparator() + " WHERE " + sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR")) + System.lineSeparator();

        sql = sql.replace("predicater_position_category", sqlPredicate);

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            for (int i = 0; i < ids.length; ++i)
                ps.setInt(i + 1, ids[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Array sqlRowCats = rs.getArray("spec_id");
                if (sqlRowCats == null)
                    continue;
                Long[] RowCats = (Long[]) sqlRowCats.getArray();
                for (int i = 0; i < RowCats.length; ++i) {
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
