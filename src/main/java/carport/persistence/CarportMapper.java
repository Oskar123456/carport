package carport.persistence;

import carport.entities.Item;
import carport.entities.Product;
import carport.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.currentThread;

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
    /*
     * product
     */
    public static List<Product> ListProductsByName(ConnectionPool cp, boolean exact, String needle) throws DatabaseException
    {
        List<Product> results = new ArrayList<>();

        String sql = (exact) ? "SELECT * FROM product WHERE name = ?" :
                "SELECT * FROM product WHERE name ILIKE ?";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
        ) {
            if (exact)
                ps.setString(1, needle);
            else
                ps.setString(1, "%" + needle + "%");
            System.err.println(ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                results.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        (String[]) rs.getArray("links").getArray(),
                        rs.getBigDecimal("price")
                ));
            }
        }
        catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }
        return results;
    }
    /*
     * item
     */
    public static List<Item> ListItemsByName(ConnectionPool cp, boolean exact, String needle) throws DatabaseException
    {
        List<Item> results = new ArrayList<>();

        String sql = (exact) ? "SELECT * FROM product WHERE name = ?" :
                "SELECT * FROM product WHERE name ILIKE ?";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
        ) {
            if (exact)
                ps.setString(1, needle);
            else
                ps.setString(1, "%" + needle + "%");
            System.err.println(ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                results.add(new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("width"),
                        rs.getInt("length"),
                        rs.getInt("height"),
                        rs.getInt("weight"),
                        (String[]) rs.getArray("links").getArray(),
                        rs.getBigDecimal("price")
                ));
            }
        }
        catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }
        return results;
    }
}
