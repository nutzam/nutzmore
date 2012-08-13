package org.nutz.integration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;
import org.nutz.resource.Scans;

/**
 * 不使用NutMVC时,Web环境下初始化NutIoc<p/>
 * 用法:<p/> 
 * <ul>首先,建一个MainModule类,配上@IocBy</ul>
 * <ul>然后在你的web.xml中,添加</ul>
 * <pre>
 * {@code
 * <filter>
 *  <filter-name>nutz</filter-name>
 *  <filter-class>org.nutz.integration.NutServletContextMaster</filter-class>
 *  <init-param>
 *      <param-name>modules</param-name>
 *      <param-value>com.mine.app.MainModule</param-value>
 *  </init-param>
 * </filter>
 * }
 * </pre>
 * @author wendal
 *
 */
public class NutServletContextMaster extends HttpServlet implements ServletContextListener, Filter {

    private static final long serialVersionUID = 2915093071648223328L;

    public void contextInitialized(ServletContextEvent event) {
        _init(event.getServletContext(), null);
    }

    public void contextDestroyed(ServletContextEvent event) {
        destroy();
    }
    
    public void init(FilterConfig config) throws ServletException {
        _init(config.getServletContext(), config.getInitParameter("modules"));
    }
    
    public void init(ServletConfig config) throws ServletException {
        _init(config.getServletContext(), config.getInitParameter("modules"));
    }
    
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(req, resp);
    }
    
    public void destroy() {
        try {
            Webs.ioc().depose();
        }
        catch (Throwable e) {}
    }
    
    protected void _init(ServletContext ctx, String mainmodule) {
        Mvcs.setServletContext(ctx);
        Scans.me().init(ctx);
        if (mainmodule == null)
            mainmodule = ctx.getInitParameter("nutz.modules");
        if (mainmodule != null)
            try {
                Webs.makeIoc(mainmodule);
            }
            catch (ClassNotFoundException e) {
                throw Lang.wrapThrow(e);
            }
    }
}
