package org.nutz.integration.nettice.core.exception;

public class ValidationException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ValidationException() {
        super();
    }
	
	public ValidationException(String s) {
        super(s);
    }
	
	public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
	
}
