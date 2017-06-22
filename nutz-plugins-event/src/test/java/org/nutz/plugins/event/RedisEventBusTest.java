package org.nutz.plugins.event;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;

/**
 * 基于redis的事件测试
 * @author gongqin@dhgate.com
 * @varsion 2017-5-16
 */
public class RedisEventBusTest {

	@Test
	public void redis() throws Exception {
		Ioc ioc = new NutIoc(new ComboIocLoader("*js", "redis.js", "*anno", "org.nutz.plugins.event", "*jedis", "*org.nutz.plugins.event.EventIocLoader", "redis"));
		EventBus eventBus = ioc.get(EventBus.class, "eventBus");
		for (int i = 0; i < 4; i++) {
			Event e1 = new Event("log-event", i);
			eventBus.fireEvent(e1);
		}

		Lang.sleep(10 * 1000);
		ioc.depose();
	}

}
