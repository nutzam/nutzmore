package org.nutz.integration;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.config.AbstractNutConfig;
import org.nutz.mvc.impl.ServletValueProxyMaker;

public class Webs {

    private static final Log log = Logs.get();

    static Ioc ioc; 
    
    static final Object lock = new Object();
    
    @SuppressWarnings("unchecked")
    public static Ioc ioc() {
        if (ioc != null)
            return ioc;
        synchronized (lock) {
            if (ioc != null)
                return ioc;
            log.debug("Search for NutIoc");
            ServletContext servletContext = Mvcs.getServletContext();
            if (servletContext != null) {
                String mainModule = servletContext.getInitParameter("nutz.modules");
                if (mainModule != null)
                    try {
                        makeIoc(mainModule);
                        return ioc;
                    }
                    catch (ClassNotFoundException e) {
                        throw Lang.wrapThrow(e);
                    }
                
                //也行我能直接拿到Ioc容器
                ioc = Mvcs.getIoc();
                if (ioc != null)
                    return ioc;
                else {
                    //Search in servletContext.attr
                    Enumeration<String> names = servletContext.getAttributeNames();
                    while (names.hasMoreElements()) {
                        String attrName = (String) names.nextElement();
                        Object obj = servletContext.getAttribute(attrName);
                        if (obj instanceof Ioc) {
                            ioc = (Ioc)obj;
                            return ioc;
                        }
                    }
                    
                    //还是没找到? 试试新版Mvcs.ctx
                    ioc = Mvcs.ctx.getDefaultIoc();
                    if (ioc != null) {
                        return ioc;
                    }
                }
            }
        }
        log.warn("No Ioc found!!");
        throw new RuntimeException("NutIoc not found!!");
    }
    
    public static void makeIoc(String mainModule) throws ClassNotFoundException {
        IocBy ib = Class.forName(mainModule).getAnnotation(IocBy.class);
        if (ib == null)
            throw new IllegalArgumentException("Need IocBy!!");
        if (log.isDebugEnabled())
            log.debugf("@IocBy(%s)", ib.type().getName());

        makeIoc(ib.type(), ib.args());
    }
    
    public static void makeIoc(Class<? extends IocProvider> clazz, String[] args) {
        final ServletContext context = Mvcs.getServletContext();
        NutConfig config = new AbstractNutConfig(context) {
            
            public ServletContext getServletContext() {
                return context;
            }
            
            public List<String> getInitParameterNames() {
                return new ArrayList<String>(0);
            }
            
            public String getInitParameter(String name) {
                return null;
            }
            
            public String getAppName() {
                return "struts-nutz";
            }
        };
        ioc = Mirror.me(clazz).born().create(config, args);
        // 如果是 Ioc2 的实现，增加新的 ValueMaker
        if (ioc instanceof Ioc2) {
            ((Ioc2) ioc).addValueProxyMaker(new ServletValueProxyMaker(config.getServletContext()));
        }
    }
}
