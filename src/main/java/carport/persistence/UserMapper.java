package carport.persistence;

import carport.entities.User;
import carport.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool cp) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = cp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("name"), rs.getString("surname"), password, rs.getString("role"), rs.getInt("address_id"));
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl: " + e.getMessage());
        }
    }

    public static void createuser(String name, String surname, int addressId, String email, String password, String role, ConnectionPool cp) throws DatabaseException {
        String sql = "INSERT INTO users (name, surname, address_id, email, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = cp.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, surname);
            ps.setInt(3, addressId);
            ps.setString(4, email);
            ps.setString(5, password);
            ps.setString(6, role);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger. Ingen rækker påvirket.");
            }
        } catch (SQLException e) {
            String message = "Der skete en fejl. Prøv igen.";
            if (e.getMessage().startsWith("ERROR: duplicate key value")) {
                message = "Denne email findes allerede. Vælg en anden.";
            }
            throw new DatabaseException(message, e.getMessage());
        }
    }
}