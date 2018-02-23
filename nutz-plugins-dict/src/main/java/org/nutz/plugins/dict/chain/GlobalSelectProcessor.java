package org.nutz.plugins.dict.chain;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局 常用的
 * @author 邓华锋 http://dhf.ink
 *
 */
public class GlobalSelectProcessor implements SelectProcessor {
	public static Map<String, Map<String, String>> dictSelectMap = new LinkedHashMap<String, Map<String, String>>();

	Map<String, String> dictMap = new LinkedHashMap<String, String>();

	@Override
	public void process(String value, String text) {
		dictMap.put(value, text);
	}

	@Override
	public void put(String key) {
		dictSelectMap.put(key, dictMap);
		dictMap = new LinkedHashMap<String, String>();
	}

	@Override
	public void putGlobalDict(Map<String,Object> globalDictVal) {
		globalDictVal.put("globalSelect",dictSelectMap);
	}
	
}
