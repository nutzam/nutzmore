package org.nutz.plugins.oauth2.server.module.oauth;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.JspView;
import org.nutz.mvc.view.ViewWrapper;
import org.nutz.plugins.oauth2.server.Constants;
import org.nutz.plugins.oauth2.server.entity.OAuthUser;
import org.nutz.plugins.oauth2.server.entity.Status;
import org.nutz.plugins.oauth2.server.service.OAuthClientService;
import org.nutz.plugins.oauth2.server.service.OAuthService;
import org.nutz.plugins.oauth2.server.service.OAuthUserService;

@IocBean
public class AuthorizeModule {

    @Inject
    private OAuthService oAuthService;
    @Inject
    private OAuthClientService oAuthClientService;
    @Inject
    private OAuthUserService oAuthUserService;

    @At("/authorize")
    @Ok("json")
    public Object authorize(HttpServletRequest request, HttpServletResponse res)
            throws URISyntaxException, OAuthSystemException {

        try {

            // 构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);

            // 检查传入的客户端id是否正确
            if (!oAuthService.checkClientId(oauthRequest.getClientId())) {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                                        .setErrorDescription(Constants.INVALID_CLIENT_ID)
                                                        .buildJSONMessage();
                res.setStatus(response.getResponseStatus());
                return response.getBody();
            }

            // 如果用户没有登录，跳转到登陆页面
            if (!login(request)) {// 登录失败时跳转到登陆页面
                return new ViewWrapper(new JspView("jsp.oauth2login"), oAuthClientService.findByClientId(oauthRequest.getClientId()));
            }

            String username = request.getParameter("username"); // 获取用户名
            // 生成授权码
            String authorizationCode = null;
            // responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if (responseType.equals(ResponseType.CODE.toString())) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthService.addAuthCode(authorizationCode, username);
            }

            // 进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request,
                                                                                                              HttpServletResponse.SC_FOUND);
            // 设置授权码
            builder.setCode(authorizationCode);
            // 得到到客户端重定向地址
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);

            // 构建响应
            final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();

            // 根据OAuthResponse返回ResponseEntity响应
            res.setHeader("Location", response.getLocationUri());
            res.setStatus(response.getResponseStatus());
            return null;
        }
        catch (OAuthProblemException e) {

            // 出错处理
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                // 告诉客户端没有传入redirectUri直接报错
                res.addHeader("Content-Type", "application/json; charset=utf-8");
                Status status = new Status();
                status.setCode(404);
                status.setMsg(Constants.INVALID_REDIRECT_URI);
                return status;
            }
            // 返回错误消息（如?error=）
            final OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                                                          .error(e)
                                                          .location(redirectUri)
                                                          .buildQueryMessage();
            res.setHeader("Location", response.getLocationUri());
            res.setStatus(response.getResponseStatus());
            return null;
        }
    }

    private boolean login(HttpServletRequest request) {
        if ("get".equalsIgnoreCase(request.getMethod())) {
            request.setAttribute("error", "非法的请求");
            return false;
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (Strings.isEmpty(username) || Strings.isEmpty(password)) {
            request.setAttribute("error", "登录失败:用户名或密码不能为空");
            return false;
        }
        try {
            // 写登录逻辑
            OAuthUser user = oAuthUserService.findByUsername(username);
            if (user != null) {
                if (!oAuthUserService.checkUser(username, password, user.getSalt(), user.getPassword())) {
                    request.setAttribute("error", "登录失败:密码不正确");
                    return false;
                } else {
                    return true;
                }
            } else {
                request.setAttribute("error", "登录失败:用户名不正确");
                return false;
            }
        }
        catch (Exception e) {
            request.setAttribute("error", "登录失败:" + e.getClass().getName());
            return false;
        }
    }
}