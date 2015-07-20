package org.nutz.plugins.protobuf.mvc.adaptor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * 根据 HTTP 参数表，生成一个 POJO 对象
 * 
 * @author rekoe(koukou900@qq.com)
 */
public class JProtobufPairInjector implements ParamInjector {

	public JProtobufPairInjector(Type type) {
		boolean isRight = false;
		Class<?> clazz = Lang.getTypeClass(type);
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			Protobuf protobuf = field.getAnnotation(Protobuf.class);
			if (!Lang.isEmpty(protobuf)) {
				isRight = true;
				break;
			}
		}
		if (!isRight) {
			throw Lang.makeThrow(IllegalArgumentException.class, "Only Support One Message Type Class");
		}
	}

	public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
		return refer;
	}

}
