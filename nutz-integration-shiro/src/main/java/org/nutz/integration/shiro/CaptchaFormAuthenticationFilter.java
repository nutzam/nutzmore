package org.nutz.integration.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

/**
 * @author 科技㊣²º¹³ 2014年2月3日 下午4:48:45 http://www.rekoe.com QQ:5382211
 * @author wendal<wendal1985@gmail.com>
 */
@Deprecated
public class CaptchaFormAuthenticationFilter extends FormAuthenticationFilter implements ActionFilter {
    
    protected String JsonParams_KEY = "CaptchaFormAuthenticationFilter_JsonParams";

    private String captchaParam = NutShiro.DEFAULT_CAPTCHA_PARAM;

    public String getCaptchaParam() {
        return captchaParam;
    }
    
    public String getCleanParams(ServletRequest request, String name) {
        HttpServletRequest req = (HttpServletRequest)request;
        NutMap jsonParams = (NutMap) req.getAttribute(JsonParams_KEY);
        if (jsonParams != null && jsonParams.containsKey(name)) {
            return StringUtils.clean(jsonParams.getString(name));
        }
        return WebUtils.getCleanParam(request, name);
    }

    protected String getCaptcha(ServletRequest request) {
        return getCleanParams(request, getCaptchaParam());
    }
    
    @Override
    protected String getUsername(ServletRequest request) {
        return getCleanParams(request, getUsernameParam());
    }
    
    @Override
    protected String getPassword(ServletRequest request) {
        return getCleanParams(request, getPasswordParam());
    }
    
    @Override
    protected boolean isRememberMe(ServletRequest request) {
        String value = getCleanParams(request, getRememberMeParam());
        return value != null &&
                (value.equalsIgnoreCase("true") ||
                        value.equalsIgnoreCase("t") ||
                        value.equalsIgnoreCase("1") ||
                        value.equalsIgnoreCase("enabled") ||
                        value.equalsIgnoreCase("y") ||
                        value.equalsIgnoreCase("yes") ||
                        value.equalsIgnoreCase("on"));
    }

    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        String captcha = getCaptcha(request);
        if (Strings.isBlank(captcha)) {
            return onCaptchaError(request, response);
        }
        Subject subject = getSubject(request, response);
        Session session = subject.getSession(false);
        if (session == null) {
            return onCaptchaError(request, response);
        }
        HttpServletRequest req = (HttpServletRequest)request;
        if (req.getHeader("Content-Type") != null &&req.getHeader("Content-Type").contains("json")) {
            NutMap jsonParams = Json.fromJson(NutMap.class, req.getReader());
            req.setAttribute(JsonParams_KEY, jsonParams);
        }
        Object _expected = session.getAttribute(captchaParam);
        if (_expected == null)
            _expected = session.getAttribute("nutz_captcha");
        if (_expected == null)
            return onCaptchaError(request, response);
        if (!captcha.equalsIgnoreCase(String.valueOf(_expected)))
            return onCaptchaError(request, response);
        String username = getUsername(request);
        String password = getPassword(request);
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        AuthenticationToken token = new CaptchaUsernamePasswordToken(username, password, rememberMe, host, captcha);
        try {
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        }
        catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    protected boolean onCaptchaError(ServletRequest req, ServletResponse resp) {
        if (NutShiro.isAjax(req)) {
            NutMap re = new NutMap().setv("ok", false).setv("msg", "验证码错误");
            NutShiro.rendAjaxResp(req, resp, re);
            return false;
        } else {
            return super.onLoginFailure(null, new AuthenticationException("验证码错误"), req, resp);
        }
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest req, ServletResponse resp) {
        if (NutShiro.isAjax(req)) {
            NutMap re = new NutMap().setv("ok", false).setv("msg", "登陆失败");
            NutShiro.rendAjaxResp(req, resp, re);
            return false;
        }
        return super.onLoginFailure(token, e, req, resp);
    }

    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest req, ServletResponse resp) throws Exception {
        subject.getSession().setAttribute(NutShiro.SessionKey, subject.getPrincipal());
        if (NutShiro.isAjax(req)) {
            NutShiro.rendAjaxResp(req, resp, new NutMap().setv("ok", true));
            return false;
        }
        return super.onLoginSuccess(token, subject, req, resp);
    }

    public View match(ActionContext ac) {
        HttpServletRequest request = ac.getRequest();
        AuthenticationToken authenticationToken = createToken(request, ac.getResponse());
        request.setAttribute("loginToken", authenticationToken);
        return null;
    }
}