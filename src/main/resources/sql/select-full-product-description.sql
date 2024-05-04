--
-- fetches all relevant info about products -----------------------------------------------
--
SELECT product.id as id,
    product.name as name,
    product.description as description,
    product.price as price,
    product.links as links,
    product.images as image_ids,
    product.images_mini as image_ids_mini,
    spec_names as spec_names,
    spec_details as spec_details,
    spec_unit as spec_units,
    category_names as categories,
    doc_ids,
    comp_ids,
    comp_quantities
FROM product
-- fetch category info as array -----------------------------------------------
LEFT JOIN
(
    SELECT product_category.product_id,
        ARRAY_AGG(category.name) category_names
    FROM product_category
    INNER JOIN
    category
    ON category.id = product_category.category_id
    GROUP BY product_category.product_id
) AS pcat
ON pcat.product_id = product.id
-- fetch specification info as array -----------------------------------------------
LEFT JOIN
(
    SELECT product_specification.product_id,
        ARRAY_AGG(specification.name) spec_names,
        ARRAY_AGG(product_specification.details) spec_details,
        ARRAY_AGG(specification.unit) spec_unit
    FROM product_specification
    INNER JOIN
    specification
    ON specification.id = product_specification.specification_id
    GROUP BY product_specification.product_id
) AS pspec
ON pspec.product_id = product.id
-- fetch documentation ids info as array -----------------------------------------------
LEFT JOIN
(
    SELECT product_id,
        ARRAY_AGG(id) as doc_ids
    FROM product_documentation
    GROUP BY product_id
) as pdocs
ON pdocs.product_id = product.id

LEFT JOIN
(
    SELECT product_id,
        ARRAY_AGG(component_id) as comp_ids,
        ARRAY_AGG(quantity) as comp_quantities
    FROM product_component
    GROUP BY product_id
) as pcomps
ON pcomps.product_id = product.id
--
-- predicate_injection

--
-- orderby_injection
ORDER BY product.id ASC

LIMIT ?
OFFSET ?
