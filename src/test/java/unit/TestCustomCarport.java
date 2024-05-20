package unit;

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



public class TestCustomCarport
{
    @Test
    @DisplayName("LOL") 
    void TestTest() {
        assertEquals(1,1);
    }
}
