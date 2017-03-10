package org.nutz.plugins.thrift.netty.server.transport;

import java.net.SocketAddress;

/**
 * @author rekoe
 *
 */
public interface TNettyTransportContext {

	public SocketAddress getRemoteAddress();
}
