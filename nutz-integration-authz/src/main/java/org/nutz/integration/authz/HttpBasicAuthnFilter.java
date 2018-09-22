package org.nutz.integration.authz;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.StringTokenizer;

/**
 * HTTP基础身份认证（HTTP basic authentication），用来测试本插件功能，可以替换成其他实现
 * @author Yang Luo<hsluoyz@gmail.com>
 *
 */
public class HttpBasicAuthnFilter extends AbstractProcessor implements ActionFilter {
    private String realm = "Protected";

    // Gets HTTP basic authentication's user name and password.
    private String getUserPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                String basic = st.nextToken();

                if (basic.equalsIgnoreCase("Basic")) {
                    try {
                        String credentials = new String(Base64.getDecoder().decode(st.nextToken()), "UTF-8");
                        int p = credentials.indexOf(":");
                        if (p != -1) {
                            return credentials;
                        } else {
                            unauthorized(response, "Invalid authentication token");
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new Error("Couldn't retrieve authentication", e);
                    }
                }
            }
        } else {
            unauthorized(response, "Authorization header not found");
        }

        return "";
    }

    // Checks the correctness of user name and password as you like.
    private boolean checkUserPassword(String username, String password) {
        return true;
    }

    // Returns HTTP 401 Unauthorized if the authentication fails.
    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    @Override
    public View match(ActionContext ac) {
        return null;
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        HttpServletRequest request = ac.getRequest();
        HttpServletResponse response = ac.getResponse();

        try {
            // Get user name and password from HTTP header.
            String credentials = getUserPassword(request, response);
            if (credentials.equals("")) {
                return;
            }
            int p = credentials.indexOf(":");
            String username = credentials.substring(0, p).trim();
            String password = credentials.substring(p + 1).trim();

            // Check the user name and password.
            if (!checkUserPassword(username, password)) {
                unauthorized(response, "Bad credentials");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // All passed, go to the next handler.
        doNext(ac);
    }
}
