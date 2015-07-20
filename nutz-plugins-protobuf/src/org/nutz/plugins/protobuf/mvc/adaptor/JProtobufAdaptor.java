package org.nutz.plugins.protobuf.mvc.adaptor;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.annotation.Param;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class JProtobufAdaptor extends PairAdaptor {

	private static final Log log = Logs.get();

	private Class<?> clazz;

	protected ParamInjector evalInjectorBy(Type type, Param param) {
		Class<?> clazz = Lang.getTypeClass(type);
		if (clazz == null) {
			if (log.isWarnEnabled())
				log.warnf("!!Fail to get Type Class : type=%s , param=%s", type, param);
			return null;
		}
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			Protobuf protobuf = field.getAnnotation(Protobuf.class);
			if (!Lang.isEmpty(protobuf)) {
				if (Lang.isEmpty(this.clazz)) {
					this.clazz = clazz;
					return new JProtobufPairInjector(type);
				} else {
					throw Lang.makeThrow(IllegalArgumentException.class, "Only Support One Message Type Class");
				}
			}
		}
		return super.evalInjectorBy(type, param);
	}

	public Object getReferObject(ServletContext sc, HttpServletRequest request, HttpServletResponse response, String[] pathArgs) {
		try {
			String contentType = request.getContentType();
			if (contentType == null) {
				throw Lang.makeThrow(IllegalArgumentException.class, "Content-Type is NULL!!");
			}
			if (contentType.contains("application/x-protobuf")) {
				if (Lang.isEmpty(this.clazz)) {
					throw Lang.makeThrow(IllegalArgumentException.class, "Not Support Adaptor,You Must Have A Message Type Class ");
				}
				Codec<?> codec = ProtobufProxy.create(clazz);
				InputStream is = request.getInputStream();
				log.debug(is.available());
				byte[] bytes = Streams.readBytes(is);
				return codec.decode(bytes);
			}
			throw Lang.makeThrow(IllegalArgumentException.class, "UnSupport Content-Type : " + contentType);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}
}
