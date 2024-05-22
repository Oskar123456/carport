package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import carport.entities.CustomCarport;

public class TestCustomCarport {
    private static CustomCarport cc;

    @BeforeEach
    void setup() {
        cc = new CustomCarport();
    }

    @Test
    @DisplayName("LOL")
    void TestValid() {
        boolean shouldNot = cc.Make(5000, 5000, 5000, true, 5000, 5000);
        boolean should = cc.Make(5000, 5000, 5000, true, 2000, 2000);
        boolean shouldNot1 = cc.Make(1000, 1000, 1000, false, 2000, 2000);
        boolean should1 = cc.Make(5000, 5000, 5000, false, 2000, 2000);
        assertEquals(false, shouldNot);
        assertEquals(true, should);
        assertEquals(false, shouldNot1);
        assertEquals(true, should1);
    }

    @Test
    @DisplayName("LOL")
    void TestN() {
        boolean should = cc.Make(5000, 5000, 5000, true, 2000, 2000);
        assertEquals(true, should);
        assertEquals(6, cc.GetNRemProd());
        assertEquals(24, cc.GetNStolpeProd());
        assertEquals(20, cc.GetNSpaerProd());
        assertEquals(128, cc.GetNSternProd());
        assertEquals(42, cc.GetNTagpladeProd());
    }
}
