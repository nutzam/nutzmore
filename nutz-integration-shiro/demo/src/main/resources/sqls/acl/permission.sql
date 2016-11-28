/*
list.direct.permission.by.user.id
*/
SELECT
	*
FROM
	t_permission p
LEFT JOIN t_user_permission up ON p.id = up.p_id
WHERE
	up.u_id = @userId
/*
list.indirect.permission.by.user.id
*/
SELECT
	p.*
FROM
	t_permission p
LEFT JOIN t_role_permission rp ON p.id = rp.p_id
LEFT JOIN t_user_role ur ON ur.r_id = rp.r_id
WHERE ur.u_id = @userId
/*
find.permissions.with.user.powered.info.by.user.id
*/
SELECT
	p.*, CASE sup.id IS NULL
WHEN 1 THEN
	''
ELSE
	'selected'
END AS has_permission
FROM
	t_permission p
LEFT JOIN (
	SELECT
		*
	FROM
		t_user_permission
	WHERE
		u_id = @id
) sup ON p.id = sup.p_id
/*
find.permissions.with.role.powered.info.by.role.id
*/
SELECT
	p.*, CASE srp.id IS NULL
WHEN 1 THEN
	''
ELSE
	'selected'
END AS hasr_permission
FROM
	t_permission p
LEFT JOIN (
	SELECT
		*
	FROM
		t_role_permission
	WHERE
		r_id =@id
) srp ON p.id = srp.p_id