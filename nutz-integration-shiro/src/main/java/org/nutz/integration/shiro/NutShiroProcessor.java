package org.nutz.integration.shiro;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.mvc.view.ServerRedirectView;

/**
 * 处理Shiro注解及异常, 如果使用ioc方式放入动作链,必须使用非单例模式.
 * @author wendal<wendal1985@gmail.com>
 *
 */
public class NutShiroProcessor extends AbstractProcessor {
    
    protected NutShiroMethodInterceptor interceptor;
    
    protected String loginUri;
    
    protected String noAuthUri;
    
    protected boolean match;
    
    protected boolean init;
    
    public NutShiroProcessor() {
        interceptor = new NutShiroMethodInterceptor();
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
            } catch (Exception e) {
                whenException(ac, e);
                return;
            }
    	}
        doNext(ac);
    }
    
    protected void whenException(ActionContext ac, Exception e) throws Throwable {
        Object val = ac.getRequest().getAttribute("shiro_auth_error");
        if (val != null && val instanceof View) {
            ((View)val).render(ac.getRequest(), ac.getResponse(), null);
            return;
        }
        if (e instanceof UnauthenticatedException) {
            whenUnauthenticated(ac, (UnauthenticatedException)e);
        } else if (e instanceof UnauthorizedException) {
            whenUnauthorized(ac, (UnauthorizedException)e);
        } else {
            whenOtherException(ac, e);
        }
    }
    
    protected void whenUnauthenticated(ActionContext ac, UnauthenticatedException e) throws Exception {
        if (NutShiro.isAjax(ac.getRequest())) {
            NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), ajaxFail("user.require.login", "user.require.login"));
        } else {
            new ServerRedirectView(loginUri()).render(ac.getRequest(), ac.getResponse(), null);
        }
    }
    
    protected NutMap ajaxFail(String msg, String type) {
        return new NutMap().setv("ok", false).setv("msg", msg).setv("type", type);
    }
    
    protected void whenUnauthorized(ActionContext ac, UnauthorizedException e) throws Exception {
        if (NutShiro.isAjax(ac.getRequest())) {
            NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), ajaxFail("user.require.auth", "user.require.auth"));
        } else {
            new ServerRedirectView(noAuthUri()).render(ac.getRequest(), ac.getResponse(), null);
        }
    }
    
    protected void whenOtherException(ActionContext ac, Exception e) throws Exception {
        if (NutShiro.isAjax(ac.getRequest())) {
            NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), ajaxFail("user.require.login", "user.require.login"));
        } else {
            new ServerRedirectView(loginUri()).render(ac.getRequest(), ac.getResponse(), null);
        }
    }
    
    protected String loginUri() {
        if (loginUri == null)
            return NutShiro.DefaultLoginURL;
        return loginUri;
    }
    
    protected String noAuthUri() {
        if (noAuthUri == null)
            return NutShiro.DefaultNoAuthURL == null ? NutShiro.DefaultLoginURL : NutShiro.DefaultNoAuthURL;
        return noAuthUri;
    }
}
