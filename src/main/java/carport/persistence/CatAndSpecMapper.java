package carport.persistence;

import carport.entities.ProductCategory;
import carport.entities.ProductSpecification;
import carport.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatAndSpecMapper
{
    /*
     * Category and Specification
     */
    public static List<ProductCategory> SelectAllCategories(ConnectionPool cp) throws DatabaseException
    {
        List<ProductCategory> cats = new ArrayList<>() ;

        String sql = "SELECT * from category";

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                cats.add(new ProductCategory(rs.getInt("id"), rs.getString("name"), null));
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }

        return cats;
    }

    public static ProductCategory SelectCategoryById(ConnectionPool cp,
                                                     int id) throws DatabaseException{
        ProductCategory cat = null;

        String sql = "SELECT * from category WHERE id = ? ";

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                cat = new ProductCategory(rs.getInt("id"), rs.getString("name"), null);
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }

        return cat;
    }

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
        // String thisMethodName =
        // Thread.currentThread().getStackTrace()[1].getMethodName();
        // System.err.printf("%s::%n\tsqlCatPredicate : %s%n\tFULL:%n%s%n%n",
        // thisMethodName,
        // sqlCatPredicate,
        // sql);
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

    public static Map<Integer, Long[]> SelectSpecificationsByCategory(ConnectionPool cp,
                                                                      List<Integer> catIds) throws DatabaseException {
        if (catIds == null || catIds.size() < 1)
            return null;
        int[] catIdsArray = new int[catIds.size()];
        for (int i = 0; i < catIds.size(); ++i)
            catIdsArray[i] = catIds.get(i);
        return SelectSpecificationsByCategory(cp, catIdsArray);
    }

    public static Map<Integer, Long[]> SelectSpecificationsByCategory(ConnectionPool cp,
                                                                      int... catIds) throws DatabaseException {
        if (catIds == null)
            return null;
        Map<Integer, Long[]> catIdsWithSpecIds = new HashMap<>();

        String sql = """
                SELECT c.id, ARRAY_AGG(specification_id) specification_ids
                FROM (SELECT * FROM category
                    -- predicate_position_category
                    ) as c
                INNER JOIN category_specification as cs ON c.id = cs.category_id
                GROUP BY c.id
                ORDER BY c.id""";
        String sqlPredicate = "";
        for (int i = 0; i < catIds.length; ++i)
            sqlPredicate += " id = ? OR ";
        if (sqlPredicate.length() > 0)
            sqlPredicate = System.lineSeparator() + " WHERE " +
                    sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR")) + System.lineSeparator();
        sql = sql.replace("predicate_position_category", sqlPredicate);

        try (Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            for (int i = 0; i < catIds.length; ++i)
                ps.setInt(argNum++, catIds[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Array sqlSpecIds = rs.getArray("specification_ids");
                if (sqlSpecIds != null) {
                    Long[] specIds = (Long[]) sqlSpecIds.getArray();
                    catIdsWithSpecIds.put(rs.getInt("id"), specIds);
                }
            }
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return catIdsWithSpecIds;
    }

    /*
     * Selects unique category ids based on provided array of product ids.
     * Will select only common denominators if desired.
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
            sqlPredicate = System.lineSeparator() + " WHERE "
                    + sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR")) + System.lineSeparator();

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
            sqlPredicate = System.lineSeparator() + " WHERE "
                    + sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR")) + System.lineSeparator();

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
}
