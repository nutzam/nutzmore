package org.nutz.plugins.thrift.netty.server.codec;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;
import org.nutz.plugins.thrift.netty.server.transport.TNettyTransport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.TooLongFrameException;

/**
 * @author rekoe
 *
 */
public class ThriftNettyDecoder extends AbstractThriftNettyDecoder<TNettyTransport> {

	private final int maxFrameSize;

	private final TProtocolFactory inputProtocolFactory;

	public ThriftNettyDecoder(int maxFrameSize, TProtocolFactory inputProtocolFactory) {
		this.maxFrameSize = maxFrameSize;
		this.inputProtocolFactory = inputProtocolFactory;
	}

	@Override
	protected TNettyTransport decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		if (in.isReadable()) {
			short type = in.getUnsignedByte(0);
			if (type >= 0x80) {
				return tryDecodeUnframedMsg(ctx, in);
			} else if (in.readableBytes() >= DEFAULT_FRAME_SIZE) {
				return tryDecodeFramedMsg(ctx, in);
			}
		}
		return null;
	}

	private TNettyTransport tryDecodeFramedMsg(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		int messageStartReaderIndex = in.readerIndex();
		int messageContentsLength = in.getInt(messageStartReaderIndex);
		int messageLength = messageContentsLength + DEFAULT_FRAME_SIZE;
		if (messageContentsLength > maxFrameSize) {
			ctx.fireExceptionCaught(new TooLongFrameException("Maximum frame size of " + maxFrameSize + " exceeded"));
		}
		int messageContentsOffset = messageStartReaderIndex + DEFAULT_FRAME_SIZE;
		if (messageLength == 0) {
			in.readerIndex(messageContentsOffset);
		} else if (in.readableBytes() >= messageLength) {
			ByteBuf messageBuffer = extractFrame(in, messageContentsOffset, messageContentsLength);
			in.readerIndex(messageStartReaderIndex + messageLength);
			return new TNettyTransport(ctx.channel(), messageBuffer, TNettyTransport.Type.FRAMED);
		}
		return null;
	}

	private TNettyTransport tryDecodeUnframedMsg(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		int messageLength = 0;
		int messageStartReaderIndex = in.readerIndex();
		TNettyTransport decodeAttemptTransport = new TNettyTransport(ctx.channel(), in);
		try {
			TProtocol inputProtocol = this.inputProtocolFactory.getProtocol(decodeAttemptTransport);
			inputProtocol.readMessageBegin();
			TProtocolUtil.skip(inputProtocol, TType.STRUCT);
			inputProtocol.readMessageEnd();
			messageLength = in.readerIndex() - messageStartReaderIndex;
		} catch (IndexOutOfBoundsException e) {
			return null;
		} finally {
			decodeAttemptTransport.release();
			if (in.readerIndex() - messageStartReaderIndex > maxFrameSize) {
				ctx.fireExceptionCaught(
						new TooLongFrameException("Maximum frame size of " + maxFrameSize + " exceeded"));
			}
			in.readerIndex(messageStartReaderIndex);
		}
		if (messageLength <= 0) {
			return null;
		}
		ByteBuf messageBuffer = extractFrame(in, messageStartReaderIndex, messageLength);
		in.readerIndex(messageStartReaderIndex + messageLength);
		return new TNettyTransport(ctx.channel(), messageBuffer, TNettyTransport.Type.UNFRAMED);
	}

	protected ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
		return buffer.slice(index, length);
	}
}
