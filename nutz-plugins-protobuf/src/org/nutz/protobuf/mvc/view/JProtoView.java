package org.nutz.protobuf.mvc.view;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.mvc.View;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

@IocBean(name = "jproto")
public class JProtoView implements View {

	private final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";

	private final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";

	private final String DEL_STRING_CODE = "JProtoBufProtoClass";

	private final String CODE_SUFFIX = ".proto";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		Codec codec = ProtobufProxy.create(obj.getClass());
		String x_protobuf_message_header = StringUtils.remove(obj.getClass().getName(), DEL_STRING_CODE);
		String x_protobuf_schema_header = StringUtils.substringAfterLast(x_protobuf_message_header, ".");
		resp.addHeader(X_PROTOBUF_SCHEMA_HEADER, StringUtils.lowerCase(x_protobuf_schema_header) + CODE_SUFFIX);
		resp.addHeader(X_PROTOBUF_MESSAGE_HEADER, x_protobuf_message_header);
		OutputStream out = resp.getOutputStream();
		Streams.writeAndClose(out, codec.encode(obj));
	}

}
