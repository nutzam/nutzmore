package org.nutz.plugins.oauth2.server;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Mirror;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.plugins.oauth2.server.entity.OAuthUser;

/**
 * @author 科技㊣²º¹³ <br/>
 *         2014年2月3日 下午4:48:45 <br/>
 *         http://www.rekoe.com <br/>
 *         QQ:5382211
 */
public class MvcSetup implements Setup {

	@Override
	public void init(NutConfig config) {
		Ioc ioc = config.getIoc();
		Dao dao = ioc.get(Dao.class);
		Daos.createTablesInPackage(dao, OAuthUser.class.getPackage().getName(), false);
		if (0 == dao.count(OAuthUser.class)) {
			FileSqlManager fm = new FileSqlManager("init_system_h2.sql");
			List<Sql> sqlList = fm.createCombo(fm.keys());
			dao.execute(sqlList.toArray(new Sql[sqlList.size()]));
		}
	}

	@Override
	public void destroy(NutConfig config) {
		try {
			Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
		} catch (Throwable e) {
		}
	}
}
