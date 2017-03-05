package org.nutz.plugin.spring.boot.listener;

import javax.annotation.PostConstruct;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.plugin.spring.boot.NutzDaoAutoConfiguration;
import org.nutz.plugin.spring.boot.config.NutzDaoRuntimeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean({ Dao.class })
@EnableConfigurationProperties(NutzDaoRuntimeProperties.class)
@AutoConfigureAfter({ NutzDaoAutoConfiguration.class })
public class NutzDataBaseIniter {

	@Autowired
	private NutzDaoRuntimeProperties nutzDaoRuntimeProperties;

	@Autowired
	private Dao dao;

	@PostConstruct
	public void create() {
		if (nutzDaoRuntimeProperties.isCreate()) {
			Lang.each(nutzDaoRuntimeProperties.getBasepackage(), new Each<String>() {

				@Override
				public void invoke(int arg0, String pkg, int arg2) throws ExitLoop, ContinueLoop, LoopException {
					Daos.createTablesInPackage(dao, pkg, nutzDaoRuntimeProperties.isFoceCreate());
				}
			});
		}
		if (nutzDaoRuntimeProperties.isMigration()) {
			Lang.each(nutzDaoRuntimeProperties.getBasepackage(), new Each<String>() {

				@Override
				public void invoke(int arg0, String pkg, int arg2) throws ExitLoop, ContinueLoop, LoopException {
					Daos.migration(dao, pkg, nutzDaoRuntimeProperties.isAddColumn(), nutzDaoRuntimeProperties.isDeleteColumn(), nutzDaoRuntimeProperties.isCheckIndex());
				}
			});
		}
	}

}
