package org.nutz.protobuf.mvc.view;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

import com.google.protobuf.Message;

@IocBean(name = "proto")
public class ProtoView implements View {

	private final static Log log = Logs.get();

	private final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
	private final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		if (obj instanceof Message) {
			Message message = (Message) obj;
			OutputStream out = resp.getOutputStream();
			resp.addHeader(X_PROTOBUF_SCHEMA_HEADER, message.getDescriptorForType().getFile().getName());
			resp.addHeader(X_PROTOBUF_MESSAGE_HEADER, message.getDescriptorForType().getFullName());
			if (log.isDebugEnabled()) {
				log.debug(X_PROTOBUF_SCHEMA_HEADER + ":" + message.getDescriptorForType().getFile().getName());
				log.debug(X_PROTOBUF_MESSAGE_HEADER + ":" + message.getDescriptorForType().getFullName());
			}
			Streams.writeAndClose(out, message.toByteArray());
		} else {
			Mvcs.write(resp, obj, JsonFormat.compact());
		}
	}

}
