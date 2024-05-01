package carport.entities;

import java.math.BigDecimal;

public record Item (
        int Id,
        String Name,
        String Description,
        int width,
        int length,
        int height,
        int weight,
        String[] Links,
        BigDecimal Price
)
{
}
