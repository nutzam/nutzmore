package org.nutz.plugins.thrift;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * @author rekoe
 *
 */
public class ThriftIocLoader extends JsonLoader {

	public ThriftIocLoader() {
		super(new String[] {});
		_load("thrift.js");
	}

	public ThriftIocLoader(String... args) {
		super(new String[] {});
		_load("thrift.js");
	} 

	@SuppressWarnings("unchecked")
	public void _load(String path) {
		InputStream ins = getClass().getClassLoader().getResourceAsStream("ioc/" + path);
		if (ins == null)
			ins = getClass().getResourceAsStream(path);
		if (ins == null)
			return;
		try {
			String s = Lang.readAll(new InputStreamReader(ins));
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
			if (null != map && map.size() > 0)
				getMap().putAll(map);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			throw new RuntimeException("load fail , path=" + path, e);
		} finally {
			Streams.safeClose(ins);
		}
	}
}
