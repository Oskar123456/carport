package carport.entities;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;

public class Product {
    static private int PlaceholderImageId;
    static private int PlaceholderImageIdMini;

    public int Id;
    public String Name;
    public String Description;
    public BigDecimal Price;
    public String[] Links;
    public Long[] ImageIds;
    public Long[] ImageDownscaledIds;
    public Long[] CatIds;
    public Long[] SpecIds;
    public String[] SpecNames;
    public String[] SpecDetails;
    public String[] SpecUnits;
    public Long[] DocIds;
    public Long[] CompIds;
    public Long[] CompQuants;

    public Product(String name, String description,
                   BigDecimal price, String[] links,
                   Long[] catIds, Long[] specIds){
        this.Name = name;
        this.Description = description;
        this.Price = price;
        this.Links = links;
        this.CatIds = catIds;
        this.SpecIds = specIds;
        this.SpecNames = new String[specIds.length];
        this.SpecDetails = new String[specIds.length];
        this.SpecUnits = new String[specIds.length];
    }

    public Product(int id, String name,
            String description, BigDecimal price,
            String[] links, Long[] imageIds,
            Long[] imageDownscaledIds, Long[] catIds,
            Long[] specIds, String[] specDetails,
            Long[] docIds, Long[] compIds,
            Long[] compQuants) {
        this.Id = id;
        this.Name = name;
        this.Description = description;
        this.Price = price;
        this.Links = links;
        this.ImageIds = imageIds;
        this.ImageDownscaledIds = imageDownscaledIds;
        this.CatIds = catIds;
        this.SpecIds = specIds;
        this.SpecDetails = specDetails;
        this.DocIds = docIds;
        this.CompIds = compIds;
        this.CompQuants = compQuants;
    }

    public int GetFirstImageId() {
        return (ImageIds != null) ? ImageIds[0].intValue() : PlaceholderImageId;
    }

    public int GetFirstImageDownscaledId() {
        return (ImageDownscaledIds != null) ? ImageDownscaledIds[0].intValue() : PlaceholderImageIdMini;
    }

    public void AddImages(boolean downscaled,
                          int... ids){
        if (ids == null) {return;}
        if (downscaled){
            ImageIds = new Long[ids.length];
            for (int i = 0; i < ids.length; ++i)
                ImageIds[i] = Long.valueOf(ids[i]);
        }
        else{
            ImageDownscaledIds = new Long[ids.length];
            for (int i = 0; i < ids.length; ++i)
                ImageDownscaledIds[i] = Long.valueOf(ids[i]);
        }
    }

    /*
     * static
     */
    public static Product ImportFromDB(ResultSet rs) throws SQLException{
        Array sqlLinks = rs.getArray("links");
        Array sqlImageIds = rs.getArray("image_ids");
        Array sqlImageDownscaledIds = rs.getArray("image_downscaled_ids");
        Array sqlCatIds = rs.getArray("category_ids");
        Array sqlSpecIds = rs.getArray("specification_ids");
        Array sqlSpecDetails = rs.getArray("specification_details");
        Array sqlDocIds = rs.getArray("documentation_ids");
        Array sqlCompIds = rs.getArray("component_ids");
        Array sqlCompQuants = rs.getArray("component_quantities");

        return new Product(rs.getInt("id"),
                           rs.getString("name"),
                           rs.getString("description"),
                           rs.getBigDecimal("price"),
                           (sqlLinks == null) ? null : (String[]) sqlLinks.getArray(),
                           (sqlImageIds == null) ? null : (Long[]) sqlImageIds.getArray(),
                           (sqlImageDownscaledIds == null) ? null : (Long[]) sqlImageDownscaledIds.getArray(),
                           (sqlCatIds == null) ? null : (Long[]) sqlCatIds.getArray(),
                           (sqlSpecIds == null) ? null : (Long[]) sqlSpecIds.getArray(),
                           (sqlSpecDetails == null) ? null : (String[]) sqlSpecDetails.getArray(),
                           (sqlDocIds == null) ? null : (Long[]) sqlDocIds.getArray(),
                           (sqlCompIds == null) ? null : (Long[]) sqlCompIds.getArray(),
                           (sqlCompQuants == null) ? null : (Long[]) sqlCompQuants.getArray());
    }

    public static void SetPlaceholderImgs(int regular, int downscaled) {
        PlaceholderImageId = regular;
        PlaceholderImageIdMini = downscaled;
    }

    public static List<Long> MapProductsToCommonSpecIds(List<Product> products) {
        if (products == null || products.size() < 1 || products.get(0).SpecIds.length < 1)
            return null;
        List<Long> commonSpecIds = new ArrayList<>();
        for (int i = 0; i < products.get(0).SpecIds.length; ++i) {
            commonSpecIds.add(products.get(0).SpecIds[i]);
        }
        for (Product p : products) {
            for (int i = 0; i < commonSpecIds.size(); ++i) {
                boolean exists = false;
                if (p.SpecIds == null)
                    continue;
                for (int j = 0; j < p.SpecIds.length; ++j) {
                    if (commonSpecIds.get(i) == p.SpecIds[j])
                        exists = true;
                }
                if (!exists)
                    commonSpecIds.remove(i);
            }
        }
        if (commonSpecIds.size() < 1)
            return null;
        return commonSpecIds;
    }

    public static List<String> MapProductsToUniqueSpecDetails(List<Product> products, int specId) {
        if (products == null || products.size() < 1 || specId < 0)
            return null;
        List<String> uniqueSpecDetails = new ArrayList<>();
        for (Product p : products) {
            if (p.SpecIds == null || p.SpecDetails == null)
                continue;
            for (int i = 0; i < p.SpecIds.length; ++i) {
                if (p.SpecIds[i] == specId) {
                    if (!uniqueSpecDetails.contains(p.SpecDetails[i]))
                        uniqueSpecDetails.add(p.SpecDetails[i]);
                }
            }
        }
        return uniqueSpecDetails;
    }
}
