package org.nutz.integration.zbus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

/**
 * 集成zbus
 * 
 * @author wendal
 *
 */
public class ZbusIocLoader extends JsonLoader {
    
    private static final Log log = Logs.get();
	
	public ZbusIocLoader(){}

	public ZbusIocLoader(String... pkgs) {
        super(new String[]{});
		for (String pkg : pkgs) {
			add(pkg);
		}
		_load("zbus-common.js");
		_load("zbus-rpc-invoker.js");
		_load("zbus-rpc-service.js");
		_load("zbus-server.js");
	}

	protected void add(String pkg) {
		for (Class<?> klass : Scans.me().scanPackage(pkg)) {
			ZBusFactory.addInovker(klass, getMap());
		}
	}

	@SuppressWarnings("unchecked")
	public void _load(String path) {
		InputStream ins = getClass().getClassLoader().getResourceAsStream("ioc/"+path);
		if (ins == null)
			ins = getClass().getResourceAsStream(path);
		if (ins == null) {
		    log.debugf("resource not found %s", path);
			return;
		}
		try {
			String s = Lang.readAll(new InputStreamReader(ins));
	        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
	        if (null != map && map.size() > 0)
	            getMap().putAll(map);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException)e;
			throw new RuntimeException("load fail , path="+path, e);
		} finally {
			Streams.safeClose(ins);
		}
	}
}
