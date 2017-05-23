package org.nutz.integration.activiti;

import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.scripting.Resolver;
import org.activiti.engine.impl.scripting.ResolverFactory;
import org.nutz.ioc.Ioc;

public class ActivitiNutIocBeansResolverFactory implements ResolverFactory, Resolver {

    protected Ioc ioc;

    public Resolver createResolver(VariableScope variableScope) {
        return this;
    }

    public boolean containsKey(Object key) {
        return ioc.has(String.valueOf(key));
    }

    public Object get(Object key) {
        return ioc.get(null, String.valueOf(key));
    }
}
