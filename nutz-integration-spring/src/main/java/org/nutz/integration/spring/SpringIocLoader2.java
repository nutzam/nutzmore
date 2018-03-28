package org.nutz.integration.spring;

import java.util.Arrays;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.springframework.context.ApplicationContext;

public class SpringIocLoader2 implements IocLoader {

    protected ApplicationContext applicationContext;

    protected String[] names;

    public SpringIocLoader2(ApplicationContext applicationContext, String[] names) {
        this.applicationContext = applicationContext;
        this.names = names;
    }

    public String[] getName() {
        return names;
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        if (!has(name))
            throw new ObjectLoadException("Object '" + name + "' without define!");
        return Iocs.wrap(applicationContext.getBean(name));
    }

    public boolean has(String name) {
        return Arrays.asList(names).contains(name);
    }

}
