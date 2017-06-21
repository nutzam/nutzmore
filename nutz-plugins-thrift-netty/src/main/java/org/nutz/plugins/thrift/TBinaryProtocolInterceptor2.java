package org.nutz.plugins.thrift;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

public class TBinaryProtocolInterceptor2 implements MethodInterceptor {

	private TTransport transport;
	private TProtocol protocol;

	public TBinaryProtocolInterceptor2(TProtocol protocol) {
		this.transport = protocol.getTransport();
		this.protocol = protocol;
	}

	protected static ThreadLocal<TProtocol> TL = new ThreadLocal<TProtocol>();

	public void filter(InterceptorChain chain) throws Throwable {
		if (TL.get() != null) {
			chain.doChain();
			return;
		}
		try {
			TL.set(protocol);
			transport.open();
			chain.doChain();
		} finally {
			TL.remove();
			transport.close();
		}
	}

	public static TProtocol protocol() {
		return TL.get();
	}
}
