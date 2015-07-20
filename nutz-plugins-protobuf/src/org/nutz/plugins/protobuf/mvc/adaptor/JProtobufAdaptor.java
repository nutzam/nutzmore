package org.nutz.plugins.protobuf.mvc.adaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream ins = request.getInputStream();
			byte[] buf = new byte[8192];
			int len = 0;
			while (-1 != (len = ins.read(buf))) {
				   baos.write(buf, 0, len);
			}
			return codec.decode(baos.toByteArray());
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}
}
