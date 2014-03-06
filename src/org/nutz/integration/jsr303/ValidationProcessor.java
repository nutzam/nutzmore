package org.nutz.integration.jsr303;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * 实现jsr303的Processor,实现对入口方法参数的校验, 需要参数声明@Valid注解,及声明一个ValidationResult参数.<p/>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ValidationProcessor extends AbstractProcessor {

	protected ValidatorFactory factory;

	protected Validator validator;

	protected Map<Method, Integer> map = new ConcurrentHashMap<Method, Integer>();

	public ValidationProcessor() {
		init();
	}

	/**
	 * 建议子类覆盖这个方法以最大化定制validator
	 */
	public void init() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		Class<?>[] array = ai.getMethod().getParameterTypes();
		for (int i = 0; i < array.length; i++) {
			if (array[i] == ValidationResult.class) {
				map.put(ai.getMethod(), i);
				break;
			}
		}
	}

	public void process(ActionContext ac) throws Throwable {
		Object[] args = ac.getMethodArgs();
		if (args != null && args.length > 1) {
			Integer index = map.get(ac.getMethod());
			if (index != null) {
				if (args[index] == null)
					args[index] = new ValidationResult();
				for (Object obj : args) {
					if (obj == null)
						continue;
					if (obj.getClass().getAnnotation(Valid.class) == null)
						continue;
					Set<ConstraintViolation<Object>> violations = validator.validate(obj);
					((ValidationResult) args[index]).add(violations);
				}
			}
		}
		doNext(ac);
	}

}
