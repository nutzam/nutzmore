package org.nutz.plugin.spring.boot;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.SqlManager;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.DaoRunner;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.spring.SpringDaoRunner;
import org.nutz.plugin.spring.boot.config.SqlTemplateProperties;
import org.nutz.plugins.sqltpl.impl.beetl.BeetlSqlTpl;
import org.nutz.plugins.sqltpl.impl.freemarker.FreeMarkerSqlTpl;
import org.nutz.plugins.sqltpl.impl.jetbrick.JetbrickSqlTpl;
import org.nutz.plugins.sqltpl.impl.velocity.VelocitySqlTpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Dao.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, SqlManagerAutoConfiguration.class})
@EnableConfigurationProperties({SqlTemplateProperties.class, SqlTemplateProperties.class})
public class NutzDaoAutoConfiguration {

    @Autowired
    SqlTemplateProperties sqlTemplateProperties;

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

    @PostConstruct
    public void initSqlTemplate() {
        if (sqlTemplateProperties.isEnable()) {
            switch (sqlTemplateProperties.getType()) {
            case BEETL:
                Sqls.setSqlBorning(BeetlSqlTpl.class);
                break;
            case FREEMARKER:
                Sqls.setSqlBorning(FreeMarkerSqlTpl.class);
                break;
            case JETBRICK:
                Sqls.setSqlBorning(JetbrickSqlTpl.class);
                break;
            default:
                Sqls.setSqlBorning(VelocitySqlTpl.class);
                break;
            }
        }
    }

}
