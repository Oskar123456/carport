package carport.persistence;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import carport.entities.Order;
import carport.entities.Product;
import carport.exceptions.DatabaseException;

/**
 * OrderMapper
 */
public class OrderMapper {

    public static List<Order> SelectAllOrders(ConnectionPool cp, int customerId, int employeeId, int statusCode)
            throws DatabaseException {
        List<Order> orders = new ArrayList<>();

        String sql = """
                SELECT * from
                (
                SELECT * FROM orders
                WHERE status_code_id = ?
                -- injection
                ) as o
                INNER JOIN order_status_code as osc
                ON o.status_code_id = osc.id """;

        String sqlPredicate = "";
        if (customerId > 0)
            sqlPredicate += " AND customer_id = ? ";
        if (employeeId > 0)
            sqlPredicate += " AND employee_id = ? ";
        sql = sql.replace("injection", System.lineSeparator() +
                sqlPredicate + System.lineSeparator());

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            ps.setInt(argNum++, statusCode);
            if (customerId > 0)
                ps.setInt(argNum++, customerId);
            if (employeeId > 0)
                ps.setInt(argNum++, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                orders.add(new Order(rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("status_code_id"),
                        rs.getString("description"),
                        rs.getInt("shipment_id"),
                        rs.getBigDecimal("price"),
                        rs.getString("time_of_order"),
                        rs.getString("note")));
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }

        return orders;
    }

    public static List<Integer[]> GetProductIdsWithQuants(ConnectionPool cp, int orderId) throws DatabaseException {
        List<Integer[]> productIds = new ArrayList<>();

        String sql = """
                SELECT o.id,
                ARRAY_AGG(p.id) product_ids,
                ARRAY_AGG(op.quantity) product_quantities
                from
                (SELECT *
                FROM orders
                WHERE id = ?
                ) as o
                INNER JOIN
                order_product as op
                ON o.id = op.order_id
                INNER JOIN
                product as p
                ON op.product_id = p.id
                GROUP BY o.id """;

        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            ps.setInt(argNum++, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Array pIdsA = rs.getArray("product_ids");
                Array pQuantsA = rs.getArray("product_quantities");
                if (pIdsA == null || pQuantsA == null)
                    continue;
                Long[] pIds = (Long[]) pIdsA.getArray();
                Long[] pQuants = (Long[]) pQuantsA.getArray();
                if (pIds.length != pQuants.length)
                    continue;
                for (int i = 0; i < pIds.length; ++i) {
                    Integer[] pIdWithQuant = new Integer[2];
                    pIdWithQuant[0] = Integer.valueOf(pIds[i].intValue());
                    pIdWithQuant[1] = Integer.valueOf(pQuants[i].intValue());
                    productIds.add(pIdWithQuant);
                }
            }
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }

        return productIds;
    }

    public static void DeleteOrder(ConnectionPool cp, int orderId) throws DatabaseException {
        String sqlCascade = "DELETE FROM order_product WHERE order_id = ?";
        String sql = "DELETE FROM orders WHERE id = ?";
        try {
            Connection c = cp.getConnection();
            PreparedStatement ps = c.prepareStatement(sqlCascade);

            ps.setInt(1, orderId);
            ps.executeUpdate();

            ps = c.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
    }

    public static void DeleteOrderProduct(ConnectionPool cp, int orderId, int productId) throws DatabaseException {
        String sql = "DELETE FROM order_product WHERE order_id = ? AND product_id = ?";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            ps.setInt(argNum++, orderId);
            ps.setInt(argNum++, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
    }

    public static void ApproveOrder(ConnectionPool cp, int orderId) throws DatabaseException {
        String sql = "UPDATE public.orders SET status_code_id=status_code_id - 1 WHERE id = ? AND status_code_id > 1";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            ps.setInt(argNum++, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
    }

    public static void ReplaceOrderProduct(ConnectionPool cp, // TODO: this needs a bit of thinking :)
            int orderId,
            int oldProductId,
            int newProductId) throws DatabaseException {
        String sql = "UPDATE order_product SET product_id = ? WHERE order_Id = ? AND product_id = ?";
        try (
                Connection c = cp.getConnection();
                PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;
            ps.setInt(argNum++, newProductId);
            ps.setInt(argNum++, orderId);
            ps.setInt(argNum++, oldProductId);
            ps.executeUpdate();
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
    }

    public static void InsertOrderProduct(ConnectionPool cp,
                                          int orderId, int productId,
                                          int quantity) throws DatabaseException {
        String sql = """
            INSERT INTO public.order_product(
                id, order_id, product_id, quantity)
                VALUES (DEFAULT, ?, ?, ?)
            """;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);) {
            int argNum = 1;

            ps.setInt(argNum++, orderId);
            ps.setInt(argNum++, productId);
            ps.setInt(argNum++, quantity);

            ps.executeUpdate();
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
    }


    public static void InsertOrder(ConnectionPool cp,
                                   Order order,
                                   int statusCode,
                                   List<Product> products) throws DatabaseException {
        String sql = """
            INSERT INTO public.orders(
            id, customer_id, employee_id, status_code_id, time_of_order, shipment_id, price, note)
            VALUES (DEFAULT, ?, 3, ?, ?, 1, ?, ?)
            """;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            int argNum = 1;

            ps.setInt(argNum++, order.CustomerId);
            ps.setInt(argNum++, statusCode);
            ps.setTimestamp(argNum++, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(argNum++, order.Price);
            ps.setString(argNum++, order.Note);

            int success = ps.executeUpdate();
            if (success > 0) {
                int newId = -1;
                ResultSet newIds = ps.getGeneratedKeys();
                if (newIds.next())
                    newId = (int) newIds.getLong(1);
                if (newId < 0)
                    return;
                for (Product p : products)
                    InsertOrderProduct(cp, newId, p.Id, 1);
            }
        } catch (SQLException e) {
            String thisMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            throw new DatabaseException(thisMethodName + "::error (" + e.getMessage() + ")");
        }
    }
}
