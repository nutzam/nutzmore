package org.nutz.plugin.spring.boot;

import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.integration.spring.SpringResourceLoaction;
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

  @Autowired
  private SqlManagerProperties sqlManagerProperties;

  @Bean
  @ConditionalOnMissingBean
  public SpringResourceLoaction springResourceLoaction() {
    SpringResourceLoaction springResourceLoaction = new SpringResourceLoaction();
    Scans.me().addResourceLocation(springResourceLoaction);
    return springResourceLoaction;
  }

  @Bean
  @ConditionalOnMissingBean
  public SqlManager sqlManager() {
    String[] paths = sqlManagerProperties.getPaths();
    if (paths == null) {
      paths = new String[]{"sqls"};
    }
    return sqlManagerProperties.getMode() == Mode.XML ? new XmlSqlManager(paths) : new FileSqlManager(paths);
  }

}
