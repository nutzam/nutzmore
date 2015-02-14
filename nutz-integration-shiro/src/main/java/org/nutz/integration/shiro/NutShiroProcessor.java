package org.nutz.integration.shiro;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.processor.AbstractProcessor;

public class NutShiroProcessor extends AbstractProcessor {
    
    protected ShiroActionFilter filter;
    
    public NutShiroProcessor() {
        filter = new ShiroActionFilter();
    }
    
    public NutShiroProcessor(String view) {
        filter = new ShiroActionFilter(view);
    }

    public void process(ActionContext ac) throws Throwable {
        View view = filter.match(ac);
        if (view != null) {
            view.render(ac.getRequest(), ac.getResponse(), null);
        } else {
            doNext(ac);
        }
    }
}
