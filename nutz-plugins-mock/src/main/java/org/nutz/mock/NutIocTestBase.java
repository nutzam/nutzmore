package org.nutz.mock;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.IocBy;

/**
 * @author wendal
 *
 */
public abstract class NutIocTestBase extends Assert {
    
    private static final Log log = Logs.get();
    
    // 下面的方法必须覆盖一个
    
    /**
     * 返回MainModule类,必须带IocBy注解. 一般用于Mvc环境下
     */
    protected Class<?> getMainModule() throws Exception {
        throw new IllegalArgumentException("Must override one of getMainModule/getIocArgs/getIocLoader");
    }
    
    /**
     * 直接返回Ioc参数,例如 return new String[]{"*js", "ioc/", "*anno", "net.wendal.nutzbook", "*tx", "*async"};
     */
    protected String[] getIocArgs() throws Exception {
        return getMainModule().getAnnotation(IocBy.class).args();
    }
    
    /**
     * 返回一个IocLoader, 一般用于测试自定义IocLoader,比较少用到.
     */
    protected IocLoader getIocLoader() throws Exception {
        return new ComboIocLoader(getIocArgs());
    }

    // 下面的方法按需覆盖
    
    /**
     * Ioc初始化后执行的逻辑
     */
    protected void _before() throws Exception {}
    
    /**
     * Ioc销毁前执行的逻辑
     */
    protected void _after() throws Exception {}
    
    //-----------------------------------------------------------------------

    protected Ioc ioc;
    
    @Before
    public void before() throws Exception {
        ioc = new NutIoc(getIocLoader());
        injectSelfFields();
        _before();
    }
    
    @After
    public void after() throws Exception {
        _after();
        if (ioc != null) {
            ioc.depose();
        }
    }
    
    protected void injectSelfFields() throws Exception {
        for (Field field : getClass().getDeclaredFields()) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                log.debug("inject field name="+field.getName());
                field.setAccessible(true);
                Object obj = ioc.get(field.getType(), field.getName());
                field.set(this, obj);
            }
        }
    }
}
