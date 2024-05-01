package carport.entities;

import java.math.BigDecimal;

public record Product (
        int Id,
        String Name,
        String Description,
        String[] Links,
        BigDecimal Price
)
{
}
