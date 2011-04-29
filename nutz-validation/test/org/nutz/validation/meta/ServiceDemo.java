package org.nutz.validation.meta;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.validation.Errors;

/**
 * 基于 AOP 的验证
 * 
 * @author QinerG(QinerG@gmail.com)
 */
@IocBean
public class ServiceDemo {

	@Aop("validationInterceptor")
	public Errors test(Bean bean, Errors es) {
		System.out.println(es.errorCount());
		return es;
	}
}
