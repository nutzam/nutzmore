package org.nutz.integration.nettice.core.exception;

public class InterceptorNoExistException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String id;

	public InterceptorNoExistException(String id) {
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return "interceptor id [ " + id + "] is not configured!";
	}
}
