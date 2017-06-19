package org.nutz.plugins.event;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;

/**
 * @author gongqin@dhgate.com
 * @varsion 2017-5-15
 */
public class JvmEventBusTest {

	// 同步事件处理
	@Test
	public void testSyncBase() throws Exception {
		Ioc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.event", "*org.nutz.plugins.event.EventIocLoader"));
		EventBus eventBus = ioc.get(EventBus.class, "eventBus");
		for (int i = 0; i < 4; i++) {
			Event e1 = new Event("log-event", i);
			eventBus.fireEvent(e1);
		}
		ioc.depose();
	}

	// 异步事件处理
	@Test
	public void testAsyncBase() throws Exception {
		Ioc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.event", "*async", "2", "*org.nutz.plugins.event.EventIocLoader"));
		EventBus eventBus = ioc.get(EventBus.class, "eventBus");
		for (int i = 0; i < 4; i++) {
			Event e1 = new Event("log-event", i);
			eventBus.fireEvent(e1);
		}
		Lang.sleep(5 * 1000);
		ioc.depose();
	}

}
