package org.nutz.integration.spring;

import javax.servlet.ServletContext;

import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.util.AbstractLifeCycle;
import org.nutz.mvc.Mvcs;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 注意,这个类的作用是从NutIoc的接口,通过名字,拿到一个Spring Ioc容器中的对象.
 * @author wendal
 *
 */
public class SpringIocLoader extends AbstractLifeCycle implements IocLoader {
    
    protected ApplicationContext context;
    
    protected ContextLoaderListener ctx;
    
    protected boolean inited;
    
    protected String contextConfigLocation;
    
    public SpringIocLoader() {
    }
    
    public SpringIocLoader(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;

        ServletContext sc = Mvcs.getServletContext();
        if (sc != null && sc.getInitParameter("contextConfigLocation") == null)
            sc.setInitParameter("contextConfigLocation", contextConfigLocation);
    }

    public String[] getName() {
        return context().getBeanDefinitionNames();
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        if (!has(name))
            throw new IocException(name, "not such bean in spring ioc");
        IocObject iocObject = new IocObject();
        iocObject.addArg(new IocValue(IocValue.TYPE_NORMAL, context()));
        iocObject.addArg(new IocValue(IocValue.TYPE_NORMAL, name));
        iocObject.setFactory("org.nutz.integration.spring.SpringIocLoader#fromSpring");
        iocObject.setType(context().getType(name));
        return iocObject;
    }

    public boolean has(String name) {
        return context().containsBean(name);
    }

    protected ApplicationContext context() {
        if (context == null) {
            _init();
        }
        return context;
    }
    
    public static Object fromSpring(ApplicationContext context, String name) {
        return context.getBean(name);
    }
    
    public void _init() {
        if (context == null) {
            if (contextConfigLocation == null) {
                context = WebApplicationContextUtils.getRequiredWebApplicationContext(Mvcs.getServletContext());
            } else {
                ctx = new ContextLoaderListener();
                ServletContext sc = Mvcs.getServletContext();
                context = ctx.initWebApplicationContext(sc);
            }
        }
    }

    public void depose() throws Exception {
        if (ctx != null)
            ctx.closeWebApplicationContext(Mvcs.getServletContext());
    }
}
