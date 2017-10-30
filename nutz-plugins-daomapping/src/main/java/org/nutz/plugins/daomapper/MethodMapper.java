package org.nutz.plugins.daomapper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.MethodParamNamesScaner;

public abstract class MethodMapper {

	protected Method method;

	protected Dao dao;

	protected List<String> paramNames;

	protected Class<?> pojoType;

	protected String pName;

	public MethodMapper(Dao dao, Method method, String pName) {
		super();
		this.dao = dao;
		this.method = method;
		this.pojoType = method.getReturnType();
		this.pName = pName;
		if (List.class.isAssignableFrom(this.pojoType)) {
			Type t = Mirror.me(method.getGenericReturnType()).getGenericsType(0);
			if (t instanceof Class)
			    this.pojoType = (Class<?>) t;
		}
		paramNames = mName();
	}

	protected List<String> mName() {
		List<String> paramNames = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (char tmp : method.getName().substring(prefix().length()).toCharArray()) {
			if ('A' <= tmp && tmp <= 'Z') {
				if (sb.length() > 0) {
					paramNames.add(sb.toString());
					sb.setLength(0);
				}
			}
			sb.append(Character.toLowerCase(tmp));
		}
		if (sb.length() > 0)
			paramNames.add(sb.toString());

		if (paramNames.size() > 1) {
			if (paramNames.get(1).equals("by")) {
				try {
					pojoType = Lang.loadClass(pName + "." + Strings.upperFirst(paramNames.get(0)));
				} catch (ClassNotFoundException e) {
					throw Lang.wrapThrow(e);
				}
				paramNames.remove(0);
			}
		}
		if (paramNames.size() > 0 && paramNames.get(0).equals("by")) {
			paramNames.remove(0);
		}
		if (paramNames.isEmpty()) {
			paramNames = MethodParamNamesScaner.getParamNames(method);
		}
		return paramNames;
	}

	public Cnd makeCnd(Object[] args) {
		Cnd cnd = Cnd.NEW();
		for (int i = 0; i < args.length; i++) {
			cnd.and(paramNames.get(i), "=", args[i]);
		}
		return cnd;
	}

	public abstract Object exec(Object[] args);

	public abstract String prefix();
}
