package org.nutz.integration.shiro;

import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.mvc.view.ServerRedirectView;

public class NutShiroProcessor extends AbstractProcessor {
    
    protected NutShiroMethodInterceptor interceptor;
    
    protected String uri;
    
    public NutShiroProcessor() {
        interceptor = new NutShiroMethodInterceptor();
        uri = NutShiro.DefaultLoginURL;
    }

    public void process(ActionContext ac) throws Throwable {
        try {
            interceptor.assertAuthorized(new NutShiroInterceptor(ac));
            doNext(ac);
        } catch (Throwable e) {
            Throwable e2 = Lang.unwrapThrow(e);
            if (e2 != null && e2.getClass().getName().startsWith("org.apache.shiro.authz")) {
            	if (NutShiro.isAjax(ac.getRequest())) {
            		NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), new NutMap().setv("ok", false).setv("msg", e2.getMessage()));
            		return;
            	}
                new ServerRedirectView(uri).render(ac.getRequest(), ac.getResponse(), null);
                return;
            }
            throw Lang.wrapThrow(e);
        }
    }
}
