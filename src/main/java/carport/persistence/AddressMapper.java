package carport.persistence;

import carport.entities.Address;
import carport.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper {

    public static int saveAddress(Address address, ConnectionPool cp) throws DatabaseException {
        String sql = "INSERT INTO addresses (street, number, floor, info, zip) VALUES (?, ?, ?, ?, ?) RETURNING id;";
        try (Connection conn = cp.getConnection(); //Jeg sikre at der er forbindelse til databasen fra poolen
             PreparedStatement ps = conn.prepareStatement(sql)) { // Bare til egen indfo jeg bruger et PreparedStatement for at undgå SQL injektion, den skulle være ret sikker.
            ps.setString(1, address.getStreet());
            ps.setInt(2, address.getNumber());
            ps.setInt(3, address.getFloor());
            ps.setString(4, address.getInfo());
            ps.setInt(5, address.getZip());

            ResultSet rs = ps.executeQuery();     // Her bliver der udført en forspørgelse, som så retunere resultatsættet
            if (rs.next()) {
                return rs.getInt("id"); // Hvis ja, Returnerer ID for den nyoprettede adresse
            } else {
                throw new DatabaseException("Fejl ved indsættelse af adresse i databasen."); //ellers kaster den en ny databaseexeption med fejlbeskden "fejl ved...."
            }
        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl ved indsættelse af adresse: " + e.getMessage());
        }
    }
}