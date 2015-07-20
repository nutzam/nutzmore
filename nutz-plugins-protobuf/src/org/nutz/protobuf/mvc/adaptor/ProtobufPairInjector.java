package org.nutz.protobuf.mvc.adaptor;

import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;

/**
 * 根据 HTTP 参数表，生成一个 POJO 对象
 * 
 * @author rekoe(koukou900@qq.com)
 */
public class ProtobufPairInjector implements ParamInjector {

	protected Class<?> clazz;
	private ExtensionRegistry registry;
	private ConcurrentHashMap<Class<?>, Method> methodCache;

	public ProtobufPairInjector(Type type, ExtensionRegistry registry, ConcurrentHashMap<Class<?>, Method> methodCache) {
		this.clazz = Lang.getTypeClass(type);
		if (!this.clazz.isAssignableFrom(Message.class))
			throw Lang.makeThrow("Can not accept Class, type '%s'", clazz.getName());
		this.registry = registry;
		this.methodCache = methodCache;
	}

	public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
		try {
			String contentType = req.getContentType();
			if (contentType == null) {
				throw Lang.makeThrow(IllegalArgumentException.class, "Content-Type is NULL!!");
			}
			DataInputStream dis = new DataInputStream(req.getInputStream());
			if (contentType.contains("application/x-protobuf")) {
				Message.Builder builder = getMessageBuilder(clazz);
				builder.mergeFrom(dis, this.registry);
				return builder.build();
			}
			throw Lang.makeThrow(IllegalArgumentException.class, "UnSupport Content-Type : " + contentType);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	private Message.Builder getMessageBuilder(Class<?> clazz) throws Exception {
		Method method = methodCache.get(clazz);
		if (method == null) {
			method = clazz.getMethod("newBuilder");
			methodCache.put(clazz, method);
		}
		return (Message.Builder) method.invoke(clazz);
	}

}
