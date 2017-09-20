package org.nutz.integration.nettice.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.resource.Scans;

import org.nutz.integration.nettice.core.config.ActionWrapper;
import org.nutz.integration.nettice.core.config.RouterConfig;
import org.nutz.integration.nettice.core.exception.DuplicateActionException;
import org.nutz.integration.nettice.core.invocation.ActionInvocation;
import org.nutz.integration.nettice.core.invocation.ActionProxy;

/**
 * 路由上下文
 */
public class RouterContext {

	private final static Log log = Logs.get();

	private Map<String, ActionWrapper> actions = new HashMap<String, ActionWrapper>();

	private String suffix = ".action";

	public RouterContext(String configFilePath, String suffix) throws Exception {
		RouterConfig config = RouterConfig.parse(configFilePath);
		this.suffix = "." + suffix;
		initActionMap(config);
	}

	public RouterContext(String configFilePath) throws Exception {
		RouterConfig config = RouterConfig.parse(configFilePath);
		initActionMap(config);
	}

	private void initActionMap(RouterConfig config) {
		List<String> packages = config.getActionPacages();
		for (String packagee : packages) {
			List<Class<?>> clazzs = Scans.me().scanPackage(packagee);
			for (Class<?> clazz : clazzs) {
				try {
					if (!BaseAction.class.isAssignableFrom(clazz)) {
						continue;
					}
					BaseAction baseAction = (BaseAction) Mirror.me(clazz).born();
					At atClass = clazz.getAnnotation(At.class);
					if (Lang.isEmpty(atClass)) {
						for (Method method : clazz.getDeclaredMethods()) {
							registerAction("", baseAction, method);
						}
					} else {
						String[] classUrl = atClass.value();
						for (String atUrl : classUrl) {
							for (Method method : clazz.getDeclaredMethods()) {
								registerAction(atUrl, baseAction, method);
							}
						}
					}
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}

	private void registerAction(String clzAction, BaseAction baseAction, Method method) {
		if (method.getModifiers() == Modifier.PUBLIC) {
			At at = method.getAnnotation(At.class);
			boolean isRight = !Lang.isEmpty(at) || Strings.isNotBlank(clzAction);
			if (isRight) {
				if (Lang.isEmpty(at)) {
					registerAt(clzAction, method, baseAction);
				} else {
					String[] actions = at.value();
					if (Lang.isEmptyArray(actions)) {
						registerAt(clzAction, method, baseAction);
					} else {
						for (String action : actions) {
							registerAt(clzAction + action, method, baseAction);
						}
					}
				}
			}
		}
	}

	private void registerAt(String action, Method method, BaseAction baseAction) {
		String actionPath = action + method.getName() + suffix;
		if (this.actions.get(actionPath) != null) {
			throw new DuplicateActionException(this.actions.get(actionPath).method, method, actionPath);
		}
		ActionWrapper actionWrapper = new ActionWrapper(baseAction, method, actionPath);
		if (log.isDebugEnabled()) {
			log.debugf("load action %s", actionPath);
		}
		this.actions.put(actionPath, actionWrapper);
	}

	public ActionWrapper getActionWrapper(String path) {
		return actions.get(path);
	}

	public ActionProxy getActionProxy(ActionWrapper actionWrapper) throws Exception {
		ActionProxy proxy = new ActionProxy();
		ActionInvocation invocation = new ActionInvocation();
		invocation.init(proxy);
		proxy.setActionObject(actionWrapper.actionObject);
		proxy.setMethod(actionWrapper.method);
		proxy.setMethodName(actionWrapper.method.getName());
		proxy.setInvocation(invocation);
		return proxy;
	}

	public String getSuffix() {
		return suffix;
	}
}
