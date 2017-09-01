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
@Table("t_user_permission")
@Comment("用户权限关系表")
public class UserPermission extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 用户id
	 */
	@Column("u_id")
	@Comment("用户id")
	private long userId;
	/**
	 * 权限id
	 */
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
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param permissionId
	 *            the permissionId to set
	 */
	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

}