package org.nutz.plugins.thrift.netty.server.transport;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.PlatformDependent;

/**
 * @author rekoe
 *
 */
public class TNettyTransport extends TTransport implements ReferenceCounted, TNettyTransportContext {

	private static final int DEFAULT_OUTPUT_BUFFER_SIZE = 1024;
	private static final AtomicIntegerFieldUpdater<TNettyTransport> refCntUpdater;

	public static enum Type {
		UNKNOWN, UNFRAMED, FRAMED
	}

	static {
		AtomicIntegerFieldUpdater<TNettyTransport> updater = PlatformDependent
				.newAtomicIntegerFieldUpdater(TNettyTransport.class, "refCnt");
		if (updater == null) {
			updater = AtomicIntegerFieldUpdater.newUpdater(TNettyTransport.class, "refCnt");
		}
		refCntUpdater = updater;
	}

	private Channel channel;
	private ByteBuf in;
	private ByteBuf out = null;
	private Type type;

	private volatile int refCnt = 1;

	public TNettyTransport(Channel channel, ByteBuf in) {
		this(channel, in, Type.UNKNOWN);
	}

	public TNettyTransport(Channel channel, ByteBuf in, Type type) {
		this.channel = channel;
		this.in = in;
		this.in.retain();
		this.type = type;
		this.out = channel.alloc().heapBuffer(DEFAULT_OUTPUT_BUFFER_SIZE);
	}

	public Type getType() {
		return type;
	}

	public ByteBuf getOut() {
		return out;
	}
	
	@Override
	public SocketAddress getRemoteAddress() {
		return this.channel.remoteAddress();
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void open() throws TTransportException {
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws TTransportException {
		int _read = Math.min(in.readableBytes(), length);
		in.readBytes(bytes, offset, _read);
		return _read;
	}

	@Override
	public void write(byte[] bytes, int offset, int length) throws TTransportException {
		out.writeBytes(bytes, offset, length);
	}

	@Override
	public int refCnt() {
		return refCnt;
	}

	@Override
	public ReferenceCounted retain() {
		for (;;) {
			int refCnt = this.refCnt;
			if (refCnt == 0) {
				throw new IllegalReferenceCountException(0, 1);
			}
			if (refCnt == Integer.MAX_VALUE) {
				throw new IllegalReferenceCountException(Integer.MAX_VALUE, 1);
			}
			if (refCntUpdater.compareAndSet(this, refCnt, refCnt + 1)) {
				break;
			}
		}
		return this;
	}

	@Override
	public ReferenceCounted retain(int increment) {
		if (increment <= 0) {
			throw new IllegalArgumentException("increment: " + increment + " (expected: > 0)");
		}

		for (;;) {
			int refCnt = this.refCnt;
			if (refCnt == 0) {
				throw new IllegalReferenceCountException(0, increment);
			}
			if (refCnt > Integer.MAX_VALUE - increment) {
				throw new IllegalReferenceCountException(refCnt, increment);
			}
			if (refCntUpdater.compareAndSet(this, refCnt, refCnt + increment)) {
				break;
			}
		}
		return this;
	}

	@Override
	public boolean release() {
		for (;;) {
			int refCnt = this.refCnt;
			if (refCnt == 0) {
				throw new IllegalReferenceCountException(0, -1);
			}

			if (refCntUpdater.compareAndSet(this, refCnt, refCnt - 1)) {
				if (refCnt == 1) {
					deallocate();
					return true;
				}
				return false;
			}
		}
	}

	@Override
	public boolean release(int decrement) {
		if (decrement <= 0) {
			throw new IllegalArgumentException("decrement: " + decrement + " (expected: > 0)");
		}

		for (;;) {
			int refCnt = this.refCnt;
			if (refCnt < decrement) {
				throw new IllegalReferenceCountException(refCnt, -decrement);
			}

			if (refCntUpdater.compareAndSet(this, refCnt, refCnt - decrement)) {
				if (refCnt == decrement) {
					deallocate();
					return true;
				}
				return false;
			}
		}
	}

	private void deallocate() {
		in.release();
		out.release();
	}

}
