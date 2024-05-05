package carport.entities;

import java.util.List;

/**
 * ProductCategory
 */
public class ProductCategory {
    public int Id;
    public String Name;
    public List<ProductSpecification> CommonSpecIds;

    public ProductCategory(int id, String name, List<ProductSpecification> commonSpecIds) {
        Id = id;
        Name = name;
        CommonSpecIds = commonSpecIds;
    }
}
