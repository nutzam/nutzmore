package org.nutz.integration.nettice.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 响应处理工具类
 */
public class HttpRenderUtil {
	
	private static String CONTENT_TYPE = "Content-Type";
	private static String CONTENT_LENGTH = "Conteng-Length";
	
	/**
	 * 输出纯Json字符串
	 */
	public static FullHttpResponse renderJSON(String json){
		return render(json, "text/x-json;charset=UTF-8");
	}
	
	/**
	 * 输出纯字符串
	 */
	public static FullHttpResponse renderText(String text) {
		return render(text, "text/plain;charset=UTF-8");
	}
	
	/**
	 * 输出纯XML
	 */
	public static FullHttpResponse renderXML(String xml) {
		return render(xml, "text/xml;charset=UTF-8");
	}
	
	/**
	 * 输出纯HTML
	 */
	public static FullHttpResponse renderHTML(String html) {
		return render(html, "text/html;charset=UTF-8");
	}
	
	public static FullHttpResponse getErroResponse(){
		return render("Server error", "text/plain;charset=UTF-8");
	}
	
	public static FullHttpResponse getNotFoundResponse(){
		return render("Can not find specified action for name", "text/plain;charset=UTF-8");
	}
	
	/**
	 * response输出
	 * @param text
	 * @param contentType
	 */
	public static FullHttpResponse render(String text, String contentType){
		if(text == null){
			text = "";
		}
		ByteBuf byteBuf = Unpooled.wrappedBuffer(text.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
		response.headers().add(CONTENT_TYPE, contentType);
		response.headers().add(CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		return response;
	}

}
