package carport.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import carport.exceptions.DatabaseException;
import carport.persistence.CatAndSpecMapper;
import carport.persistence.ConnectionPool;

/**
 * ProductCategory
 */
public class ProductCategory {
    public int Id;
    public String Name;
    public List<ProductSpecification> CommonSpecs;

    public ProductCategory(int id, String name, List<ProductSpecification> commonSpecIds) {
        Id = id;
        Name = name;
        CommonSpecs = commonSpecIds;
    }

    /*
     * static
     *
     */
    public static List<ProductSpecification> GetCommonSpecsFromCategoryIdList(
            ConnectionPool cp, List<Integer> catIds) {
        List<ProductSpecification> specs = new ArrayList<>();
        try {
            List<Long> specIds = new ArrayList<>();
            Map<Integer, Long[]> catsWithSpecIds = CatAndSpecMapper.SelectSpecificationsByCategory(cp, catIds);
            if (catsWithSpecIds == null)
                return specs;
            for (Entry<Integer, Long[]> e : catsWithSpecIds.entrySet()){
                for (Long l : e.getValue())
                    if (!specIds.contains(l))
                        specIds.add(l);
            }
            if (specIds.size() < 1)
                return specs;
            specs = CatAndSpecMapper.SelectSpecificationsById(cp, specIds);
        }
        catch (DatabaseException e){
            System.err.println(e.getMessage());
        }
        return specs;
    }
    public static List<Long> GetCommonSpecIdsFromCategoryIdList(
            ConnectionPool cp, List<Integer> catIds) {
        List<Long> specIds = new ArrayList<>();
        try {
            Map<Integer, Long[]> catsWithSpecIds = CatAndSpecMapper.SelectSpecificationsByCategory(cp, catIds);
            if (catsWithSpecIds == null)
                return specIds;
            for (Entry<Integer, Long[]> e : catsWithSpecIds.entrySet()){
                for (Long l : e.getValue())
                    if (!specIds.contains(l))
                        specIds.add(l);
            }
        }
        catch (DatabaseException e){
            System.err.println(e.getMessage());
        }
        return specIds;
    }
}
