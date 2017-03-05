package org.nutz.plugin.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nutz.dao.runtime")
public class NutzDaoRuntimeProperties {

	/**
	 * 自动建表
	 */
	private boolean create = true;

	/**
	 * 自动变更
	 */
	private boolean migration = true;

	/**
	 * 实体包名
	 */
	private String[] basepackage;

	/**
	 * 强制创建<删表重建>
	 */
	private boolean foceCreate = false;

	/**
	 * 是否增加列
	 */
	private boolean addColumn = true;

	/**
	 * 是否删除列
	 */
	private boolean deleteColumn = true;

	/**
	 * 检查索引
	 */
	private boolean checkIndex = true;

	/**
	 * @return the addColumn
	 */
	public boolean isAddColumn() {
		return addColumn;
	}

	/**
	 * @param addColumn
	 *            the addColumn to set
	 */
	public void setAddColumn(boolean addColumn) {
		this.addColumn = addColumn;
	}

	/**
	 * @return the deleteColumn
	 */
	public boolean isDeleteColumn() {
		return deleteColumn;
	}

	/**
	 * @param deleteColumn
	 *            the deleteColumn to set
	 */
	public void setDeleteColumn(boolean deleteColumn) {
		this.deleteColumn = deleteColumn;
	}

	/**
	 * @return the checkIndex
	 */
	public boolean isCheckIndex() {
		return checkIndex;
	}

	/**
	 * @param checkIndex
	 *            the checkIndex to set
	 */
	public void setCheckIndex(boolean checkIndex) {
		this.checkIndex = checkIndex;
	}

	/**
	 * @return the foceCreate
	 */
	public boolean isFoceCreate() {
		return foceCreate;
	}

	/**
	 * @param foceCreate
	 *            the foceCreate to set
	 */
	public void setFoceCreate(boolean foceCreate) {
		this.foceCreate = foceCreate;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public boolean isMigration() {
		return migration;
	}

	public void setMigration(boolean migration) {
		this.migration = migration;
	}

	public String[] getBasepackage() {
		return basepackage;
	}

	public void setBasepackage(String[] basepackage) {
		this.basepackage = basepackage;
	}

}
