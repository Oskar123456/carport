package carport.persistence;

import carport.entities.ProductSpecification;
import carport.exceptions.DatabaseException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SpecMapper
 */
public class SpecMapper {

    public static List<List<ProductSpecification>> SelectSpecsByProductIds(ConnectionPool cp,
                                                                     int... ids) throws DatabaseException
    {
        List<List<ProductSpecification>> specList = new ArrayList<>();
        String sql = """
            SELECT product_id,
                    ARRAY_AGG(specification_id) specification_ids,
                    ARRAY_AGG(name) specification_names,
                    ARRAY_AGG(details) specification_details,
                    ARRAY_AGG(unit) specification_units
                    FROM (SELECT * FROM product_specification
                        -- predicate_position_specification
                        ) as ps
            INNER JOIN specification ON ps.specification_id = specification.id
            GROUP BY product_id
            ORDER BY product_id""";
        String sqlPredicate = "";
        if (ids != null) {
            for (int i = 0; i < ids.length; ++i)
                sqlPredicate += " product_id = ? OR ";
        }
        if (sqlPredicate.length() > 0)
            sqlPredicate = System.lineSeparator() + " WHERE "
                    + sqlPredicate.substring(0, sqlPredicate.lastIndexOf("OR")) + System.lineSeparator();
        sql = sql.replace("predicate_position_specification", sqlPredicate);

        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            if (ids != null)
                for (int i = 0; i < ids.length; ++i)
                    ps.setInt(argNum++, ids[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                List<ProductSpecification> singleSpecs = new ArrayList<>();
                Array sqlArraySpecIds = rs.getArray("specification_ids");
                Array sqlArraySpecNames = rs.getArray("specification_names");
                Array sqlArraySpecDetails = rs.getArray("specification_details");
                Array sqlArraySpecUnits = rs.getArray("specification_units");
                if (sqlArraySpecIds != null && sqlArraySpecNames != null &&
                    sqlArraySpecDetails != null && sqlArraySpecUnits != null){
                    Long[] arraySpecIds = (Long[]) sqlArraySpecIds.getArray();
                    String[] arraySpecNames = (String[]) sqlArraySpecNames.getArray();
                    String[] arraySpecDetails = (String[]) sqlArraySpecDetails.getArray();
                    String[] arraySpecUnits = (String[]) sqlArraySpecUnits.getArray();
                    for (int i = 0; i < arraySpecIds.length; ++i)
                        singleSpecs.add(new ProductSpecification(arraySpecIds[i].intValue(),
                                                                 arraySpecNames[i],
                                                                 arraySpecDetails[i],
                                                                 arraySpecUnits[i]));
                }
                specList.add(singleSpecs);
            }
                specList.add(null);
        } catch (SQLException e) {
            String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(funcName + "::" + e.getMessage());
        }
        return specList;
    }


}
