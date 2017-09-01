package org.nutz.integration.activiti;

import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.CommandInterceptor;

public class NutProcessEngineConfiguration extends StandaloneProcessEngineConfiguration {

    protected CommandInterceptor createTransactionInterceptor() {
        return new NutTransactionInterceptor();
    }
}
