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
@Table("t_permission")
@Comment("权限表")
public class Permission extends Entity {

	@Column("p_name")
	@Name
	@Comment("权限名称")
	private String name;

	@Column("p_url")
	@Comment("权限对应url")
	private String url;

	@Column("p_icon")
	@Comment("菜单icon")
	private String icon;

	@Column("p_desc")
	@Comment("描述")
	private String description;

	@Column("installed")
	@Comment("内置标识")
	private boolean installed;

	@Column("p_is_menu")
	@Comment("是否菜单标识")
	private boolean menu;

	@Column("p_need")
	@Comment("前置权限ID")
	private String needPermission;

	@Column("p_hilight_key")
	@Comment("菜单高亮关键字,如果是菜单的时候此字段建议填写")
	private String hilightKey;

	@Column("p_level")
	@Comment("权限级别,0为顶级菜单儿")
	private int level = 0;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	public String getHilightKey() {
		return hilightKey;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public String getNeedPermission() {
		return needPermission;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the installed
	 */
	public boolean isInstalled() {
		return installed;
	}

	public boolean isMenu() {
		return menu;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setHilightKey(String hilightKey) {
		this.hilightKey = hilightKey;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @param installed
	 *            the installed to set
	 */
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public void setMenu(boolean menu) {
		this.menu = menu;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setNeedPermission(String needPermission) {
		this.needPermission = needPermission;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}