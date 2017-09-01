package demo.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
@Table("t_role_permission")
@Comment("角色权限关系表")
public class RolePermission extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column("r_id")
	@Comment("角色id")
	private long roleId;

	@Column("p_id")
	@Comment("权限id")
	private long permissionId;

	/**
	 * @return the permissionId
	 */
	public long getPermissionId() {
		return permissionId;
	}

	/**
	 * @return the roleId
	 */
	public long getRoleId() {
		return roleId;
	}

	/**
	 * @param permissionId
	 *            the permissionId to set
	 */
	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

}
