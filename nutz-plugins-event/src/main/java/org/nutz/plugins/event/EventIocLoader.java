package org.nutz.plugins.event;

import java.io.StringReader;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.meta.IocObject;

/**
 * Event Ioc加载器.
 * 启动后会创建名称为 eventBus 的对象。
 * 可根据参数动态决定是创建基于jvm的单机事件处理，还是基于redis的分布式事件处理。默认为jvm方式
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-15
 */
public class EventIocLoader implements IocLoader {
	
	private JsonLoader loader;

	/**
	 * 定义事件处理器bean对象, 默认使用进程内事件处理器
	 */
	public EventIocLoader() {
		this("jvm");
	}

	/**
	 * 定义事件处理器bean对象
	 * @param busType 事件总线类型，目前支持 jvm 和 redis 
	 */
	public EventIocLoader(String busType) {
		String json = "{eventBus:{type:'org.nutz.plugins.event.impl.JvmEventBus',events:{create:'init', depose:'depose'},fields:{'ioc':{refer:'$ioc'}}}}";
		if ("redis".equalsIgnoreCase(busType)) {
			json = "{eventBus:{type:'org.nutz.plugins.event.impl.RedisEventBus',events:{create:'init', depose:'depose'},fields:{'ioc':{refer:'$ioc'}, 'redisService':{refer:'redisService'}}}}";
		}
		loader = new JsonLoader( new StringReader(json));
	}

	@Override
	public String[] getName() {
		return loader.getName();
	}

	@Override
	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		return loader.load(loading, name);
	}

	@Override
	public boolean has(String name) {
		return loader.has(name);
	}
}
