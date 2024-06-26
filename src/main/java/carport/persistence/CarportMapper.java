package carport.persistence;

import carport.entities.ProductDocumentation;
import carport.entities.ProductImage;
import carport.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarportMapper
{


    /*
     * Call once
     * TODO: Reset table sequences
     */
    public static void Init()
    {
        ProductMapper.Init();
    }

    public static void CloseResources(PreparedStatement ps, ResultSet rs)
    {
        try {
            if (ps != null)
                ps.close();
        } catch (SQLException ignored) {
        }
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException ignored) {
        }
    }

    /*
     * Images
     */
    public static List<Integer> SelectProductImageIds(ConnectionPool cp,
                                                      boolean downscaled) throws DatabaseException
    {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM image WHERE downscaled = ? AND format <> 'svg+xml' AND format <> 'svg'";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, downscaled);
            System.err.println(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                ids.add(rs.getInt("id"));
            CloseResources(ps, rs);
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }
        return ids;
    }

    public static ProductImage SelectProductImageById(ConnectionPool cp,
                                                      int id) throws DatabaseException
    {
        ProductImage img = null;
        String sql = "SELECT * FROM image WHERE id = ?";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                img = new ProductImage(id,
                        rs.getString("name"),
                        rs.getString("source"),
                        rs.getBytes("data"),
                        rs.getString("format"),
                        rs.getBoolean("downscaled"));
            }
            CloseResources(ps, rs);
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }
        return img;
    }

    /*
     * Documentation
     */
    public static ProductDocumentation SelectProductDocumentationById(ConnectionPool cp,
                                                                      int id) throws DatabaseException
    {
        ProductDocumentation doc = null;

        String sql = "SELECT * FROM product_documentation WHERE id = ?";

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                doc = new ProductDocumentation(id, rs.getString("name"),
                        rs.getString("description"),
                        rs.getBytes("data"),
                        rs.getInt("product_id"),
                        rs.getString("type"),
                        rs.getString("format"));
            CloseResources(ps, rs);
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException("fejl ved søgning i databasen (" + thisMethodName + ")");
        }

        return doc;
    }
}
