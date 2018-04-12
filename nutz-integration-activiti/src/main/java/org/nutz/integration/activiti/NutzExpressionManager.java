package org.nutz.integration.activiti;

import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.el.ExpressionManager;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;

public class NutzExpressionManager extends ExpressionManager {

    protected NutIocElResolver nutIocElResolver;
    
    protected ELResolver createElResolver(VariableScope variableScope) {
        
        CompositeELResolver elResolver =  (CompositeELResolver) super.createElResolver(variableScope);
        elResolver.add(nutIocElResolver);
        return elResolver;
    }
}
