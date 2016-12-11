package org.nutz.integration.activiti;

import javax.sql.DataSource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Mirror;

public class ActivitiFactory {

    public static ProcessEngine build(DataSource ds, PropertiesProxy conf) {
        StandaloneProcessEngineConfiguration spec = new StandaloneProcessEngineConfiguration();
        spec.setDataSource(ds);
        Mirror<StandaloneProcessEngineConfiguration> mirror = Mirror.me(StandaloneProcessEngineConfiguration.class);
        for (String key : conf.keys()) {
            if (!key.startsWith("activiti."))
                continue;
            mirror.setValue(spec, key.substring("activiti.".length()), conf.get(key));
        }
        return spec.buildProcessEngine();
    }
}
