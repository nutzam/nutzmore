package org.nutz.mock;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.mvc.annotation.IocBy;

public class NutTestRunner extends BlockJUnit4ClassRunner {
    
    ThreadLocal<Ioc> iocHolder = new ThreadLocal<Ioc>();

    public NutTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        if (klass.getAnnotation(IocBean.class) == null)
            throw new InitializationError("Must mark as @IocBean");
    }
    
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (isIgnored(method)) {
            super.runChild(method, notifier);
            return;
        }
        Ioc ioc = createIoc();
        try {
            iocHolder.set(ioc);
            super.runChild(method, notifier);
        } finally {
            iocHolder.remove();
            ioc.depose();
        }
    }

    /**
     * 返回MainModule类,必须带IocBy注解. 一般用于Mvc环境下
     */
    protected Class<?> getMainModule() {
        throw new IllegalArgumentException("Must override one of getMainModule/getIocArgs/createIocLoader/createIoc");
    }
    
    /**
     * 直接返回Ioc参数,例如 return new String[]{"*js", "ioc/", "*anno", "net.wendal.nutzbook", "*tx", "*async"};
     */
    protected String[] getIocArgs() {
        return getMainModule().getAnnotation(IocBy.class).args();
    }
    
    /**
     * 返回一个IocLoader, 一般用于测试自定义IocLoader,比较少用到.
     */
    protected IocLoader createIocLoader() {
        try {
            return new ComboIocLoader(getIocArgs());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected Ioc createIoc() {
        return new NutIoc(createIocLoader());
    }
    
    protected Object createTest() throws Exception {
        return iocHolder.get().get(getTestClass().getJavaClass());
    }
    
}
