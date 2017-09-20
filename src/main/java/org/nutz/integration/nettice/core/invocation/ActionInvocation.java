package org.nutz.integration.nettice.core.invocation;

import java.lang.reflect.Method;

import org.nutz.integration.nettice.core.BaseAction;
import org.nutz.integration.nettice.core.Return;

/**
 * 封装了action拦截器和调用的过程
 * 
 * @author yunfeng.cheng
 * @create 2016-08-08
 */
public class ActionInvocation {

	private ActionProxy proxy;

	public void init(ActionProxy proxy) {
		this.proxy = proxy;
	}

	public BaseAction getAction() {
		return proxy.getActionObject();
	}

	public Method getActionMethod() {
		return proxy.getMethod();
	}

	public Return invoke() throws Exception {
		return invokeAction();
	}

	protected Return invokeAction() throws Exception {
		BaseAction action = proxy.getActionObject();
		Method method = proxy.getMethod();
		String methodName = proxy.getMethodName();
		return action.processRequest(method, methodName);
	}

}
