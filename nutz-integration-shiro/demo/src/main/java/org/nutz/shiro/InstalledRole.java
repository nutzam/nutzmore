package org.nutz.shiro;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
public enum InstalledRole {
	/**
	 * 平台管理员
	 */
	SU("admin", "后台管理员");
	private String name;

	private String description;

	/**
	 * @param name
	 * @param description
	 */
	private InstalledRole(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
