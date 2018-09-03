package org.nutz.plugin.spring.boot;

import javax.annotation.PostConstruct;

import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.integration.spring.SpringResourceLoaction;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugin.spring.boot.config.SqlManagerProperties;
import org.nutz.plugin.spring.boot.config.SqlManagerProperties.Mode;
import org.nutz.plugins.sqlmanager.xml.XmlSqlManager;
import org.nutz.resource.Scans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ApplicationObjectSupport;

@Configuration
@ConditionalOnClass(SqlManager.class)
@EnableConfigurationProperties(SqlManagerProperties.class)
public class SqlManagerAutoConfiguration extends ApplicationObjectSupport {

	Log log = Logs.get();

	@Autowired
	private SqlManagerProperties sqlManagerProperties;

	@Autowired
	private SpringResourceLoaction loaction;

	@PostConstruct
	public void init() {// 初始化一下nutz的扫描
		Scans.me().addResourceLocation(loaction);
	}

	@Bean
	public SpringResourceLoaction springResourceLoaction() {
		return new SpringResourceLoaction();
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlManager sqlManager() {
		String[] paths = sqlManagerProperties.getPaths();
		if (paths == null) {
			paths = new String[] { "sqls" };
		}
		return sqlManagerProperties.getMode() == Mode.XML ? new XmlSqlManager(paths) : new FileSqlManager(paths);
	}

}
