package org.nutz.plugins.jqgrid.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.plugins.jqgrid.entity.JQGridPage;
import org.nutz.plugins.jqgrid.entity.JQGridResult;

/**
 * JQGrid通用查询
 * @author 邓华锋 http://dhf.ink
 *
 */
public interface JQGridService {
	/**
	 * JQGrid通用查询 传class 返回class集合
	 * @param jqGridPage
	 * @param dao
	 * @param cnd
	 * @param defaultOrderField
	 * @param clazz
	 * @return
	 */
	public JQGridResult query(JQGridPage jqGridPage, Dao dao, Cnd cnd, String defaultOrderField, Class<?> clazz);
	
	/**
	 * JQGrid通用查询 传表名  返回Record集合
	 * @param jqGridPage
	 * @param tableName
	 * @param dao
	 * @param cnd
	 * @param defaultOrderField
	 * @return
	 */
	public JQGridResult query(JQGridPage jqGridPage, String tableName,Dao dao, Cnd cnd, String defaultOrderField);
}
