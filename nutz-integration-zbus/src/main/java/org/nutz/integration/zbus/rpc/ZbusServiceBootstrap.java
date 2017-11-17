package org.nutz.integration.zbus.rpc;

public class ZbusServiceBootstrap {

	protected io.zbus.rpc.bootstrap.http.ServiceBootstrap http;
	protected io.zbus.rpc.bootstrap.mq.ServiceBootstrap mq;
	
	public void start() throws Exception {
		if (http != null)
			http.start();
		if (mq != null)
			mq.start();
	}
}
