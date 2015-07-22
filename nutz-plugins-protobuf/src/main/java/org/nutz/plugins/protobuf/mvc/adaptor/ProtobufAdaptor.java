package org.nutz.plugins.protobuf.mvc.adaptor;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.annotation.Param;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.XmlFormat;

public class ProtobufAdaptor extends PairAdaptor {

	private static final Log log = Logs.get();

	private ExtensionRegistry registry;

	private final ConcurrentHashMap<Class<?>, Method> methodCache = new ConcurrentHashMap<Class<?>, Method>();
	private Class<?> clazz;

	public ProtobufAdaptor() {
		registry = ExtensionRegistry.newInstance();
	}

	@Override
	protected ParamInjector evalInjectorBy(Type type, Param param) {
		Class<?> clazz = Lang.getTypeClass(type);
		if (clazz == null) {
			if (log.isWarnEnabled())
				log.warnf("!!Fail to get Type Class : type=%s , param=%s", type, param);
			return null;
		}
		if (Message.class.isAssignableFrom(clazz)) {
			if (!Lang.isEmpty(this.clazz)) {
				throw Lang.makeThrow(IllegalArgumentException.class, "Only Support One Message Type Class");
			}
			this.clazz = clazz;
			return new ProtobufPairInjector(type);
		}
		return super.evalInjectorBy(type, param);
	}

	@Override
	public Message getReferObject(ServletContext sc, HttpServletRequest request, HttpServletResponse response, String[] pathArgs) {
		try {
			String contentType = request.getContentType();
			if (contentType == null) {
				throw Lang.makeThrow(IllegalArgumentException.class, "Content-Type is NULL!!");
			}
			Charset charset = Charset.forName(request.getCharacterEncoding());
			if (Lang.isEmpty(clazz)) {
				throw Lang.makeThrow(IllegalArgumentException.class, "Not Support Adaptor,You Must Have A Message Type Class ");
			}
			Message.Builder builder = getMessageBuilder(clazz);
			InputStreamReader reader = new InputStreamReader(request.getInputStream(), charset);
			if (contentType.contains("application/json")) {
				JsonFormat.merge(reader, this.registry, builder);
			} else if (contentType.contains("text/plain")) {
				TextFormat.merge(reader, this.registry, builder);
			} else if (contentType.contains("application/xml")) {
				XmlFormat.merge(reader, this.registry, builder);
			} else {
				builder.mergeFrom(request.getInputStream(), this.registry);
			}
			return builder.build();
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
