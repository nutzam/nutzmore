package org.nutz.integration.jsr303;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

/**
 * 实现jsr303的ActionFilter,实现对入口方法参数的校验, 需要参数声明@Valid注解,及声明一个ValidationResult参数.<p/>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ValidationActionFilter implements ActionFilter {
	
	protected ValidatorFactory factory;

	protected Validator validator;
	
	public ValidationActionFilter() {
		init();
	}

	/**
	 * 建议子类覆盖这个方法以最大化定制validator
	 */
	public void init() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	public View match(ActionContext ac) {
		Object[] args = ac.getMethodArgs();
		if (args != null && args.length > 1) {
			Class<?>[] array = ac.getMethod().getParameterTypes();
			for (int i = 0; i < array.length; i++) {
				if (array[i] == ValidationResult.class) {
					if (args[i] == null)
						args[i] = new ValidationResult();
					for (Object obj : args) {
						if (obj == null)
							continue;
						if (obj.getClass().getAnnotation(Valid.class) == null)
							continue;
						Set<ConstraintViolation<Object>> violations = validator.validate(obj);
						((ValidationResult) args[i]).add(violations);
					}
					break;
				}
			}
		}
		return null;
	}
}
