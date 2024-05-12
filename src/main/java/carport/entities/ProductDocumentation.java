package carport.entities;

/**
 * ProductDocumentation
 */
public record ProductDocumentation(int Id,
                                   String Name,
                                   String Description,
                                   byte[] Data,
                                   int ProductId,
                                   String Type,
                                   String Format) {
}
