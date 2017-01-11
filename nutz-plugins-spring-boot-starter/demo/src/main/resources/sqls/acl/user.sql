/*
find.users.by.role.name
*/
SELECT
	u.*
FROM
	t_user u
LEFT JOIN t_user_role ur ON u.id = ur.u_id
LEFT JOIN t_role r ON ur.r_id = r.id
WHERE
	r.r_name = @name