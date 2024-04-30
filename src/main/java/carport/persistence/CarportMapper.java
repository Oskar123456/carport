package carport.persistence;

import carport.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarportMapper
{
    public static void TestMapper(ConnectionPool cp) throws DatabaseException
    {
        String sql = "SELECT * FROM item";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                System.out.println(rs.getString( "name"));
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("fejl i test");
        }
    }
}
