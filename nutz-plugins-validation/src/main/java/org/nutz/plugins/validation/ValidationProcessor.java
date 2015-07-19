package org.nutz.plugins.validation;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.plugins.validation.annotation.AnnotationValidation;

/**
 * 可用于 MVC 效验的动作链
 * <p>
 * 要求 action 参数中必须有一个 Errors 类型的参数（不需要使用 Param 声明）。当验证完成后会向这个参数赋值
 * 
 * @author QinerG(QinerG@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class ValidationProcessor extends AbstractProcessor {
	
	private static AnnotationValidation av = new AnnotationValidation();
	
	protected boolean hasErrorArg;
	
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
	    for (Class<?> klass : ai.getMethod().getParameterTypes()) {
            if (klass.isAssignableFrom(Errors.class))
                hasErrorArg = true;
        }
	}

	public void process(ActionContext ac) throws Throwable {
	    if (hasErrorArg) {
	        Errors es = new Errors();
	        for (Object obj : ac.getMethodArgs()) {
                av.validate(obj, es);
            }
	    }
		doNext(ac);
	}
}
