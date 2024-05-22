package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carport.entities.Product;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;

public class TestProductMapper {
    private static ConnectionPool c;

    private static final String USER = "postgres";
    private static final String PASSWORD = "bwQc)89P";
    private static final String URL = "jdbc:postgresql://104.248.251.153:5432/%s?currentSchema=public";
    private static final String DB = "testcarport";

    private static Product p;

    @BeforeAll
    static void setup() {
        c = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
        if (!ConnectionPool.GetDBName().equals("testcarport")) {
            c = null;
            return;
        }

        ClassLoader classLoader = TestProductMapper.class.getClassLoader();
        File file = new File(classLoader.getResource("sql/carport-db-tables-create-script.sql").getFile());
        try (InputStream t = new FileInputStream(file);) {
            assertTrue(t.available() > 0);
            String createSql = new String(t.readAllBytes());
            /* DATABASE INIT */
            CarportMapper.Init();
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

    @BeforeAll
    static void setProduct() {
        String name = "test product";
        String description = "test product";
        BigDecimal price = new BigDecimal(1234);
        String[] links = { "test", "testing" };
        Long[] catIds = { 1L };
        Long[] specIds = { 1L, 2L, 3L };
        p = new Product(name, description, price, links, catIds, specIds);
        p.SpecDetails[0] = "12";
        p.SpecDetails[1] = "123";
        p.SpecDetails[2] = "1234";
    }

    @Test
    @DisplayName("Test Correct DB")
    void TestCorrectDB() {
        String dbName = ConnectionPool.GetDBName();
        assertEquals(dbName, "testcarport");
    }

    @Test
    @DisplayName("Test Insert and Delete")
    void TestProductInsertDelete() {
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
        try {
            /* SETUP */
            ProductMapper.InsertProduct(c, false, p);

            List<String> nameN = toList("test product");
            List<String> nameNW = toList("taste produce");
            List<String> dscrN = toList("test", "product");
            List<String> dscrNW = toList("taste", "produce");
            List<Integer> catIds = toList(1);
            List<Integer> catIdsW = toList(2);
            List<Integer> specIds = toList(1, 2, 3);
            List<List<String>> specDs = toList(toList("12"));
            specDs.add(toList("123"));
            specDs.add(toList("1234"));
            List<List<String>> specDsW = toList(toList("123"));
            specDsW.add(toList("1234"));
            specDsW.add(toList("12"));
            /* NOW SEARCH FOR IT. <name>W are the 'wrong' searches */
            List<Integer> pSBN = ProductMapper.SearchProducts(c, 0, 100, nameN, null, null, false, null, null);
            List<Integer> pSBNW = ProductMapper.SearchProducts(c, 0, 100, nameNW, null, null, false, null, null);

            List<Integer> pSBD = ProductMapper.SearchProducts(c, 0, 100, null, dscrN, null, false, null, null);
            List<Integer> pSBDW = ProductMapper.SearchProducts(c, 0, 100, null, dscrNW, null, false, null, null);

            List<Integer> pSBC = ProductMapper.SearchProducts(c, 0, 100, null, null, catIds, false, null, null);
            List<Integer> pSBCW = ProductMapper.SearchProducts(c, 0, 100, null, null, catIdsW, false, null, null);

            List<Integer> pSBS = ProductMapper.SearchProducts(c, 0, 100, null, null, null, true, specIds, specDs);
            List<Integer> pSBSW = ProductMapper.SearchProducts(c, 0, 100, null, null, null, true, specIds, specDsW);
            /* VERIFY */
            assertEquals(1, pSBN.size());
            assertEquals(0, pSBNW.size());
            assertEquals(1, pSBD.size());
            assertEquals(0, pSBDW.size());
            assertEquals(1, pSBC.size());
            assertEquals(0, pSBCW.size());
            assertEquals(1, pSBS.size());
            assertEquals(0, pSBSW.size());

        } catch (DatabaseException | NullPointerException e) {
            fail("");
        }

    }

    /* HELPER */
    private static List<String> toList(String... ss) {
        return new ArrayList<>(Arrays.asList(ss));
    }

    private static List<Integer> toList(int... is) {
        List<Integer> ret = new ArrayList<>();
        for (int i : is)
            ret.add(i);
        return ret;
    }

    private static <T> List<T> toList(T t) {
        List<T> ret = new ArrayList<>();
        ret.add(t);
        return ret;
    }

    private static <T> List<List<T>> toList(List<T> lt) {
        List<List<T>> ret = new ArrayList<>();
        ret.add(lt);
        return ret;
    }
}
