package org.nutz.plugins.protobuf.mvc.adaptor;

import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

import com.google.protobuf.Message;

/**
 * 根据 HTTP 参数表，生成一个 POJO 对象
 * 
 * @author rekoe(koukou900@qq.com)
 */
public class ProtobufPairInjector implements ParamInjector {

	public ProtobufPairInjector(Type type) {
		Class<?> clazz = Lang.getTypeClass(type);
		if (!Message.class.isAssignableFrom(clazz))
			throw Lang.makeThrow("Can not accept Class, type '%s'", clazz.getName());
	}

	public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
		return refer;
	}

}
