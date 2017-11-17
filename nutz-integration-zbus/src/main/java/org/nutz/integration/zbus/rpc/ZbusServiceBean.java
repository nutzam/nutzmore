package org.nutz.integration.zbus.rpc;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

import io.zbus.mq.BrokerConfig;
import io.zbus.rpc.Remote;

@IocBean
public class ZbusServiceBean {
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject("refer:$ioc")
	protected Ioc ioc;

	@IocBean(name="zbusHttpServiceBootstrap", depose="close")
	public io.zbus.rpc.bootstrap.http.ServiceBootstrap createHttpServiceBootstrap() {
		io.zbus.rpc.bootstrap.http.ServiceBootstrap bootstrap = new io.zbus.rpc.bootstrap.http.ServiceBootstrap();
		if (conf.has("zbus.rpc.service.port")) {
			bootstrap.port(conf.getInt("zbus.rpc.service.port"));
		}
		if (conf.has("zbus.rpc.service.host")) {
			bootstrap.host(conf.get("zbus.rpc.service.host"));
		}
		if (conf.has("zbus.rpc.service.certFile")) {
			bootstrap.ssl(conf.get("zbus.rpc.service.certFile"), conf.get("zbus.rpc.service.keyFile"));
		}
		if (conf.has("zbus.rpc.service.token")) {
			bootstrap.serviceToken(conf.get("zbus.rpc.service.serviceToken"));
		}
		if (conf.has("zbus.rpc.service.responseTypeInfo")) {
			bootstrap.responseTypeInfo(conf.getBoolean("zbus.rpc.service.responseTypeInfo"));
		}
		if (conf.has("zbus.rpc.service.verbose")) {
			bootstrap.verbose(conf.getBoolean("zbus.rpc.service.verbose"));
		}
		bootstrap.autoDiscover(false); // 基于File的查找机制... 只能禁用
		for (String pkgName : Strings.splitIgnoreBlank(conf.get("zbus.rpc.service.packageNames", ""))) {
			for (Class<?> klass : Scans.me().scanPackage(pkgName)) {
				if (klass.getAnnotation(IocBean.class) != null && klass.getAnnotation(Remote.class) != null) {
					bootstrap.addModule(ioc.get(klass));
				}
			}
		}
		return bootstrap;
	}
	
	@IocBean(name="zbusMqServiceBootstrap", depose="close")
	public io.zbus.rpc.bootstrap.mq.ServiceBootstrap createMqServiceBootstrap() {
		io.zbus.rpc.bootstrap.mq.ServiceBootstrap bootstrap = new io.zbus.rpc.bootstrap.mq.ServiceBootstrap();

		bootstrap.serviceName(conf.check("zbus.rpc.service.serviceName"));
		
		if (conf.has("zbus.rpc.service.port")) {
			bootstrap.port(conf.getInt("zbus.rpc.service.port"));
			if (conf.has("zbus.rpc.service.host")) {
				bootstrap.host(conf.get("zbus.rpc.service.host"));
			}
		} else {
			Mirror.me(bootstrap).setValue(bootstrap, "brokerConfig", ioc.get(BrokerConfig.class, "zbusBrokerConfig"));
		}
		
		if (conf.has("zbus.rpc.service.certFile")) {
			bootstrap.ssl(conf.get("zbus.rpc.service.certFile"), conf.get("zbus.rpc.service.keyFile"));
		}
		if (conf.has("zbus.rpc.service.token")) {
			bootstrap.serviceToken(conf.get("zbus.rpc.service.serviceToken"));
		}
		if (conf.has("zbus.rpc.service.responseTypeInfo")) {
			bootstrap.responseTypeInfo(conf.getBoolean("zbus.rpc.service.responseTypeInfo"));
		}
		if (conf.has("zbus.rpc.service.verbose")) {
			bootstrap.verbose(conf.getBoolean("zbus.rpc.service.verbose"));
		}
		
		bootstrap.autoDiscover(false); // 基于File的查找机制... 只能禁用
		for (String pkgName : Strings.splitIgnoreBlank(conf.get("zbus.rpc.service.packageNames", ""))) {
			for (Class<?> klass : Scans.me().scanPackage(pkgName)) {
				if (klass.getAnnotation(IocBean.class) != null && klass.getAnnotation(Remote.class) != null) {
					bootstrap.addModule(ioc.get(klass));
				}
			}
		}
		return bootstrap; 
	}
	
	@IocBean(name="zbusServiceBootstrap")
	public ZbusServiceBootstrap createZBusServiceBootstrap() {
		ZbusServiceBootstrap bootstrap = new ZbusServiceBootstrap();
		if ("mq".equals(conf.get("zbus.rpc.service.mode", "mq"))) {
			bootstrap.mq = ioc.get(io.zbus.rpc.bootstrap.mq.ServiceBootstrap.class, "zbusMqServiceBootstrap");
		} else {
			bootstrap.http = ioc.get(io.zbus.rpc.bootstrap.http.ServiceBootstrap.class, "zbusHttpServiceBootstrap");
		}
		return bootstrap;
	}
}
