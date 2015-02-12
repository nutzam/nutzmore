package org.nutz.plugins.validation;

import java.lang.reflect.Method;

import org.nutz.aop.interceptor.AbstractMethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.validation.annotation.AnnotationValidation;

/**
 * 基于注解的验证用拦截器
 * <p>
 * 该拦截器主要用于方法参数的验证，要求该方法中必须有一个 Errors 类型的参数(允许为空)，当验证完成后会向这个参数赋值
 * 
 * @author QinerG(QinerG@gmail.com)
 */
@IocBean(name = "validationInterceptor")
public class ValidationInterceptor extends AbstractMethodInterceptor {

	private static AnnotationValidation av = new AnnotationValidation();

	/**
	 * 方法调用前进行拦截，遍历参数进行验证
	 */
	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		Errors es = ValidationUtils.checkArgs(method.getParameterTypes(), args);
		if (null != es) {
			for (Object argsObj : args) {
				if (argsObj instanceof Errors)
					continue;
				av.validate(argsObj, es);
			}
		}
		return true;
	}
}
