package org.nutz.integration.authz;

import org.casbin.jcasbin.main.Enforcer;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Nutz与jCasbin的集成插件
 * @author Yang Luo<hsluoyz@gmail.com>
 *
 */
public class JCasbinAuthzFilter extends AbstractProcessor implements ActionFilter {
    static Enforcer enforcer;

    // Initialize jCasbin's enforcer with model and policy rules.
    // Here we load policy from file, you can choose to load policy from database.
    static {
        enforcer = new Enforcer("examples/authz_model.conf", "examples/authz_policy.csv");
    }

    // In this demo, we use HTTP basic authentication as the authentication method.
    // This method retrieves the user name from the HTTP header and passes it to jCasbin.
    // You can change to your own authentication method like OAuth, JWT, Apache Shiro, etc.
    // You need to implement this getUser() method to make sure jCasbin can get the
    // authenticated user name.
    private String getUser(HttpServletRequest request) {
        String res = "";

        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            // credentials = "username:password"
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            final String[] values = credentials.split(":", 2);
            res = values[0];
        }

        return res;
    }

    public View match(ActionContext ac) {
        return null;
    }

    // Filters all requests through jCasbin's authorization.
    // If jCasbin allows the request, pass the request to next handler.
    // If jCasbin denies the request, return HTTP 403 Forbidden.
    @Override
    public void process(ActionContext ac) throws Throwable {
        HttpServletRequest request = ac.getRequest();
        HttpServletResponse response = ac.getResponse();

        String user = getUser(request);
        String path = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("(" + user + ", " + path + ", " + method + ")");

        if (enforcer.enforce(user, path, method)) {
            doNext(ac);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
