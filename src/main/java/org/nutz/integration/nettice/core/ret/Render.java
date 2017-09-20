package org.nutz.integration.nettice.core.ret;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import org.nutz.integration.nettice.core.Return;
import org.nutz.integration.nettice.core.utils.HttpRenderUtil;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 字符型的输出
 */
public class Render implements Return {

	private final static Log logger = Logs.get();

	private String data;
	public RenderType renderType;

	public Render(RenderType renderType, String data) {
		this.data = data;
		this.renderType = renderType;
	}

	public FullHttpResponse process() throws Exception {
		FullHttpResponse response;
		switch (renderType) {
		case JSON:
			response = HttpRenderUtil.renderJSON(data);
			break;
		case TEXT:
			response = HttpRenderUtil.renderText(data);
			break;
		case XML:
			response = HttpRenderUtil.renderXML(data);
			break;
		case HTML:
			response = HttpRenderUtil.renderHTML(data);
			break;
		default:
			response = HttpRenderUtil.getErroResponse();
			logger.error("unkown render type");
		}
		return response;
	}

}
