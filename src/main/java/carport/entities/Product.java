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
import carport.persistence.CatAndSpecMapper;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;

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
    private List<ProductSpecification> fullSpecs;
    private List<ProductCategory> cats;
    public Long[] DocIds;
    public Long[] CompIds;
    public Long[] CompQuants;

    public Product() {
    }

    public Product(String name, String description,
            BigDecimal price, String[] links,
            Long[] catIds, Long[] specIds) {
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

    public void AddComps(int id, int quant) {
        if (CompIds == null || CompIds.length < 1 ||
                CompQuants == null || CompQuants.length < 1) {
            CompIds = new Long[1];
            CompQuants = new Long[1];
        } else {
            Long[] newCompIds = new Long[CompIds.length + 1];
            Long[] newCompQants = new Long[CompIds.length + 1];
            for (int i = 0; i < CompIds.length; ++i) {
                newCompIds[i] = CompIds[i];
                newCompQants[i] = CompQuants[i];
            }
            CompIds = newCompIds;
            CompQuants = newCompQants;
        }
        CompIds[CompIds.length - 1] = Long.valueOf(id);
        CompQuants[CompIds.length - 1] = Long.valueOf(quant);
    }

    public List<ProductSpecification> GetFullSpecs(ConnectionPool cp) throws DatabaseException {
        if (fullSpecs == null && SpecIds != null) {
            int[] specIds = new int[SpecIds.length];
            for (int i = 0; i < specIds.length; ++i)
                specIds[i] = SpecIds[i].intValue();
            fullSpecs = CatAndSpecMapper.SelectSpecificationsById(cp, specIds);
            for (int i = 0; i < specIds.length; ++i)
                for (int j = 0; j < fullSpecs.size(); ++j)
                    if (specIds[i] == fullSpecs.get(j).Id)
                        fullSpecs.get(j).Details = SpecDetails[i];
        }
        return fullSpecs;
    }

    public ProductSpecification GetFullSpec(String name) {
        if (fullSpecs == null)
            return null;
        for (ProductSpecification ps : fullSpecs)
            if (ps.Name.toLowerCase().equals(name.toLowerCase()))
                return ps;
        return null;
    }

    public ProductSpecification GetFullSpec(int id) {
        if (fullSpecs == null)
            return null;
        for (ProductSpecification ps : fullSpecs)
            if (ps.Id == id)
                return ps;
        return null;
    }

    public ProductSpecification GetSpecLength() {
        return GetFullSpec("length");
    }

    public ProductSpecification GetSpecWidth() {
        return GetFullSpec("width");
    }

    public ProductSpecification GetSpecHeight() {
        return GetFullSpec("height");
    }

    public int GetFirstImageId() {
        return (ImageIds != null) ? ImageIds[0].intValue() : PlaceholderImageId;
    }

    public int GetFirstImageDownscaledId() {
        return (ImageDownscaledIds != null) ? ImageDownscaledIds[0].intValue() : PlaceholderImageIdMini;
    }

    public void AddImages(boolean downscaled,
            int... ids) {
        if (ids == null) {
            return;
        }
        if (downscaled) {
            ImageIds = new Long[ids.length];
            for (int i = 0; i < ids.length; ++i)
                ImageIds[i] = Long.valueOf(ids[i]);
        } else {
            ImageDownscaledIds = new Long[ids.length];
            for (int i = 0; i < ids.length; ++i)
                ImageDownscaledIds[i] = Long.valueOf(ids[i]);
        }
    }

    public List<ProductCategory> GetCategories(ConnectionPool cp) throws DatabaseException {
        if (cats == null)
            cats = CatAndSpecMapper.SelectCategoriesById(cp,
                    CatAndSpecMapper.GetProductCategories(cp, Id));
        return cats;
    }

    public boolean IsType(String cat) {
        if (fullSpecs != null)
            for (ProductCategory c : cats) {
                if (cat.toLowerCase().equals(c.Name.toLowerCase()))
                    return true;
            }
        return false;
    }

    public boolean IsCarport(){
        return IsType("carport");
    }

    /*
     * static
     */
    public static Product ImportFromDB(ResultSet rs) throws SQLException {
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

    public static int[] GetPlaceHolderImageId() {
        return new int[] { PlaceholderImageId, PlaceholderImageIdMini };
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

    public static void LoadFullSpecs(ConnectionPool cp, List<Product> products) throws DatabaseException {
        for (Product p : products)
            p.GetFullSpecs(cp);
    }

    public BigDecimal GetSumOfComponentPrices(ConnectionPool cp) throws DatabaseException {
        if (CompIds == null || CompIds.length < 1)
            return new BigDecimal(0);
        List<Integer> compidsints = new ArrayList<>();
        for (Long l : CompIds) {
            compidsints.add(l.intValue());
        }
        List<Product> components = ProductMapper.SelectProductsById(cp, compidsints);
        BigDecimal retval = new BigDecimal(0);
        for (int i = 0; i < components.size(); ++i) {
            for (int j = 0; j < CompIds.length; ++j) {
                BigDecimal compPrice = components.get(i).Price;
                if (CompIds[j] == components.get(i).Id) {
                    BigDecimal fullPrice = compPrice.multiply(new BigDecimal(CompQuants[j].intValue()));
                    retval = retval.add(fullPrice);
                }
            }
        }
        return retval;
    }

    public static BigDecimal GetSumOfProductPrices(List<Product> products) {
        BigDecimal total = new BigDecimal(0);
        if (products == null)
            return total;
        for (Product p : products)
            total = total.add(p.Price);
        return total;
    }
}
