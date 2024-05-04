package carport.entities;

public record ProductImage(int Id,
        String Name,
        String Source,
        byte[] Data,
        String Format) {
}
