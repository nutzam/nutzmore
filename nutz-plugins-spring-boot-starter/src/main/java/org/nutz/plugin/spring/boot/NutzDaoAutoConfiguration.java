package org.nutz.plugin.spring.boot;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.DaoRunner;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.spring.SpringDaoRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Dao.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, SqlManagerAutoConfiguration.class})
public class NutzDaoAutoConfiguration {

    @Bean
    public Dao dao(DataSource dataSource, SqlManager sqlManager, DaoRunner daoRunner) {
        NutDao dao = new NutDao(dataSource, sqlManager);
        dao.setRunner(daoRunner);
        return dao;
    }

    @Bean
    @ConditionalOnMissingBean(DaoRunner.class)
    public DaoRunner daoRunner() {
        return new SpringDaoRunner();
    }

}
