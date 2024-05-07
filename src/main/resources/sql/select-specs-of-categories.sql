--MAP CATEGORIES TO THEIR SPECIFICATIONS---------------------------------------
SELECT category.id as category_id,
	category.name as category_name,
	catSpecs.spec_name,
	catSpecs.spec_unit,
	catSpecs.spec_id
FROM category
LEFT JOIN
(
	SELECT category_specification.category_id as category_id,
		ARRAY_AGG(specification.name) spec_name,
		ARRAY_AGG(specification.unit) spec_unit,
		ARRAY_AGG(specification.id) spec_id
	FROM category_specification
	INNER JOIN
	specification
	ON category_specification.specification_id = specification.id
	GROUP BY category_specification.category_id
) as catSpecs
ON catSpecs.category_id = category.id
--
-- predicate_injection

--
-- orderby_injection
