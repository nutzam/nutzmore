package org.nutz.integration.zbus.broker;

import java.io.IOException;
import java.util.Map;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.zbus.kit.FileKit;
import io.zbus.kit.StrKit;
import io.zbus.mq.Broker;
import io.zbus.mq.BrokerConfig;
import io.zbus.transport.ServerAddress;

/**
 * 负责生成MQ所需要的BrokerConfig和Broker
 * @author wendal
 *
 */
@IocBean
public class ZbusBrokerBean {
	
	private static final Log log = Logs.get();

	@Inject
	protected PropertiesProxy conf;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@IocBean(name="zbusBrokerConfig")
	public BrokerConfig createBrokerConfig() throws IOException {
		BrokerConfig brokerConfig = new BrokerConfig();
		// zbus.broker.readyTimeout
		if (conf.has("zbus.broker.readyTimeout")) {
			brokerConfig.setReadyTimeout(conf.getInt("zbus.broker.readyTimeout"));
			log.debug("zbus.broker.readyTimeout=" + brokerConfig.getReadyTimeout());
		}
		// zbus.broker.clientPoolSize
		if (conf.has("zbus.broker.clientPoolSize")) {
			brokerConfig.setClientPoolSize(conf.getInt("zbus.broker.clientPoolSize"));
			log.debug("zbus.broker.clientPoolSize=" + brokerConfig.getClientPoolSize());
		}
		// zbus.trackerList
		Map<String, Object> trackerList = Lang.filter((Map)conf.toMap(), "zbus.broker.trackerList", null, null, null);
        if (trackerList.isEmpty()) {
            log.debug("add zbus default tracker 127.0.0.1:15555");
            trackerList.put("zbus.broker.trackerList.default.address", "127.0.0.1:15555");
        }
		for (Map.Entry<String, Object> en : trackerList.entrySet()) {
			String key = en.getKey();
			if (!key.endsWith(".address")) {
				continue;
			}
			String name = key.substring(0, key.length() - ".address".length());
			String address = en.getValue().toString();
			boolean sslEnabled = "true".equals(String.valueOf(trackerList.get(name + ".sslEnabled")));  
		    String certFile = String.valueOf(trackerList.get(name + ".certFile"));
		    if(StrKit.isEmpty(address)) continue;
		    

			log.debugf("add tracker: name=%s address=%s sslEnabled=%s", name, address, sslEnabled);
		    
		    ServerAddress serverAddress = new ServerAddress(address, sslEnabled);
		    if(serverAddress.isSslEnabled()){
		    	String certificate = FileKit.loadFile(certFile);
		    	serverAddress.setCertificate(certificate);
		    }
		    brokerConfig.addTracker(serverAddress);
		}
		return brokerConfig;
	}
	
	@IocBean(name="zbusBroker")
	public Broker createBroker(@Inject BrokerConfig brokerConfig) {
		return new Broker(brokerConfig);
	}
}
