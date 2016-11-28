/*
list.role.by.user.id
*/
SELECT
	r.*
FROM
	t_role r
LEFT JOIN t_user_role ur ON r.id = ur.r_id
WHERE
	ur.u_id = @userId
/*
find.roles.with.user.powerd.info.by.user.id
*/
SELECT
	r.*, CASE sur.id IS NULL
WHEN 1 THEN
	''
ELSE
	'selected'
END AS has_role
FROM
	t_role r
LEFT JOIN (
	SELECT
		*
	FROM
		t_user_role
	WHERE
		u_id = @id
) sur ON r.id = sur.r_id