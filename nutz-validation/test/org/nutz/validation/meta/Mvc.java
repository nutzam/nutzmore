package org.nutz.validation.meta;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Dumps;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.ioc.provider.AnnotationIocProvider;
import org.nutz.validation.Errors;
import org.nutz.validation.ValidationProcessor;

/**
 * 在 MVC 中使用拦截器进行参数验证，这种方式必须在每个需要验证的方法声明 AOP 拦截
 * <p>
 * 如果感觉麻烦的话，还可以试试“参数验证的动作链”，在默认的动作链中加入 ValidationProcessor
 * 动作链，即可自动对所有用户请求的参数进行验证。
 * 
 * @see ValidationProcessor
 * @author QinerG(QinerG@gmail.com)
 */
@AdaptBy(type = JsonAdaptor.class)
@IocBy(type = AnnotationIocProvider.class, args = { "org.nutz.validation" })
@InjectName
@IocBean
public class Mvc {

	@At("/test")
	@Ok("json")
	@Aop("validationInterceptor")
	public int test(@Param("bean")Bean bean, Errors es) {
		System.out.println(Dumps.obj(bean));
		if (es != null) {
			return es.errorCount();
		}
		return -99;
	}

}
