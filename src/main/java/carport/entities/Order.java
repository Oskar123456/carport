package carport.entities;

import java.math.BigDecimal;

/**
 * Order
 */
public class Order {
    int ProductId;
    int EmployeeId;
    int CustomerId;
    int StatusCode;
    int ShipmentId;
    BigDecimal Price;
    String TimeOfOrder;
    String Note;
}
