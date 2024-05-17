select
    p.id, p.name, p.description, p.links,
    p.price, cp.category_ids, cs.specification_ids,
    cs.specification_details, cd.documentation_ids,
    ci.image_ids, ci.image_downscaled_ids,
    pcomp.component_ids, pcomp.component_quantities
from (select * from product
        -- predicate_position_product
        ) as p
left join
(
    select product_id,
        array_agg(category_id) category_ids
    from product_category
    group by product_id
) as cp
on p.id = cp.product_id

left join
(
    select product_id,
        array_agg(specification_id) specification_ids,
        array_agg(details) specification_details
    from product_specification
        -- predicate_position_specification
    group by product_id
) as cs
on p.id = cs.product_id

left join
(
    select product_id,
        array_agg(id) documentation_ids
    from product_documentation
    group by product_id
) as cd
on p.id = cd.product_id

left join
(
    select product_id,
        array_agg(image_id) image_ids,
        array_agg(image_downscaled_id) image_downscaled_ids
    from product_image
    group by product_id
) as ci
on p.id = ci.product_id

left join
(
    select product_id,
        array_agg(component_id) component_ids,
        array_agg(quantity) component_quantities
    from product_component
    group by product_id
) as pcomp
on p.id = pcomp.product_id

group by p.id, p.name, p.description, p.links,
p.price, cp.category_ids, cs.specification_ids,
cs.specification_details, cd.documentation_ids,
ci.image_ids, ci.image_downscaled_ids,
pcomp.component_ids, pcomp.component_quantities

order by p.id
