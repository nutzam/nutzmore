package org.nutz.integration.spring;

import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.mvc.Mvcs;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 注意,这个类的作用是从NutIoc的接口,通过名字,拿到一个Spring Ioc容器中的对象.
 * @author wendal
 *
 */
public class SpringIocLoader implements IocLoader {
    
    protected ApplicationContext context;

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
        return iocObject;
    }

    public boolean has(String name) {
        return context().containsBean(name);
    }

    protected ApplicationContext context() {
        if (context == null)
            context = WebApplicationContextUtils.getRequiredWebApplicationContext(Mvcs.getServletContext());
        return context;
    }
    
    public static Object fromSpring(ApplicationContext context, String name) {
        return context.getBean(name);
    }
}
