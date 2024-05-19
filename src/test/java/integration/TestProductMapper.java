package integration;

import carport.entities.Product;
import carport.entities.ProductCategory;
import carport.exceptions.DatabaseException;
import carport.persistence.CatAndSpecMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.dnd.DropTarget;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestProductMapper
{
    private static ConnectionPool c;

    @BeforeAll
    static void setup(){
        c = ConnectionPool.getInstance("", "", "", "");
        assertEquals(ConnectionPool.dbName, "testcarport");
        if (!ConnectionPool.dbName.equals("testcarport")){
            c = null;
            return;
        }

        ClassLoader classLoader = TestProductMapper.class.getClassLoader();
        File file = new File(classLoader.getResource("sql/carport-db-tables-create-script.sql").getFile());
        try {
            InputStream t = new FileInputStream(file);
            assertTrue(t.available() > 0);
            String createSql = new String(t.readAllBytes());
            /* DATABASE INIT */
            ProductMapper.Init();
            Statement stmt = c.getConnection().createStatement();
            stmt.execute(createSql);
            stmt.execute("INSERT INTO public.category(id, name) VALUES (DEFAULT, 'carport')");
            stmt.execute("INSERT INTO public.specification(id, name, unit) VALUES (DEFAULT, 'length', 'mm')");
            stmt.execute("INSERT INTO public.specification(id, name, unit) VALUES (DEFAULT, 'width', 'mm')");
            stmt.execute("INSERT INTO public.specification(id, name, unit) VALUES (DEFAULT, 'height', 'mm')");
        } catch (IOException | SQLException e) {
            fail("setup failed " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Insert and Delete")
    void TestProductInsertDelete() {
        String name = "test product";
        String description = "test product";
        BigDecimal price = new BigDecimal(1234);
        String[] links = {"test", "testing"};
        Long[] catIds = {Long.valueOf(1)};
        Long[] specIds = {Long.valueOf(1), Long.valueOf(2), Long.valueOf(3)};
        Product p = new Product(name, description, price, links, catIds, specIds);
        p.SpecDetails[0] = "12";
        p.SpecDetails[1] = "123";
        p.SpecDetails[2] = "1234";
        try {
            /* INSERT */
            int productId = ProductMapper.InsertProduct(c, false, p);
            Product pFetched = ProductMapper.SelectProductsById(c, productId).get(0);
            assertEquals(p.Name, pFetched.Name);
            assertEquals(p.Description, pFetched.Description);
            assertEquals(p.Price, pFetched.Price);
            /* DELETE */
            ProductMapper.DeleteProduct(c, productId);
            List<Product> results = ProductMapper.SelectProductsById(c, productId);
            assertEquals(results.size(), 0);
        } catch (DatabaseException | NullPointerException e) {
            fail("");
        }
    }

    @Test
    @DisplayName("Test Search")
    void TestSearch() {
        String name = "test product";
        String description = "test product";
        BigDecimal price = new BigDecimal(1234);
        String[] links = {"test", "testing"};
        Long[] catIds = {Long.valueOf(1)};
        Long[] specIds = {Long.valueOf(1), Long.valueOf(2), Long.valueOf(3)};
        Product p = new Product(name, description, price, links, catIds, specIds);
        p.SpecDetails[0] = "12";
        p.SpecDetails[1] = "123";
        p.SpecDetails[2] = "1234";
//        try {
//            /* SEARCH FOR THE PRODUCT IN DIFFERENT WAYS */
//        } catch (DatabaseException | NullPointerException e) {
//            fail("");
//        }
    }
}
