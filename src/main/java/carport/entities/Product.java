package carport.entities;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;

public class Product {
    static public int PlaceholderImageId;
    static public int PlaceholderImageIdMini;

    public int Id;
    public String Name;
    public String Description;
    public BigDecimal Price;
    public String[] Links;
    public int[] ImageIds;
    public int[] ImageIdsMini;
    public ProductSpecification[] Specs;
    public ProductCategory[] Categories;
    public ProductComponent[] Components;
    public int[] DocIds;

    public Product(int id, String name,
            String description, BigDecimal price,
            Array sqlLinks, Array sqlImageIds,
            Array sqlImageIdsMini, Array sqlSpecIds,
            Array sqlSpecNames, Array sqlSpecDetails,
            Array sqlSpecUnits, Array sqlCategories,
            Array sqlDocIds, Array sqlCompIds,
            Array sqlCompQuants) {

        this.Id = id;
        this.Name = name;
        this.Description = description;
        this.Price = price;
        // SQL array conversion hell
        if (sqlLinks != null) {
            try {
                Links = (String[]) sqlLinks.getArray();
            } catch (SQLException ignored) {
            }
        }
        if (sqlImageIds != null) {
            try {
                Long[] imageIdsObj = (Long[]) sqlImageIds.getArray();
                ImageIds = Arrays.stream(imageIdsObj).mapToInt(Long::intValue).toArray();
            } catch (SQLException ignored) {
            }
        }
        if (sqlImageIdsMini != null) {
            try {
                Long[] imageIdsMiniObj = (Long[]) sqlImageIdsMini.getArray();
                ImageIdsMini = Arrays.stream(imageIdsMiniObj).mapToInt(Long::intValue).toArray();
            } catch (SQLException ignored) {
            }
        }
        if (sqlSpecNames != null &&
                sqlSpecDetails != null &&
                sqlSpecUnits != null &&
            sqlSpecIds != null) {
            try {
                Long[] specIds = (Long[]) sqlSpecIds.getArray();
                String[] specNames = (String[]) sqlSpecNames.getArray();
                String[] specDetails = (String[]) sqlSpecDetails.getArray();
                String[] specUnits = (String[]) sqlSpecUnits.getArray();
                if (specNames.length == specIds.length &&
                    specDetails.length == specIds.length &&
                    specUnits.length == specIds.length) {
                    Specs = new ProductSpecification[specNames.length];
                    for (int i = 0; i < specIds.length; ++i) {
                        Specs[i] = new ProductSpecification(
                                specIds[i].intValue(),
                                specNames[i],
                                specDetails[i],
                                specUnits[i]);
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        if (sqlCategories != null) {
            try {
                String[] sCats = (String[]) sqlCategories.getArray();
                Categories = new ProductCategory[sCats.length];
                for (int i = 0; i < Categories.length; ++i) {
                    Categories[i] = new ProductCategory(-1,
                            sCats[i],
                            null);
                }
            } catch (SQLException ignored) {
            }
        }
        if (sqlCompIds != null &&
                sqlCompQuants != null) {
            try {
                Long[] compIdsObj = (Long[]) sqlCompIds.getArray();
                Long[] compQuantsObj = (Long[]) sqlCompQuants.getArray();
                if (compIdsObj.length == compQuantsObj.length) {
                    Components = new ProductComponent[compIdsObj.length];
                    for (int i = 0; i < Components.length; ++i) {
                        Components[i] = new ProductComponent(compIdsObj[i].intValue(),
                                compQuantsObj[i].intValue());
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        if (sqlDocIds != null) {
            try {
                Long[] docIdsObj = (Long[]) sqlDocIds.getArray();
                DocIds = new int[docIdsObj.length];
                for (int i = 0; i < DocIds.length; ++i) {
                    DocIds[i] = docIdsObj[i].intValue();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public int GetFirstImageId() {
        return (ImageIds != null) ? ImageIds[0] : PlaceholderImageId;
    }
    public int GetFirstImageIdDownscaled() {
        return (ImageIdsMini != null) ? ImageIdsMini[0] : PlaceholderImageIdMini;
    }
    /*
     * static
     */
    public static void SetPlaceholderImgs(int regular, int downscaled){
        PlaceholderImageId = regular;
        PlaceholderImageIdMini = downscaled;
    }

    public static int[] MapProductsToCommonSpecIds(List<Product> products) {
        if (products == null || products.size() < 1 || products.get(0).Specs.length < 1)
            return null;
        List<Integer> specIds = new ArrayList<>();
        for (int i = 0; i < products.get(0).Specs.length; ++i){
            specIds.add(products.get(0).Specs[i].Id());
        }
        for (Product p : products) {
            for (int i = 0; i < specIds.size(); ++i){
                boolean exists = false;
                if (p.Specs == null)
                    continue;
                for (int j = 0; j < p.Specs.length; ++j){
                    if (specIds.get(i) == p.Specs[j].Id())
                        exists = true;
                }
                if (!exists)
                    specIds.remove(i);
            }
        }
        if (specIds.size() < 1)
            return null;
        int[] specIdsArray = new int[specIds.size()];
        for (int i = 0; i < specIdsArray.length; ++i)
            specIdsArray[i] = specIds.get(i);
        return specIdsArray;
    }

    public static List<String> MapProductsToUniqueSpecDetails(List<Product> products, int specId){
        if (products == null || products.size() < 1 || specId < 0)
            return null;
        List<String> uniqueSpecDetails = new ArrayList<>();
        for (Product p : products){
            if (p.Specs == null)
                continue;
            for (ProductSpecification sp : p.Specs){
                if (sp.Id() == specId){
                    if (!uniqueSpecDetails.contains(sp.Details()))
                        uniqueSpecDetails.add(sp.Details());
                }
            }
        }
        return uniqueSpecDetails;
    }
}
