package org.nutz.integration.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

/**
 * @author 科技㊣²º¹³
 * 2014年2月3日 下午4:48:45
 * http://www.rekoe.com
 * QQ:5382211
 * @author wendal<wendal1985@gmail.com>
 */
public class CaptchaFormAuthenticationFilter extends FormAuthenticationFilter implements ActionFilter {
	
	private String captchaParam = NutShiro.DEFAULT_CAPTCHA_PARAM;

	public String getCaptchaParam() {
		return captchaParam;
	}

	protected String getCaptcha(ServletRequest request) {
		return WebUtils.getCleanParam(request, getCaptchaParam());
	}

	public AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
		String username = getUsername(request);
		String password = getPassword(request);
		String captcha = getCaptcha(request);
		boolean rememberMe = isRememberMe(request);
		String host = getHost(request);
		return new CaptchaUsernamePasswordToken(username, password, rememberMe, host, captcha);
	}
	
	@Override
	protected boolean onLoginFailure(AuthenticationToken token,
			AuthenticationException e, ServletRequest req,
			ServletResponse resp) {
		if (NutShiro.isAjax(req)) {
			NutMap re = new NutMap().setv("ok", false).setv("msg", e.getMessage());
			NutShiro.rendAjaxResp(req, resp, re);
			return false;
		}
		return super.onLoginFailure(token, e, req, resp);
	}
	
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token,
			Subject subject, ServletRequest req, ServletResponse resp)
			throws Exception {
		subject.getSession().setAttribute(NutShiro.SessionKey, subject.getPrincipal());
		if (NutShiro.isAjax(req)) {
			NutShiro.rendAjaxResp(req, resp, new NutMap().setv("ok", true));
			return false;
		}
		return super.onLoginSuccess(token, subject, req, resp);
	}

	@Override
	public View match(ActionContext ac) {
		HttpServletRequest request = ac.getRequest();
		AuthenticationToken authenticationToken = createToken(request, ac.getResponse());
		request.setAttribute("loginToken", authenticationToken);
		return null;
	}
}