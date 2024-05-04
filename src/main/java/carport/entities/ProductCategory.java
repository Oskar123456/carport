package carport.entities;

/**
 * ProductCategory
 * TODO: need function that maps DB search to commonspecs for ALL products found, not just one page
 */
public record ProductCategory(int Id,
                              String Name,
                              String[] CommonSpecs) {}
