/*
load.top.branchs
*/
SELECT
	id,
	sub_name AS `name`,
	sub_parent_id AS `pId`,
	TRUE AS `open`,
	TRUE AS `isParent`
FROM
	t_branch
WHERE
	sub_parent_id  = 0
/*
load.branchs.by.id
*/
SELECT
	t.*, sub_name AS `name`,
	sub_parent_id AS `pId`,
	TRUE AS `open`,
	(
		SELECT
			COUNT(id)
		FROM
			t_branch i
		WHERE
			i.sub_parent_id = t.id
	) > 0 AS `isParent`
FROM
	t_branch t
WHERE
	sub_parent_id = @id
	
/*
list.top.branchs.by.page
*/
SELECT
	*, (
		SELECT
			COUNT(id)
		FROM
			t_branch i
		WHERE
			i.sub_parent_id = t.id
	) > 0 AS has_sub
FROM
	t_branch t
WHERE
	sub_parent_id = 0
LIMIT @pageStart,
 @pageSize