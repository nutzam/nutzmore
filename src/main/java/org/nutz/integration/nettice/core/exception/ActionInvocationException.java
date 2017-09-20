package org.nutz.integration.nettice.core.exception;

public class ActionInvocationException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private String methodName;
    private Object[] params;

    public ActionInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionInvocationException(String methodName, Object[] params, String message, Throwable cause) {
        super(message, cause);
        this.methodName = methodName;
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

}
