package org.nutz.plugins.thrift.netty.server.codec;

import org.nutz.plugins.thrift.netty.server.transport.TNettyTransport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.TooLongFrameException;

/**
 * @author rekoe
 *
 */
public class ThriftNettyEncoder extends AbstractThriftNettyEncoder {

	private final int maxFrameSize;

	public ThriftNettyEncoder(int maxFrameSize) {
		this.maxFrameSize = maxFrameSize;
	}

	@Override
	protected ByteBuf encode(ChannelHandlerContext ctx, TNettyTransport msg) throws Exception {
		int frameSize = msg.getOut().readableBytes();
		if (msg.getOut().readableBytes() > maxFrameSize) {
			ctx.fireExceptionCaught(new TooLongFrameException(
					String.format("Frame size exceeded on encode: frame was %d bytes, maximum allowed is %d bytes",
							frameSize, maxFrameSize)));
			return null;
		}
		switch (msg.getType()) {
		case UNFRAMED:
			msg.getOut().retain();
			return msg.getOut();
		case FRAMED:
			ByteBuf sizeBuf = ctx.alloc().buffer(4);
			sizeBuf.writeInt(msg.getOut().readableBytes());
			msg.getOut().retain();
			return Unpooled.wrappedBuffer(sizeBuf, msg.getOut());
		default:
			throw new UnsupportedOperationException("Unrecognized transport type");
		}
	}

}
