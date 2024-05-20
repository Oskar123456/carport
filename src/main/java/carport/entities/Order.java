package carport.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import carport.exceptions.DatabaseException;
import carport.persistence.ConnectionPool;
import carport.persistence.OrderMapper;
import carport.persistence.ProductMapper;

/**
 * Order
 */
public class Order {
    public int Id;
    public int EmployeeId;
    public int CustomerId;
    public int StatusCode;
    public String StatusName;
    public String TimeOfOrder;
    public int ShipmentId;
    public BigDecimal Price;
    public String Note;

    public List<Integer> ProductIds;
    public List<Integer> ProductQuants;
    public List<Product> ProductList;

    public Order(int id, int employeeId,
            int customerId, int statusCode,
            String statusName, int shipmentId,
            BigDecimal price,
            String timeOfOrder, String note) {
        Id = id;
        EmployeeId = employeeId;
        CustomerId = customerId;
        StatusCode = statusCode;
        StatusName = statusName;
        ShipmentId = shipmentId;
        Price = price;
        TimeOfOrder = timeOfOrder;
        Note = note;
    }

    public void LoadProductIdsAndQuants(ConnectionPool cp) throws DatabaseException {
        List<Integer[]> prodIdsAndQuants = OrderMapper.GetProductIdsWithQuants(cp, this.Id);
        if (prodIdsAndQuants == null)
            return;
        ProductIds = new ArrayList<>();
        ProductQuants = new ArrayList<>();
        for (Integer[] iq : prodIdsAndQuants) {
            ProductIds.add(iq[0]);
            ProductQuants.add(iq[1]);
        }
    }

    public void LoadProductList(ConnectionPool cp) throws DatabaseException {
        ProductList = new ArrayList<>();
        for (Integer i : ProductIds) {
            Product p = ProductMapper.SelectProductsById(cp, i).get(0);
            p.GetCategories(cp);
            p.GetFullSpecs(cp);
            ProductList.add(p);
        }
    }

    public void Load(ConnectionPool cp) throws DatabaseException {
        LoadProductIdsAndQuants(cp);
        LoadProductList(cp);
    }

    public BigDecimal GetBasePrice() {
        return Product.GetSumOfProductPrices(ProductList);
    }

    public static void LoadList(ConnectionPool cp, List<Order> orders) throws DatabaseException {
        for (Order o : orders)
            o.Load(cp);
    }

    @Override
    public String toString() {
        return "Order [Id=" + Id + ", EmployeeId=" + EmployeeId + ", CustomerId=" + CustomerId + ", StatusCode="
                + StatusCode + ", StatusName=" + StatusName + ", TimeOfOrder=" + TimeOfOrder + ", ShipmentId="
                + ShipmentId + ", Price=" + Price + ", Note=" + Note + ", ProductIds=" + ProductIds.toString()
                + ", ProductQuants="
                + ProductQuants.toString() + ", ProductList=" + ProductList.toString() + "]";
    }

}
