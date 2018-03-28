package org.nutz.plugins.dict.chain;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * jqgrid 处理
 * @author 邓华锋 http://dhf.ink
 *
 */
public class JqgridSelectProcessor implements SelectProcessor {
	public static Map<String, String> jqgridSelectMap = new LinkedHashMap<String, String>();
	private StringBuffer sb = new StringBuffer();

	@Override
	public void process(String value, String text) {
		sb.append(value);
		sb.append(":");
		sb.append(text);
		sb.append(";");
	}

	@Override
	public void put(String key) {
		String row=sb.toString();
		row=row.substring(0, row.lastIndexOf(";"));
		jqgridSelectMap.put(key, row);
		sb = new StringBuffer();
	}

	@Override
	public void putGlobalDict(Map<String, Object> globalDictVal) {
		globalDictVal.put("jqgridSelect", jqgridSelectMap);
	}
}
