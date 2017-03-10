package org.nutz.plugins.thrift.netty.server.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author rekoe
 *
 */
public abstract class AbstractThriftNettyDecoder<T> extends ByteToMessageDecoder {

	protected static final int DEFAULT_FRAME_SIZE = 4;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		T obj = decode(ctx, in);
		if (obj != null) {
			out.add(obj);
		}
	}

	protected abstract T decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception;

}
