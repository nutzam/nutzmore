package org.nutz.plugins.oauth2.server.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.plugins.oauth2.server.Constants;
import org.nutz.plugins.oauth2.server.entity.Status;

public class Oauth2Filter implements ActionFilter {

	private final static Log log = Logs.get();

	public final static int HTTPSTATUS_UNAUTHORIZED = 401;
	public final static int HTTPSTATUS_BAD_REQUEST = 400;

	/**
	 * oAuth认证失败时的输出
	 * 
	 * @param res
	 * @throws OAuthSystemException
	 * @throws IOException
	 */
	private void oAuthFaileResponse(HttpServletResponse res) throws OAuthSystemException, IOException {
		OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED).setRealm(Constants.RESOURCE_SERVER_NAME).setError(OAuthError.ResourceResponse.INVALID_TOKEN).buildHeaderMessage();
		res.addHeader("Content-Type", "application/json; charset=utf-8");
		res.addHeader(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
		PrintWriter writer = res.getWriter();
		writer.write(Json.toJson(getStatus(HTTPSTATUS_UNAUTHORIZED, Constants.INVALID_ACCESS_TOKEN)));
		writer.flush();
		writer.close();
	}

	/**
	 * 验证accessToken
	 * 
	 * @param accessToken
	 * @return
	 * @throws IOException
	 */
	private boolean checkAccessToken(String accessToken) throws IOException {
		URL url = new URL(Constants.CHECK_ACCESS_CODE_URL + accessToken);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.disconnect();
		return HttpServletResponse.SC_OK == conn.getResponseCode();
	}

	private Status getStatus(int code, String msg) {
		Status status = new Status();
		status.setCode(code);
		status.setMsg(msg);
		return status;
	}

	@Override
	public View match(ActionContext actionContext) {
		try {
			// 构建OAuth资源请求
			OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(actionContext.getRequest(), ParameterStyle.QUERY);
			String accessToken = oauthRequest.getAccessToken();
			// 验证Access Token
			if (!checkAccessToken(accessToken)) {
				// 如果不存在/过期了，返回未验证错误，需重新验证
				oAuthFaileResponse(actionContext.getResponse());
			}
			return null;
		} catch (OAuthProblemException e) {
			try {
				oAuthFaileResponse(actionContext.getResponse());
			} catch (OAuthSystemException ex) {
				log.error("error trying to access oauth server %s", ex);
			} catch (IOException ex) {
				log.error("error trying to access oauth server %s", ex);
			}
		} catch (OAuthSystemException e) {
			log.error("error trying to access oauth server %s", e);
		} catch (IOException e) {

		}
		return null;
	}

}
