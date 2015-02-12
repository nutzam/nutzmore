package org.nutz.integration.jsr303;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

public class ValidationActionFilter2 implements ActionFilter {
	
	ExecutableValidator validator;
	
	public ValidationActionFilter2() {
		validator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
	}

	@Override
	public View match(ActionContext ac) {
		Object[] args = ac.getMethodArgs();
		if (args != null && args.length > 1) {
			Class<?>[] array = ac.getMethod().getParameterTypes();
			for (int i = 0; i < array.length; i++) {
				if (array[i] == ValidationResult.class) {
					if (args[i] == null)
						args[i] = new ValidationResult();
					Set<ConstraintViolation<Object>> violations = validator.validateParameters(ac.getModule(), ac.getMethod(), ac.getMethodArgs());
					((ValidationResult) args[i]).add(violations);
					break;
				}
			}
		}
		return null;
	}

}
