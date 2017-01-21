package org.nutz.plugins.sigar;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-sigar
 *
 * @file SigarIocLoader.java
 *
 * @description
 *
 * @time 2016年8月29日 下午11:15:12
 *
 */
public class SigarIocLoader extends JsonLoader {
	public SigarIocLoader() {
		_load("sigar.js", "sigar");
	}

	public SigarIocLoader(String... args) {
		String confName = args.length > 0 ? args[0] : "sigar";
		_load("sigar.js", confName);
	}

	@SuppressWarnings("unchecked")
	public void _load(String path, String confName) {
		InputStream ins = getClass().getClassLoader().getResourceAsStream("ioc/" + path);
		if (ins == null)
			ins = getClass().getResourceAsStream(path);
		if (ins == null)
			return;
		try {
			String s = Lang.readAll(new InputStreamReader(ins));
			s = s.replace("@confName", confName);
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
