package org.nutz.plugins.bex5;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;

import com.justep.model.Model;

public class DaoUtils {
	private static Map<String, Dao> map = new HashMap<String, Dao>();
	private static Map<String, NutDao> x5map = new HashMap<String, NutDao>();

	/**
	 * 得到dao
	 * 
	 * @param dataSource
	 * @return
	 */
	public static Dao getDao(String dataSource) {
		if (dataSource == null) {
			throw new RuntimeException("dataSource不能为空！");
		}

		Dao dao = map.get(dataSource);
		if (null == dao) {
			synchronized (map) {
				dao = map.get(dataSource);
				if (null == dao) {
					Context context;
					try {
						context = new InitialContext();
						DataSource ds = (DataSource) context.lookup(dataSource);
						dao = new NutDao(ds);
						map.put(dataSource, dao);
					} catch (NamingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return dao;
	}

	/**
	 * 通过model得到dao
	 * 
	 * @param model
	 * @return
	 */
	public static Dao getDao(Model model) {
		String dataSource = ModelUtils2.getDataSourceName(model);
		return getDao(dataSource);
	}

	/**
	 * 得到x5dao
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public static Dao getDaoInTransaction(Model model) {
		if (model == null) {
			throw new RuntimeException("model不能为空！");
		}

		String dataSource = ModelUtils2.getDataSourceName(model);
		NutDao dao = x5map.get(dataSource);
		if (null == dao) {
			synchronized (x5map) {
				dao = x5map.get(dataSource);
				if (null == dao) {
					dao = new NutDao(new X5DataSource(dataSource));
					dao.setRunner(new X5DaoRunner());
					x5map.put(dataSource, dao);
				}
			}
		}
		return dao;
	}

}
