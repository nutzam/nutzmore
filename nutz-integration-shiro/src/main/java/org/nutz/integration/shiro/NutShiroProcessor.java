package org.nutz.integration.shiro;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.mvc.view.ServerRedirectView;

/**
 * 处理Shiro注解及异常, 如果使用ioc方式放入动作链,必须使用非单例模式.
 * @author wendal<wendal1985@gmail.com>
 *
 */
public class NutShiroProcessor extends AbstractProcessor {
    
    protected NutShiroMethodInterceptor interceptor;
    
    protected String uri;
    
    protected boolean match;
    
    protected boolean init;
    
    public NutShiroProcessor() {
        interceptor = new NutShiroMethodInterceptor();
        uri = NutShiro.DefaultLoginURL;
    }
    
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
    	if (init) // 禁止重复初始化,常见于ioc注入且使用了单例
    		throw new IllegalStateException("this Processor have bean inited!!");
    	super.init(config, ai);
    	match = NutShiro.match(ai.getMethod());
    	init = true;
    }

    public void process(ActionContext ac) throws Throwable {
    	if (match) {
    		try {
            	interceptor.assertAuthorized(new NutShiroInterceptor(ac));
            } catch (Throwable e) {
                if (NutShiro.isAjax(ac.getRequest())) {
                	NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), new NutMap().setv("ok", false).setv("msg", e.getMessage()));
                } else {
                	new ServerRedirectView(uri).render(ac.getRequest(), ac.getResponse(), null);
                }
                return;
            }
    	}
        doNext(ac);
    }
}
