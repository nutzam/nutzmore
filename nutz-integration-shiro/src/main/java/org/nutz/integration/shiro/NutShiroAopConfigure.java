package org.nutz.integration.shiro;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.MethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.IocBean;
/**
 * 自动配置Shiro的AOP配置
 * @author wendal<wendal1985@gmail.com>
 *
 */
@IocBean(name="$aop_shiro")
public class NutShiroAopConfigure implements AopConfigration, MethodMatcher {

    public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> klass) {
    	boolean flag = true;
    	for (Method method : klass.getDeclaredMethods()) {
			if (NutShiro.match(method)) {
				flag = false;
				break;
			}
		}
    	if (flag)
    		return null;
        List<InterceptorPair> list = new ArrayList<InterceptorPair>();
        list.add(new InterceptorPair(new NutShiroMethodInterceptor(), this));
        return list;
    }

    public boolean match(Method method) {
        return NutShiro.match(method);
    }

}
