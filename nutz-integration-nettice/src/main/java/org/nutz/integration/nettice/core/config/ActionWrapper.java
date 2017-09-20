package org.nutz.integration.nettice.core.config;

import java.lang.reflect.Method;

import org.nutz.integration.nettice.core.BaseAction;

/**
 * 包装Action
 */
public class ActionWrapper {

	public BaseAction actionObject;
	public Method method;
	public String actionPath;
	public Method callBackMethod;

	public ActionWrapper(BaseAction actionObject, Method method, String actionPath) {
		this(actionObject, method, actionPath, null);
	}

	public ActionWrapper(BaseAction actionObject, Method method, String actionPath, Method callBackMethod) {
		this.actionObject = actionObject;
		this.method = method;
		this.actionPath = actionPath;
		this.callBackMethod = callBackMethod;
	}
}
