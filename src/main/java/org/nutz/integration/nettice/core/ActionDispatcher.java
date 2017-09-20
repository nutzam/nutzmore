package org.nutz.integration.nettice.core;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import org.nutz.integration.nettice.core.config.ActionWrapper;
import org.nutz.integration.nettice.core.invocation.ActionProxy;
import org.nutz.integration.nettice.core.utils.HttpRenderUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;

/**
 * Action 分发器，收到 request 后不做实际业务处理，而是组装 action 并交给处理。
 */
public class ActionDispatcher extends ChannelInboundHandlerAdapter {

	private final static Log log = Logs.get();

	private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
	private static final String CONNECTION_CLOSE = "close";

	protected static RouterContext routerContext;
	private HttpRequest request;
	private FullHttpResponse response;
	private Channel channel;

	public ActionDispatcher() {

	}

	public void init(String configFilePath, String suffix) throws Exception {
		if (configFilePath == null) {
			configFilePath = "router.xml";
		}
		if (Strings.isBlank(suffix)) {
			routerContext = new RouterContext(configFilePath);
		} else {
			routerContext = new RouterContext(configFilePath, suffix);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			channel = ctx.channel();
			request = (HttpRequest) msg;
			try {
				String path = getRequestPath();
				ActionWrapper actionWrapper = routerContext.getActionWrapper(path);
				if (actionWrapper == null) {
					response = HttpRenderUtil.getNotFoundResponse();
					writeResponse(true);
					if (log.isDebugEnabled())
						log.debugf("Search mapping path=%s : NOT Action match", path);
					return;
				}
				DataHolder.setRequest(request);
				ActionProxy proxy = routerContext.getActionProxy(actionWrapper);
				Return result = proxy.execute();
				if (result != null) {
					response = result.process();
				}
				writeResponse(false);
			} catch (Exception e) {
				response = HttpRenderUtil.getErroResponse();
				writeResponse(true);
			} finally {
				ReferenceCountUtil.release(msg);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private String getRequestPath() throws Exception {
		String uri = request.uri();
		if ("/favicon.ico".equals(uri)) {
			return uri;
		}
		int startIndex = uri.indexOf(routerContext.getSuffix());
		if (startIndex <= 0) {
			throw new Exception("request path error");
		}
		return uri.substring(0, startIndex + routerContext.getSuffix().length());
	}

	private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");

	private void writeResponse(boolean forceClose) {
		boolean close = isClose();
		if (!close && !forceClose) {
			response.headers().add(CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
		}
		ChannelFuture future = channel.write(response);
		if (close || forceClose) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private boolean isClose() {
		if (request.headers().contains(CONTENT_LENGTH, CONNECTION_CLOSE, true) || (request.protocolVersion().equals(HttpVersion.HTTP_1_0) && !request.headers().contains(CONTENT_LENGTH, CONNECTION_KEEP_ALIVE, true)))
			return true;
		return false;
	}

}
