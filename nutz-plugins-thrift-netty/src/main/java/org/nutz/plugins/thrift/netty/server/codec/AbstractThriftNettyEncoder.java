package org.nutz.plugins.thrift.netty.server.codec;

import org.nutz.plugins.thrift.netty.server.transport.TNettyTransport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author rekoe
 *
 */
public abstract class AbstractThriftNettyEncoder extends MessageToByteEncoder<TNettyTransport> {

	@Override
	protected void encode(ChannelHandlerContext ctx, TNettyTransport msg, ByteBuf out) throws Exception {
		ByteBuf message = encode(ctx, msg);
		try {
			out.writeBytes(message);
		} finally {
			message.release();
		}
	}

	protected abstract ByteBuf encode(ChannelHandlerContext ctx, TNettyTransport msg) throws Exception;
}
