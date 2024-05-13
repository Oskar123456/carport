package main.java.carport.persistence;

/**
 * SpecMapper
 */
public class SpecMapper {

    public static List<ProductSpecification> SelectSpecsByProductIds(ConnectionPool cp,
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


}
