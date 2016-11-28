package org.nutz.shiro.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
@Table("t_role")
@Comment("角色表")
public class Role extends Entity {

	@Column("r_name")
	@Comment("角色名称")
	@Name
	private String name;

	@Column("r_desc")
	@Comment("描述")
	private String description;

	@Column("r_installed")
	@Comment("是否内置角色标识")
	private boolean installed = true;

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	public void setName(String name) {
		this.name = name;
	}

}
