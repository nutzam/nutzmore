package org.nutz.plugins.protobuf.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.utils.StringUtils;
import com.google.protobuf.CodedOutputStream;

@IocBean(name = "jproto")
public class JProtoView implements View {

	private final static Log log = Logs.get();

	private final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";

	private final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";

	private final String DEL_STRING_CODE = "JProtoBufProtoClass";

	private final String CODE_SUFFIX = ".proto";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		Codec codec = ProtobufProxy.create(obj.getClass());
		String x_protobuf_message_header = StringUtils.substringBeforeLast(obj.getClass().getName(), DEL_STRING_CODE);
		String x_protobuf_schema_header = StringUtils.substringAfterLast(x_protobuf_message_header, ".");
		resp.addHeader(X_PROTOBUF_SCHEMA_HEADER, x_protobuf_schema_header.toLowerCase() + CODE_SUFFIX);
		resp.addHeader(X_PROTOBUF_MESSAGE_HEADER, x_protobuf_message_header);
		if (log.isDebugEnabled()) {
			log.debug(X_PROTOBUF_SCHEMA_HEADER + ":" + x_protobuf_schema_header.toLowerCase() + CODE_SUFFIX);
			log.debug(X_PROTOBUF_MESSAGE_HEADER + ":" + x_protobuf_message_header);
		}
		codec.writeTo(obj, CodedOutputStream.newInstance(resp.getOutputStream()));
	}

}
