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
@Table("t_user_role")
@Comment("用户角色关系表")
public class UserRole extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column("u_id")
	@Comment("用户id")
	private long userId;

	@Column("r_id")
	@Comment("角色id")
	private long roleId;

	/**
	 * @return the roleId
	 */
	public long getRoleId() {
		return roleId;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

}
