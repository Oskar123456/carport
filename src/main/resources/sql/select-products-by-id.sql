SELECT
    p.id, p.name, p.description, p.links,
    p.price, cp.category_ids, cs.specification_ids,
    cs.specification_details, cd.documentation_ids,
    ci.image_ids, ci.image_downscaled_ids,
    pcomp.component_ids, pcomp.component_quantities
FROM (SELECT * FROM product
        -- predicate_position_product
        ) as p
LEFT JOIN
(
    SELECT product_id,
        ARRAY_AGG(category_id) category_ids
    FROM product_category
    GROUP BY product_id
) as cp
ON p.id = cp.product_id

LEFT JOIN
(
    SELECT product_id,
        ARRAY_AGG(specification_id) specification_ids,
        ARRAY_AGG(details) specification_details
    FROM product_specification
        -- predicate_position_specification
    GROUP BY product_id
) as cs
ON p.id = cs.product_id

LEFT JOIN
(
    SELECT product_id,
        ARRAY_AGG(id) documentation_ids
    FROM product_documentation
    GROUP BY product_id
) as cd
ON p.id = cd.product_id

LEFT JOIN
(
    SELECT product_id,
        ARRAY_AGG(image_id) image_ids,
        ARRAY_AGG(image_downscaled_id) image_downscaled_ids
    FROM product_image
    GROUP BY product_id
) as ci
ON p.id = ci.product_id

LEFT JOIN
(
    SELECT product_id,
        ARRAY_AGG(component_id) component_ids,
        ARRAY_AGG(quantity) component_quantities
    FROM product_component
    GROUP BY product_id
) as pcomp
ON p.id = pcomp.product_id

GROUP BY p.id, p.name, p.description, p.links,
p.price, cp.category_ids, cs.specification_ids,
cs.specification_details, cd.documentation_ids,
ci.image_ids, ci.image_downscaled_ids,
pcomp.component_ids, pcomp.component_quantities

ORDER BY p.id
