package org.nutz.integration.shiro;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.apache.shiro.web.util.WebUtils;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.*;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.mvc.view.ServerRedirectView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 处理Shiro注解及异常, 如果使用ioc方式放入动作链,必须使用非单例模式.
 *
 * @author wendal<wendal1985@gmail.com>
 * @author rekoe<koukou890@gmail.com>
 *
 *         如果是ajax请求的可以通过header 增加的状态属性做友好提示
 *
 *         $(document).ajaxComplete(function(event, request, settings) { var
 *         loginStatus = request.getResponseHeader("loginStatus"); if
 *         (loginStatus == "accessDenied") { $.message("warn", "登录超时，请重新登录");
 *         setTimeout(function() { location.reload(true); }, 2000); } else if
 *         (loginStatus == "unauthorized") { $.message("warn", "对不起，您无此操作权限！");
 *         }else{ $.message("warn", "系统错误"); } });
 *
 *         完整插件代码 插件开始//********************************* (function($) { var
 *         zIndex = 100; var $message=null; var messageTimer=null; $.message =
 *         function() { var message = {}; if ($.isPlainObject(arguments[0])) {
 *         message = arguments[0]; } else if (typeof arguments[0] === "string"
 *         && typeof arguments[1] === "string") { message.type = arguments[0];
 *         message.content = arguments[1]; } else { return false; } if
 *         (message.type == null || message.content == null) { return false; }
 *         if ($message == null) { $message = $('<div class="xxMessage"><div
 *         class
 *         ="messageContent message' + message.type + 'Icon"><\/div><\/div>');
 *         if (!window.XMLHttpRequest) { $message.append('<iframe
 *         class="messageIframe"><\/iframe>'); } $message.appendTo("body"); }
 *
 *         $message.children("div").removeClass(
 *         "messagewarnIcon messageerrorIcon messagesuccessIcon"
 *         ).addClass("message" + message.type + "Icon").html(message.content);
 *         $message.css({"margin-left": - parseInt($message.outerWidth() / 2),
 *         "z-index": zIndex ++}).show(); clearTimeout(messageTimer);
 *         messageTimer = setTimeout(function() { $message.hide(); }, 3000);
 *         return $message; };
 *
 *         $(document).ajaxComplete(function(event, request, settings) { var
 *         loginStatus = request.getResponseHeader("loginStatus"); if
 *         (loginStatus == "accessDenied") { $.message("warn", "登录超时，请重新登录");
 *         setTimeout(function() { location.reload(true); }, 2000); } else if
 *         (loginStatus == "unauthorized") { $.message("warn", "对不起，您无此操作权限！");
 *         }else { $.message("warn", "系统错误"); } }); })(jQuery);
 */
public class NutShiroProcessor extends AbstractProcessor {

	protected NutShiroMethodInterceptor interceptor;

	protected String loginUri;

	protected String noAuthUri;

	protected boolean match;

	protected boolean init;

	protected Class<? extends Annotation>[] annotations;

	public NutShiroProcessor(Collection<AuthorizingAnnotationMethodInterceptor> interceptors) {
		interceptor = new NutShiroMethodInterceptor(interceptors);
	}

	public NutShiroProcessor(Collection<AuthorizingAnnotationMethodInterceptor> interceptors, Class<? extends Annotation>... annotations) {
		interceptor = new NutShiroMethodInterceptor(interceptors);
		this.annotations = annotations;
	}

	public NutShiroProcessor() {
		interceptor = new NutShiroMethodInterceptor();
	}

	/**
	 * 始化
	 * @param config
	 * @param ai
	 * @throws Throwable
	 */
	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		// 禁止重复初始化,常见于ioc注入且使用了单例
		if (init)
		{
			throw new IllegalStateException("this Processor have bean inited!!");
		}
		super.init(config, ai);
		if (annotations == null || annotations.length == 0) {
			match = NutShiro.match(ai.getMethod());
		} else {
			match = NutShiro.match(ai.getMethod()) || hasAuthAnnotion(ai.getMethod(), annotations);
		}
		init = true;
	}

	/**
	 *判断是否有权限
	 * @param method
	 * @param annotations
	 * @return
	 */
	private boolean hasAuthAnnotion(Method method, Class<? extends Annotation>[] annotations) {
		for (Class<? extends Annotation> clazz : annotations) {
			if (method.getAnnotation(clazz) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * shiro处理
	 * @param ac
	 * @throws Throwable
	 */
	@Override
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

	/**
	 * 异常处理
	 * @param ac
	 * @param e
	 * @throws Throwable
	 */
	protected void whenException(ActionContext ac, Exception e) throws Throwable {
		Object val = ac.getRequest().getAttribute("shiro_auth_error");
		if (val != null && val instanceof View) {
			((View) val).render(ac.getRequest(), ac.getResponse(), null);
			return;
		}
		WebUtils.saveRequest(ac.getRequest());
		if (e instanceof UnauthenticatedException) {
			whenUnauthenticated(ac, (UnauthenticatedException) e);
		} else if (e instanceof UnauthorizedException) {
			whenUnauthorized(ac, (UnauthorizedException) e);
		} else {
			whenOtherException(ac, e);
		}
	}

	/**
	 * 无权限
	 * @param ac
	 * @param e
	 * @throws Exception
	 */
	protected void whenUnauthenticated(ActionContext ac, UnauthenticatedException e) throws Exception {
		if (NutShiro.isAjax(ac.getRequest())) {
			ac.getResponse().addHeader("loginStatus", "accessDenied");
			NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(),  NutShiro.getMessage(ac.getRequest(),NutShiro.DefaultUnauthenticatedAjax));
		} else {
			new ServerRedirectView(loginUri()).render(ac.getRequest(), ac.getResponse(), null);
		}
	}

	protected NutMap ajaxFail(String msg, String type) {
		return new NutMap().setv("ok", false).setv("msg", msg).setv("type", type);
	}

	/**
	 * 未登录
	 * @param ac
	 * @param e
	 * @throws Exception
	 */
	protected void whenUnauthorized(ActionContext ac, UnauthorizedException e) throws Exception {
		if (NutShiro.isAjax(ac.getRequest())) {
			ac.getResponse().addHeader("loginStatus", "unauthorized");
			NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), NutShiro.getMessage(ac.getRequest(),NutShiro.DefaultUnauthorizedAjax));
		} else {
			new ServerRedirectView(noAuthUri()).render(ac.getRequest(), ac.getResponse(), null);
		}
	}

	/**
	 * 其它异常
	 * @param ac
	 * @param e
	 * @throws Exception
	 */
	protected void whenOtherException(ActionContext ac, Exception e) throws Exception {
		if (NutShiro.isAjax(ac.getRequest())) {
			ac.getResponse().addHeader("loginStatus", "accessDenied");
			NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(),  NutShiro.getMessage(ac.getRequest(),NutShiro.DefaultOtherAjax));
		} else {
			new ServerRedirectView(loginUri()).render(ac.getRequest(), ac.getResponse(), null);
		}
	}

	protected String loginUri() {
		if (loginUri == null) {
			return NutShiro.DefaultLoginURL;
		}
		return loginUri;
	}

	protected String noAuthUri() {
		if (noAuthUri == null) {
			return NutShiro.DefaultNoAuthURL == null ? NutShiro.DefaultLoginURL : NutShiro.DefaultNoAuthURL;
		}
		return noAuthUri;
	}
}
