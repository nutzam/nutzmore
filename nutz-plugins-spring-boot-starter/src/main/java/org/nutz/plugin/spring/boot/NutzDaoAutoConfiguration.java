package org.nutz.plugin.spring.boot;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.plugin.spring.boot.runner.SpringDaoRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ Dao.class })
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, FileSqlManagerAutoConfiguration.class })
public class NutzDaoAutoConfiguration {

	public NutzDaoAutoConfiguration() {
	}

	@Autowired
	private SqlManager sqlManager;

	@Autowired
	DataSource dataSource;

	@Bean
	public Dao dao() {
		NutDao dao = new NutDao(dataSource, sqlManager);
		dao.setRunner(new SpringDaoRunner());
		return dao;
	}

}
