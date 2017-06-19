package org.nutz.plugins.bex5;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.justep.model.Config;
import com.justep.model.Model;
import com.justep.model.ModelUtils;
import com.justep.system.data.DatabaseProduct;

/**
 * @author ecoolper 2017/06/19
 */
public class ModelUtils2 {
	/**
	 * 根据model获得dataSource
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public static DataSource getDataSource(Model model) throws Exception  {
		Config cfg = model.getUseableConfig(Config.DATA_SOURCE_NAME);
		Context context = new InitialContext();
		return (DataSource) context.lookup(cfg.getValue());
	}
	
	public static String getDataSourceName(Model model) {
		Config cfg = model.getUseableConfig(Config.DATA_SOURCE_NAME);
		return cfg.getValue();
	}
	
	/**
	 * 根据dataSource名称获得dataSource
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	public static DataSource getDataSource(String dataSource) throws Exception  {
		Context context = new InitialContext();
		return (DataSource) context.lookup("java:comp/env/" +dataSource);
	}
	
	public DatabaseProduct getDatabaseProduct(Model model) throws Exception {
		Connection con =ModelUtils.getConnection(model);
		return DatabaseProduct.getProduct(con);
	}
}
