package carport.entities;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;

public class Product {
    /*
     * Database representation
     * */
    int id;
    String name;
    String description;
    BigDecimal price;
    Array sqlLinks;
    Array sqlImageIds;
    Array sqlImageIdsMini;
    Array sqlSpecNames;
    Array sqlSpecDetails;
    Array sqlSpecUnits;
    Array sqlCategories;
    Array sqlDocIds;
    Array sqlCompIds;
    Array sqlCompQuants;
    /*
     * Internal representation
     * */
    String[] links;
    int[] imageIds;
    int[] imageIdsMini;
    ProductSpecification[] specs;
    ProductCategory[] categories;
    ProductComponent[] components;
    int[] docIds;
    /*
     * Public
     * */
    public Product(int id, String name,
                   String description, BigDecimal price,
                   Array sqlLinks, Array sqlImageIds,
                   Array sqlImageIdsMini, Array sqlSpecNames,
                   Array sqlSpecDetails, Array sqlSpecUnits,
                   Array sqlCategories, Array sqlDocIds,
                   Array sqlCompIds, Array sqlCompQuants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sqlLinks = sqlLinks;
        this.sqlImageIds = sqlImageIds;
        this.sqlImageIdsMini = sqlImageIdsMini;
        this.sqlSpecNames = sqlSpecNames;
        this.sqlSpecDetails = sqlSpecDetails;
        this.sqlSpecUnits = sqlSpecUnits;
        this.sqlCategories = sqlCategories;
        this.sqlDocIds = sqlDocIds;
        this.sqlCompIds = sqlCompIds;
        this.sqlCompQuants = sqlCompQuants;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public String[] getLinks() {
        if (links == null){
            if (sqlLinks != null) {
                try {
                    links = (String[]) sqlLinks.getArray();
                } catch (SQLException ignored) {}
            }
        }
        return links;
    }
    public int[] getImageIds() {
        if (imageIds == null){
            if (sqlImageIds != null) {
                try {
                    Long[] imageIdsObj = (Long[]) sqlImageIds.getArray();
                    imageIds = Arrays.stream(imageIdsObj).
                        mapToInt(Long::intValue).toArray();
                } catch (SQLException ignored) {}
            }
        }
        return imageIds;
    }
    public int[] getImageIdsMini() {
        if (imageIdsMini == null){
            if (sqlImageIdsMini != null) {
                try {
                    Long[] imageIdsMiniObj = (Long[]) sqlImageIdsMini.getArray();
                    imageIds = Arrays.stream(imageIdsMiniObj).
                        mapToInt(Long::intValue).toArray();
                } catch (SQLException e) {return null;}
            }
        }
        return imageIds;
    }
    public ProductSpecification[] getSpecs() {
        if (specs == null){
            if (sqlSpecNames == null ||
                sqlSpecDetails == null ||
                sqlSpecUnits == null)
                return null;
            try {
                String[] sNames = (String[]) sqlSpecNames.getArray();
                String[] sDetails = (String[]) sqlSpecDetails.getArray();
                String[] sUnits = (String[]) sqlSpecUnits.getArray();
                if (sNames.length != sDetails.length ||
                    sDetails.length != sUnits.length ||
                    sUnits.length != sNames.length)
                    return null;
                specs = new ProductSpecification[sNames.length];
                for (int i = 0; i < sNames.length; ++i){
                    specs[i] = new ProductSpecification(sNames[i],
                                                        sDetails[i],
                                                        sUnits[i]);
                }
            } catch (SQLException e) {
                return null;
            }
        }
        return specs;
    }
    public ProductCategory[] getCategories() {
        if (categories == null){
            if (sqlCategories != null){
                try {
                    String[] sCats = (String[]) sqlCategories.getArray();
                    categories = new ProductCategory[sCats.length];
                    for (int i = 0; i < categories.length; ++i){
                        categories[i] = new ProductCategory(-1,
                                                            sCats[i],
                                                            null);
                    }
                } catch (SQLException e) {
                    return null;
                }
            }
        }
        return categories;
    }
    public ProductComponent[] getComponents() {
        if (components == null){
            if (sqlCompIds == null ||
                sqlCompQuants == null)
                return null;
            try {
                Integer[] compIdsObj = (Integer[]) sqlCompIds.getArray();
                Integer[] compQuantsObj = (Integer[]) sqlCompQuants.getArray();
                if (compIdsObj.length != compQuantsObj.length)
                    return null;
                components = new ProductComponent[compIdsObj.length];
                for (int i = 0; i < components.length; ++i){
                    components[i] = new ProductComponent(compIdsObj[i].intValue(),
                                                         compQuantsObj[i].intValue());
                }
            } catch (SQLException e) {
                return null;
            }
        }
        return components;
    }
    public int[] getDocIds() {
        if (docIds == null){
            if (sqlDocIds == null)
                return null;
            try {
                Long[] docIdsObj = (Long[]) sqlDocIds.getArray();
                docIds = new int[docIdsObj.length];
                for (int i = 0; i < docIds.length; ++i){
                    docIds[i] = docIdsObj[i].intValue();
                }
            } catch (SQLException e) {
                return null;
            }
        }
        return docIds;
    }

}
