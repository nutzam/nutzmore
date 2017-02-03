package org.nutz.plugins.daomapper.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_permission")
public class Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	protected long id;
	@Name
	protected String name;
	@Column("al")
	protected String alias;
	@Column("dt")
	@ColDefine(type = ColType.VARCHAR, width = 500)
	private String description;

	@Column("permission_category_id")
	private String permissionCategoryId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPermissionCategoryId() {
		return permissionCategoryId;
	}

	public void setPermissionCategoryId(String permissionCategoryId) {
		this.permissionCategoryId = permissionCategoryId;
	}
}
