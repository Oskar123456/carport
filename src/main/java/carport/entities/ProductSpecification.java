package carport.entities;

/**
 * ProductSpecification
 * TODO: change to class and add function that maps unit to string to avoid 'text' showing up in front end.
 */
public class ProductSpecification {
    public int Id;
    public String Name;
    public String Details;
    public String Unit;
    public ProductSpecification(int id, String name, String details, String unit) {
        Id = id;
        Name = name;
        Details = details;
        Unit = unit;
    }
}
