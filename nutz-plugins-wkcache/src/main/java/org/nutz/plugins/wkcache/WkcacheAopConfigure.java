package org.nutz.plugins.wkcache;

import org.nutz.aop.MethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.*;

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
        for (Method method : clazz.getMethods()) {
            CacheDefaults cdefaults = clazz.getAnnotation(CacheDefaults.class);
            CacheResult cresult = method.getAnnotation(CacheResult.class);
            CacheUpdate cupdate = method.getAnnotation(CacheUpdate.class);
            CacheRemove cremove = method.getAnnotation(CacheRemove.class);
            CacheRemoveAll cremoveall = method.getAnnotation(CacheRemoveAll.class);
            if (cresult != null) {
                // 取出非单例的拦截器实例
                WkcacheResultInterceptor wr = ioc.get(WkcacheResultInterceptor.class);
                // 提前把注解和方法传过去初始化
                wr.prepare(cdefaults, cresult, method);
                // 然后基于method == this.method 进行匹配拦截, 这样每个函数都有自己的WkcacheResultInterceptor实例
                list.add(new InterceptorPair(wr, new WkcacheMethodMatcher(method)));
            }
            if (cupdate != null) {
                WkcacheUpdateInterceptor wu = ioc.get(WkcacheUpdateInterceptor.class);
                wu.prepare(cdefaults, cupdate, method);
                list.add(new InterceptorPair(wu, new WkcacheMethodMatcher(method)));
            }
            if (cremove != null) {
                WkcacheRemoveEntryInterceptor wre = ioc.get(WkcacheRemoveEntryInterceptor.class);
                wre.prepare(cdefaults, cremove, method);
                list.add(new InterceptorPair(wre, new WkcacheMethodMatcher(method)));
            }
            if (cremoveall != null) {
                WkcacheRemoveAllInterceptor wra = ioc.get(WkcacheRemoveAllInterceptor.class);
                wra.prepare(cdefaults, cremoveall, method);
                list.add(new InterceptorPair(wra, new WkcacheMethodMatcher(method)));
            }
        }
        if (list.size() == 0)
            return null;
        return list;
    }
}

class WkcacheMethodMatcher implements MethodMatcher {

    protected Method method;

    public WkcacheMethodMatcher(Method method) {
        this.method = method;
    }

    public boolean match(Method method) {
        return this.method.equals(method);
    }

}