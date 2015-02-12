package org.nutz.integration.jsr303;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

public class ValidationResult {

	protected List<Set<ConstraintViolation<Object>>> errors = new ArrayList<Set<ConstraintViolation<Object>>>();
	
	public boolean hasError() {
		return errors.size() != 0;
	}
	
	public List<Set<ConstraintViolation<Object>>> getErrors() {
		return errors;
	}
	
	public void add(Set<ConstraintViolation<Object>> violations) {
		errors.add(violations);
	}
}
