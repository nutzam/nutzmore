package org.nutz.plugins.dict.chain;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * jqGrid编辑表时
 * @author 邓华锋 http://dhf.ink
 *
 */
public class EditableSelectProcessor implements SelectProcessor {
	public static Map<String, List<Map<String, String>>> editableSelectMap = new LinkedHashMap<String, List<Map<String, String>>>();
	List<Map<String, String>> row = new LinkedList<Map<String, String>>();

	@Override
	public void process(String value, String text) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("id", value);
		map.put("text", text);
		row.add(map);
	}

	@Override
	public void put(String key) {
		editableSelectMap.put(key, row);
		row= new LinkedList<Map<String, String>>();
	}

	@Override
	public void putGlobalDict(Map<String,Object> globalDictVal) {
		globalDictVal.put("editableSelect",editableSelectMap);
	}
}
