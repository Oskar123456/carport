--MAP PRODUCT IDS TO ALL THEIR CATEGORIES-----------------------------------
SELECT DISTINCT category_ids

FROM product

LEFT JOIN
(
    SELECT product_category.product_id as pid,
           ARRAY_AGG(product_category.category_id) category_ids
    FROM product_category
    INNER JOIN
    category
    on category.id = product_category.category_id
    GROUP BY product_category.product_id
) as pCats
ON product.id = pCats.pid
