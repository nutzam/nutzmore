package org.nutz.plugins.daomapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.plugins.daomapper.impl.ClearMethodMapper;
import org.nutz.plugins.daomapper.impl.CountMethodMapper;
import org.nutz.plugins.daomapper.impl.FetchMethodMapper;
import org.nutz.plugins.daomapper.impl.QueryMethodMapper;

public class SimpleMapper implements InvocationHandler {

	public Dao dao;

	public Map<Method, MethodMapper> mappers;

	public SimpleMapper(Dao dao, Map<Method, MethodMapper> mappers) {
		this.dao = dao;
		this.mappers = mappers;
	}

	@SuppressWarnings("unchecked")
	public static <T> T map(Dao dao, String beanPackage, Class<T> klass) {
		Map<Method, MethodMapper> mappers = new HashMap<Method, MethodMapper>();
		for(Method method : klass.getMethods()) {
			String name = method.getName();
			if (!name.contains("By"))
				continue;
			if (name.startsWith("query")) {
				mappers.put(method, new QueryMethodMapper(dao, method, beanPackage));
			}
			else if (name.startsWith("fetch")) {
				mappers.put(method, new FetchMethodMapper(dao, method, beanPackage));
			}
			else if (name.startsWith("clear")) {
				mappers.put(method, new ClearMethodMapper(dao, method, beanPackage));
			}
			else if (name.startsWith("count")) {
				mappers.put(method, new CountMethodMapper(dao, method, beanPackage));
			}
		}
		SimpleMapper sm = new SimpleMapper(dao, mappers);
		return (T) Proxy.newProxyInstance(klass.getClassLoader(), new Class<?>[]{klass}, sm);
	}

	public static <T> T map(Dao dao, Class<?> bean, Class<T> klass) {
		return map(dao, bean.getPackage().getName(), klass);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodMapper mapper = mappers.get(method);
		if (mapper == null)
			return method.invoke(dao, args); // 映射到dao呗
		return mapper.exec(args);
	}
}
