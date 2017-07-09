package org.nutz.integration.shiro.ioc;

import javax.servlet.ServletContext;

import org.apache.shiro.web.env.EnvironmentLoader;
import org.nutz.mvc.Mvcs;

public class NutShiroEnvironmentLoader extends EnvironmentLoader {

    protected ServletContext servletContext;
    
    public ServletContext getServletContext() {
        if (servletContext == null)
            servletContext = Mvcs.getServletContext();
        return servletContext;
    }
    
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    public void init() {
        super.initEnvironment(getServletContext());
    }
    
    public void depose() {
        super.destroyEnvironment(getServletContext());
    }
}
