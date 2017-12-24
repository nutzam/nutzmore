package org.nutz.plugins.proxy.intercept;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

public class JollychicIntercept extends HttpProxyIntercept {

	private boolean isGzip = false;
	private ByteBuf contentBuf;

	@Override
	public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {
			if ("gzip".equalsIgnoreCase(httpResponse.headers().get(HttpHeaderNames.CONTENT_ENCODING))) {
				isGzip = true;
				pipeline.reset3();
				proxyChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
				proxyChannel.pipeline().fireChannelRead(httpResponse);
			} else {
				if (isGzip) {
					httpResponse.headers().set(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.GZIP);
				}
				contentBuf = PooledByteBufAllocator.DEFAULT.buffer();
			}
			pipeline.getDefault().afterResponse(clientChannel, proxyChannel, httpResponse, pipeline);
			//pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
	}

	@Override
	public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpContent httpContent, HttpProxyInterceptPipeline pipeline) throws Exception {
			try {
				contentBuf.writeBytes(httpContent.content());
				if (httpContent instanceof LastHttpContent) {
					String contentStr = contentBuf.toString(Charset.defaultCharset());
					contentBuf.clear().writeBytes(contentStr.getBytes());
					HttpContent hookHttpContent = new DefaultLastHttpContent();
					if (isGzip) {
						byte[] temp = new byte[contentBuf.readableBytes()];
						contentBuf.readBytes(temp);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						GZIPOutputStream outputStream = new GZIPOutputStream(baos);
						outputStream.write(temp);
						outputStream.finish();
						hookHttpContent.content().writeBytes(baos.toByteArray());
					} else {
						hookHttpContent.content().writeBytes(contentBuf);
					}
					pipeline.getDefault().afterResponse(clientChannel, proxyChannel, hookHttpContent, pipeline);
				}
			} finally {
				ReferenceCountUtil.release(httpContent);
			}
			//pipeline.afterResponse(clientChannel, proxyChannel, httpContent);
	}

}
