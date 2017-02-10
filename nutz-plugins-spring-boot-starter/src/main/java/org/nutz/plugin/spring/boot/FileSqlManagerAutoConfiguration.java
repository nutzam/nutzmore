package org.nutz.plugin.spring.boot;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.plugin.spring.boot.config.SqlManagerProperties;
import org.nutz.resource.Scans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;

@Configuration
@ConditionalOnClass(SqlManager.class)
@EnableConfigurationProperties(SqlManagerProperties.class)
public class FileSqlManagerAutoConfiguration implements ServletContextAware {

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext sc) {
		this.servletContext = sc;
	}

	public FileSqlManagerAutoConfiguration() {
	}

	@Autowired
	private SqlManagerProperties sqlManagerProperties;

	@PostConstruct
	public void init() {// 初始化一下nutz的扫描
	    if (servletContext != null)
	        Scans.me().init(servletContext);
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlManager sqlManager() {
		String[] paths = sqlManagerProperties.getPaths();
		if (paths == null) {
			paths = new String[] { "sqls" };
		}
		return new FileSqlManager(paths);
	}

}
