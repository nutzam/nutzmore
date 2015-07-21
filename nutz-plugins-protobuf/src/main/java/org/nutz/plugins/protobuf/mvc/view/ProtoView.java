package org.nutz.plugins.protobuf.mvc.view;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import com.googlecode.protobuf.format.HtmlFormat;
import com.googlecode.protobuf.format.XmlFormat;

@IocBean(name = "proto")
public class ProtoView implements View {

	private final static Log log = Logs.get();

	private final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
	private final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		String contentType = req.getContentType();
		if (obj instanceof Message) {
			Charset charset = Charset.forName(req.getCharacterEncoding());
			Message message = (Message) obj;
			OutputStream out = resp.getOutputStream();
			resp.addHeader(X_PROTOBUF_SCHEMA_HEADER, message.getDescriptorForType().getFile().getName());
			resp.addHeader(X_PROTOBUF_MESSAGE_HEADER, message.getDescriptorForType().getFullName());
			if (log.isDebugEnabled()) {
				log.debug(X_PROTOBUF_SCHEMA_HEADER + ":" + message.getDescriptorForType().getFile().getName());
				log.debug(X_PROTOBUF_MESSAGE_HEADER + ":" + message.getDescriptorForType().getFullName());
			}
			if (contentType.contains("text/html")) {
				final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, charset);
				HtmlFormat.print(message, outputStreamWriter);
				outputStreamWriter.flush();
			} else if (contentType.contains("application/json")) {
				final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, charset);
				com.googlecode.protobuf.format.JsonFormat.print(message, outputStreamWriter);
				outputStreamWriter.flush();
			} else if (contentType.contains("text/plain")) {
				final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, charset);
				TextFormat.print(message, outputStreamWriter);
				outputStreamWriter.flush();
			} else if (contentType.contains("application/xml")) {
				final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, charset);
				XmlFormat.print(message, outputStreamWriter);
				outputStreamWriter.flush();
			} else if (contentType.contains("x-protobuf")) {
				message.writeTo(out);
			}
		} else {
			Mvcs.write(resp, obj, JsonFormat.compact());
		}
	}

}
