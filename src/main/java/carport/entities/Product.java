package carport.entities;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Product {
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
                sqlSpecUnits != null) {
            try {
                String[] specNames = (String[]) sqlSpecNames.getArray();
                String[] specDetails = (String[]) sqlSpecDetails.getArray();
                String[] specUnits = (String[]) sqlSpecUnits.getArray();
                if (specNames.length == specDetails.length &&
                        specDetails.length == specUnits.length &&
                        specUnits.length == specNames.length) {
                    Specs = new ProductSpecification[specNames.length];
                    for (int i = 0; i < specNames.length; ++i) {
                        Specs[i] = new ProductSpecification(0,
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

    /*
     * static
     */
    public static int[] MapProductsToCommonSpecIds(List<Product> products) {
        int[] specIds = null;

        for (Product p : products) {

        }

        return specIds;
    }

    public static int FilterProductListUsingSpecs(List<Product> products, int... specIds){
        return 0;
    }
}
