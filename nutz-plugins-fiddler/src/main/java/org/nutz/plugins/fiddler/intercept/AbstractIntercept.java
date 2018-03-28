package org.nutz.plugins.fiddler.intercept;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

public abstract class AbstractIntercept extends HttpProxyIntercept {

	private boolean isMatch = false;
	private boolean isGzip = false;

	private ByteBuf contentBuf;

	@Override
	public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) throws Exception {
		//HttpHeaders header = httpRequest.headers();
		//header.set(HttpHeaderNames.USER_AGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
		if (log.isDebugEnabled()) {
			log.debugf("resquest url : \n\t %s", httpRequest.uri());
		}
		pipeline.beforeRequest(clientChannel, httpRequest);
	}

	@Override
	public void beforeRequest(Channel clientChannel, HttpContent httpContent, HttpProxyInterceptPipeline pipeline) throws Exception {
		HttpHeaders header = pipeline.getHttpRequest().headers();
		if (match(pipeline.getHttpRequest())) {
			isMatch = true;
			if ("gzip".equalsIgnoreCase(header.get(HttpHeaderNames.CONTENT_ENCODING))) {
				isGzip = true;
			}
			if (log.isDebugEnabled()) {
				String content = httpContent.copy().content().toString(Charset.forName("utf8"));
				System.out.println(content);
				log.debugf("PostBody :  \n\t %s ", content);
			}
		}
		super.beforeRequest(clientChannel, httpContent, pipeline);
	}

	@Override
	public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {
		if (isMatch) {
			if (isGzip) {
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
		} else {
			isMatch = false;
			pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
		}
	}

	@Override
	public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpContent httpContent, HttpProxyInterceptPipeline pipeline) throws Exception {
		if (isMatch) {
			try {
				contentBuf.writeBytes(httpContent.content());
				if (httpContent instanceof LastHttpContent) {
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
					if (log.isDebugEnabled()) {
						log.debugf("response : \n\t %s", hookHttpContent.content().copy().toString(Charset.forName("utf8")));
					}
					pipeline.getDefault().afterResponse(clientChannel, proxyChannel, hookHttpContent, pipeline);
				}
			} finally {
				ReferenceCountUtil.release(httpContent);
			}
		} else {
			pipeline.afterResponse(clientChannel, proxyChannel, httpContent);
		}
	}

	private final static Log log = Logs.get();

	public abstract boolean match(HttpRequest httpRequest);
}
