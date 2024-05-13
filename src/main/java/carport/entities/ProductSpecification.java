package carport.entities;

/**
 * ProductSpecification
 * TODO: change to class and add function that maps unit to string to avoid 'text' showing up in front end.
 */
public record ProductSpecification(int Id,
                                   String Name,
                                   String Details,
                                   String Unit) {
}
