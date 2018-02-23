package org.nutz.plugins.dict.chain;

import java.util.Map;

/**
 * select处理接口
 * @author 邓华锋 http://dhf.ink
 *
 */
public interface SelectProcessor {
	void process(String value,String text);
	void put(String key);
	void putGlobalDict(Map<String,Object> globalDictVal);
}
