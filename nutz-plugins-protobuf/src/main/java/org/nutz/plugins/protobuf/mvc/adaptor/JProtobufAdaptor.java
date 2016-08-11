package org.nutz.plugins.protobuf.mvc.adaptor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.annotation.Param;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.google.protobuf.CodedInputStream;

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
		for (Field field : clazz.getDeclaredFields()) {
			Protobuf protobuf = field.getAnnotation(Protobuf.class);
			if (protobuf != null) {
				if (null == this.clazz) {
					this.clazz = clazz;
					return new JProtobufPairInjector();
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
			if (!contentType.contains("application/x-protobuf")) {
	            throw Lang.makeThrow(IllegalArgumentException.class, "UnSupport Content-Type : " + contentType);
			}
			if (this.clazz == null) {
				throw Lang.makeThrow(IllegalArgumentException.class, "Not Support Adaptor,You Must Have A Message Type Class ");
			}
			Codec<?> codec = ProtobufProxy.create(clazz);
			return codec.readFrom(CodedInputStream.newInstance(request.getInputStream()));
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}
}
