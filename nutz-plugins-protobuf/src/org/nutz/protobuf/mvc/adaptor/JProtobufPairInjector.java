package org.nutz.protobuf.mvc.adaptor;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.ParamInjector;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

/**
 * 根据 HTTP 参数表，生成一个 POJO 对象
 * 
 * @author rekoe(koukou900@qq.com)
 */
public class JProtobufPairInjector implements ParamInjector {

	private final static Log log = Logs.get();
	protected Class<?> clazz;

	public JProtobufPairInjector(Type type) {
		this.clazz = Lang.getTypeClass(type);
	}

	public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
		Codec<?> codec = ProtobufProxy.create(clazz);
		try {
			return codec.decode(Streams.readBytesAndClose(req.getInputStream()));
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

}
