package org.nutz.integration.zbus.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

import io.zbus.mq.Broker;
import io.zbus.rpc.Remote;
import io.zbus.transport.ServerAddress;

@IocBean(name="zbus_invoker_factory", create="init")
public class ZbusClientBean implements IocLoader {

	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;
	
	protected Map<String, IocObject> iobjs = new ConcurrentHashMap<>();
	
	public void init() {
		if (!conf.has("zbus.rpc.client.packageNames")) {
			return;
		}
		boolean mqMode = "mq".equals(conf.get("zbus.rpc.client.mode", "mq"));
		for (String pkgName : Strings.splitIgnoreBlank(conf.get("zbus.rpc.client.packageNames", ""))) {
			for (Class<?> klass : Scans.me().scanPackage(pkgName)) {
				if (klass.isInterface() && klass.getAnnotation(Remote.class) != null) {
					IocObject iobj = new IocObject();
					iobj.setType(klass);
					if (mqMode) {
						iobj.setFactory("$zbus_invoker_factory#createMqInvoker");
						iobj.addArg(new IocValue(IocValue.TYPE_REFER, "zbusMqClientBootstrap"));
						iobj.addArg(new IocValue(IocValue.TYPE_NORMAL, klass.getName()));
					} else {
						iobj.setFactory("$zbus_invoker_factory#createHttpInvoker");
						iobj.addArg(new IocValue(IocValue.TYPE_REFER, "zbusHttpClientBootstrap"));
						iobj.addArg(new IocValue(IocValue.TYPE_NORMAL, klass.getName()));
					}
					iobjs.put(Strings.lowerFirst(klass.getSimpleName()), iobj);
				}
			}
		}
	}
	
	@IocBean(name="zbusHttpClientBootstrap", depose="close")
	public io.zbus.rpc.bootstrap.http.ClientBootstrap createHttpClientBootstrap() {
		io.zbus.rpc.bootstrap.http.ClientBootstrap bootstrap = new io.zbus.rpc.bootstrap.http.ClientBootstrap();
		ServerAddress serverAddress = new ServerAddress();
		if (conf.has("zbus.rpc.client.serviceToken")) {
			bootstrap.serviceToken(conf.get("zbus.rpc.client.serviceToken"));
			serverAddress.token = conf.get("zbus.rpc.client.serviceToken");
		}
		if (conf.has("zbus.rpc.client.clientPoolSize")) {
			bootstrap.clientPoolSize(conf.getInt("zbus.rpc.client.clientPoolSize"));
		}
		if (conf.has("zbus.rpc.client.serviceName")) {
			//bootstrap.serviceName(conf.get("zbus.rpc.client.serviceName"));
		}

		if (conf.has("zbus.rpc.client.serverAddress")) {
			serverAddress.address = conf.get("zbus.rpc.client.serverAddress");
		}
		if (conf.has("zbus.rpc.client.sslEnabled")) {
			serverAddress.sslEnabled = conf.getBoolean("zbus.rpc.client.sslEnabled");
		}
		if (conf.has("zbus.rpc.client.certificate")) {
			serverAddress.certificate = conf.get("zbus.rpc.client.certificate");
		}
		bootstrap.serviceAddress(serverAddress);
		return bootstrap;
	}
	
	@IocBean(name="zbusMqClientBootstrap", depose="close")
	public io.zbus.rpc.bootstrap.mq.ClientBootstrap createMqClientBootstrap(@Inject("refer:zbusBroker") Broker broker) {
		io.zbus.rpc.bootstrap.mq.ClientBootstrap bootstrap = new io.zbus.rpc.bootstrap.mq.ClientBootstrap();
		bootstrap.serviceName(conf.check("zbus.rpc.client.serviceName"));
		if (conf.has("zbus.rpc.client.serviceToken")) {
			bootstrap.serviceToken(conf.get("zbus.rpc.client.serviceToken"));
		}
		if (conf.has("zbus.rpc.client.serviceName")) {
			bootstrap.serviceName(conf.get("zbus.rpc.client.serviceName"));
		}
		bootstrap.broker(broker);
		return bootstrap;
	}
	
	public Object createMqInvoker(io.zbus.rpc.bootstrap.mq.ClientBootstrap bootstrap, String klassName) {
		return bootstrap.invoker().createProxy(Lang.loadClassQuite(klassName));
	}
	

	public Object createHttpInvoker(io.zbus.rpc.bootstrap.http.ClientBootstrap bootstrap, String klassName) {
		return bootstrap.invoker().createProxy(Lang.loadClassQuite(klassName));
	}

	public String[] getName() {
		return iobjs.keySet().toArray(new String[iobjs.size()]);
	}

	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		IocObject iobj = iobjs.get(name);
        if (null == iobj)
            throw new ObjectLoadException("Object '" + name + "' without define!");
		return iobj;
	}

	public boolean has(String name) {
		return iobjs.containsKey(name);
	}
}
