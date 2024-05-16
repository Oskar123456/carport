package carport.persistence;

import carport.entities.Product;
import carport.entities.ProductDocumentation;
import carport.entities.ProductImage;
import carport.exceptions.DatabaseException;
import carport.tools.ProductImageFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductMapper {
    static private String SQL_SELECT_PRODUCTS_BY_ID;
    static private final int PRODUCT_IMG_NOTFOUND_PLACEHOLDER = 46;
    static private final int PRODUCT_IMG_NOTFOUND_PLACEHOLDER_DOWNSCALED = 47;

    public static void Init() {
        InputStream sqlSelProdsStream = CarportMapper.class.getResourceAsStream("/sql/select-products-by-id.sql");
        try {
            assert sqlSelProdsStream != null;
            SQL_SELECT_PRODUCTS_BY_ID = new String(sqlSelProdsStream.readAllBytes());
        } catch (IOException e) {
            System.err.println("CarportMapper failed to load sql statements from file");
        }
        Product.SetPlaceholderImgs(PRODUCT_IMG_NOTFOUND_PLACEHOLDER, PRODUCT_IMG_NOTFOUND_PLACEHOLDER_DOWNSCALED);
    }
    public static int InsertProduct(ConnectionPool cp,
                                    boolean internal,
                                    Product prod) throws DatabaseException
    {
        // TODO: ensure prod is correct? although database fkeys will cause this function to fail I suppose
        String sql = """
                INSERT INTO public.product(
                id, name, description, links, price, internal)
                VALUES (DEFAULT, ?, ?, ?, ?, ?) """;

        int generatedKey = -1;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;
            ps.setString(argNum++, prod.Name);
            ps.setString(argNum++, prod.Description);
            ps.setArray(argNum++, c.createArrayOf("varchar", prod.Links));
            ps.setBigDecimal(argNum++, prod.Price);
            ps.setBoolean(argNum++, internal);
            System.err.println(ps.toString());
            int success = ps.executeUpdate();
            if (success > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next())
                    generatedKey = (int) generatedKeys.getLong(1);
                if (generatedKey < 0)
                    return generatedKey;
                for (int i = 0; i < prod.ImageIds.length; ++i)
                    InsertProductToImageLink(cp, generatedKey,
                            prod.ImageIds[i].intValue(),
                            prod.ImageDownscaledIds[i].intValue());
                for (int i = 0; i < prod.CatIds.length; ++i)
                    InsertProductToCategoryLink(cp, generatedKey, prod.CatIds[i].intValue());
                for (int i = 0; i < prod.SpecIds.length; ++i)
                    InsertProductToSpecificationLink(cp, generatedKey, prod.SpecIds[i].intValue(), prod.SpecDetails[i]);
                if (prod.CompIds != null)
                    for (int i = 0; i < prod.CompIds.length; ++i)
                        InsertProductToComponentLink(cp, generatedKey, prod.CompIds[i].intValue(),
                                prod.CompQuants[i].intValue());
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return generatedKey;
    }

    public static int InsertProductToImageLink(ConnectionPool cp,
                                               int prodId,
                                               int imageId,
                                               int imageDownscaledId) throws DatabaseException {
        String sql = """
                INSERT INTO public.product_image(
                id, product_id, image_id, image_downscaled_id)
                VALUES (DEFAULT, ?, ?, ?); """;

        int generatedKey = -1;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;
            ps.setInt(argNum++, prodId);
            ps.setInt(argNum++, imageId);
            ps.setInt(argNum++, imageDownscaledId);
            // System.err.println(ps.toString());
            int success = ps.executeUpdate();
            if (success > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next())
                    generatedKey = (int) generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return generatedKey;
    }

    public static int InsertProductToCategoryLink(ConnectionPool cp,
                                                  int prodId,
                                                  int categoryId) throws DatabaseException {
        String sql = """
                INSERT INTO product_category(
                id, product_id, category_id)
                VALUES (DEFAULT, ?, ?) """;

        int generatedKey = -1;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;
            ps.setInt(argNum++, prodId);
            ps.setInt(argNum++, categoryId);
            // System.err.println(ps.toString());
            int success = ps.executeUpdate();
            if (success > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next())
                    generatedKey = (int) generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return generatedKey;
    }

    public static int InsertProductToSpecificationLink(ConnectionPool cp,
                                                       int prodId,
                                                       int specificationId,
                                                       String details) throws DatabaseException {
        String sql = """
                INSERT INTO public.product_specification(
                id, product_id, specification_id, details)
                VALUES (DEFAULT, ?, ?, ?); """;

        int generatedKey = -1;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;
            ps.setInt(argNum++, prodId);
            ps.setInt(argNum++, specificationId);
            ps.setString(argNum++, details);
            System.err.println(ps.toString());
            int success = ps.executeUpdate();
            if (success > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next())
                    generatedKey = (int) generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return generatedKey;
    }

    public static int InsertProductToDocumentationLink(ConnectionPool cp,
                                                       ProductDocumentation doc) throws DatabaseException {
        String sql = """
                INSERT INTO public.product_documentation(
                id, name, description, data, product_id, type, format)
                VALUES (DEFAULT, ?, ?, ?, ?, ?, ?); """;

        int generatedKey = -1;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;
            ps.setString(argNum++, doc.Name());
            ps.setString(argNum++, doc.Description());
            ps.setBytes(argNum++, doc.Data());
            ps.setInt(argNum++, doc.ProductId());
            ps.setString(argNum++, doc.Type());
            ps.setString(argNum++, doc.Format());

            System.err.println(ps.toString());
            int success = ps.executeUpdate();
            if (success > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next())
                    generatedKey = (int) generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return generatedKey;
    }

    public static int InsertProductToComponentLink(ConnectionPool cp,
                                                   int prodId,
                                                   int componentId,
                                                   int quantity) throws DatabaseException {
        String sql = """
                INSERT INTO public.product_component(
                id, product_id, component_id, quantity)
                VALUES (DEFAULT, ?, ?, ?) """;

        int generatedKey = -1;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;
            ps.setInt(argNum++, prodId);
            ps.setInt(argNum++, componentId);
            ps.setInt(argNum++, quantity);
            int success = ps.executeUpdate();
            if (success > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next())
                    generatedKey = (int) generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return generatedKey;
    }

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
                WHERE p.internal = 'false'
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
            for (int i = 0; i < specIds.size(); ++i) {
                if (specDetails.get(i) != null)
                    for (int j = 0; j < specDetails.get(i).size(); ++j)
                        sqlSpecPredicate += " (specification_id = ? AND details = ?) OR ";
            }
        } else
            sql = sql.replaceAll("should_filter(.|\s)*should_filter", System.lineSeparator());
        if (sqlPredicate.length() > 0) {
            sqlPredicate = sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR"));
            sqlPredicate = System.lineSeparator() + " AND ( " + sqlPredicate + " ) " + System.lineSeparator();
        }
        if (sqlSpecPredicate.length() > 0) {
            sqlSpecPredicate = sqlSpecPredicate.substring(0, sqlSpecPredicate.lastIndexOf("OR"));
            sqlSpecPredicate = System.lineSeparator() + " WHERE " + sqlSpecPredicate + System.lineSeparator();
        }

        sql = sql.replace("predicate_position_product", sqlPredicate);
        sql = sql.replace("predicate_position_specification", sqlSpecPredicate);

        /* DEBUG PRINTING */
        // String thisMethodName =
        // Thread.currentThread().getStackTrace()[1].getMethodName();
        // System.err.printf("%s::%n%s%n%n%n%n%n%n",
        // thisMethodName,
        // sql);
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
            if (pageNum >= 0){
                ps.setInt(argNum++, pageSize);
                ps.setInt(argNum++, pageNum);
            } else {
                ps.setInt(argNum++, 10000);
                ps.setInt(argNum++, 0);
            }
            // System.err.println(ps.toString());
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
                                                   List<Integer> ids) throws DatabaseException {
        if (ids == null || ids.size() < 1)
            return null;
        int[] idsArray = new int[ids.size()];
        for (int i = 0; i < idsArray.length; ++i)
            idsArray[i] = ids.get(i);
        return SelectProductsById(cp, idsArray);
    }

    public static List<Product> SelectProductsById(ConnectionPool cp,
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
            // System.err.println("select products by id : \n" + ps.toString());
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

        String sql = "INSERT INTO image (id, data, source, name, format, downscaled) VALUES (DEFAULT, ?, ?, ?, ?, ?)";

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
            ps.setBoolean(5, img.Downscaled());
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

    public static int InsertCarportCustomBase(ConnectionPool cp,
                                              int length, int width, int height) throws DatabaseException
    {
        String sql = """
                INSERT INTO product(
                	id, name, description, links, price, internal)
                	VALUES (DEFAULT, ?, ?, ?, ?, true) 
                """;


        int generatedKey = -1;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;

            ps.setString(argNum++, "customcarport");
            ps.setString(argNum++, "customcarport");
            ps.setArray(argNum++, null);
            ps.setBigDecimal(argNum++, new BigDecimal(0));

            // System.err.println(ps.toString());
            int success = ps.executeUpdate();
            if (success > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next())
                    generatedKey = (int) generatedKeys.getLong(1);
                if (generatedKey < 0)
                    return generatedKey;
                // LINK CARPORT TO SPECS / CATS / PLACEHOLDER IMAGE
                ProductMapper.InsertProductToCategoryLink(cp, generatedKey, 1);
                ProductMapper.InsertProductToSpecificationLink(cp, generatedKey, 1, String.valueOf(length));
                ProductMapper.InsertProductToSpecificationLink(cp, generatedKey, 2, String.valueOf(width));
                ProductMapper.InsertProductToSpecificationLink(cp, generatedKey, 3, String.valueOf(height));
                ProductMapper.InsertProductToImageLink(cp, generatedKey,
                        PRODUCT_IMG_NOTFOUND_PLACEHOLDER, PRODUCT_IMG_NOTFOUND_PLACEHOLDER_DOWNSCALED);
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return generatedKey;
    }
}
