package org.nutz.plugins.wkcache;

import org.nutz.aop.MethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;
import org.nutz.plugins.wkcache.annotation.CacheUpdate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(name = "$aop_wkcache")
public class WkcacheAopConfigure implements AopConfigration {

    public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz) {
        List<InterceptorPair> list = new ArrayList<InterceptorPair>();
        boolean flag = true;
        for (Method method : clazz.getMethods()) {
            if (method.getAnnotation(CacheResult.class) != null
                    || method.getAnnotation(CacheUpdate.class) != null
                    || method.getAnnotation(CacheRemove.class) != null
                    || method.getAnnotation(CacheRemoveAll.class) != null) {
                flag = false;
                break;
            }
        }
        if (flag)
            return list;
        list.add(new InterceptorPair(ioc.get(WkcacheResultInterceptor.class),
                new WkcacheMethodMatcher(CacheResult.class)));
        list.add(new InterceptorPair(ioc.get(WkcacheUpdateInterceptor.class),
                new WkcacheMethodMatcher(CacheUpdate.class)));
        list.add(new InterceptorPair(ioc.get(WkcacheRemoveEntryInterceptor.class),
                new WkcacheMethodMatcher(CacheRemove.class)));
        list.add(new InterceptorPair(ioc.get(WkcacheRemoveAllInterceptor.class),
                new WkcacheMethodMatcher(CacheRemoveAll.class)));
        return list;
    }
}

class WkcacheMethodMatcher implements MethodMatcher {

    protected Class<? extends Annotation> klass;

    public WkcacheMethodMatcher(Class<? extends Annotation> klass) {
        this.klass = klass;
    }

    public boolean match(Method method) {
        return method.getAnnotation(klass) != null;
    }

}