package org.nutz.plugins.thrift.netty.demo.server;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.thrift.netty.demo.client.Echo;
import org.nutz.plugins.thrift.netty.server.context.ThreadContext;
import org.nutz.plugins.thrift.netty.server.transport.TNettyTransportContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rekoe
 *
 */
@IocBean
public class EchoImpl implements Echo {

	private static final Logger logger = LoggerFactory.getLogger(EchoImpl.class);

	@Override
	public String echo(String info) { 
		TNettyTransportContext context = ThreadContext.getTransportContext();
		logger.info("Echo from " + context.getRemoteAddress());
		return info;
	}

	@Override
	public void close() {
	}

}
