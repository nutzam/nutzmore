package org.nutz.plugins.event;

import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author gongqin@dhgate.com
 * @varsion 2017-5-16
 */
public class TestBean {
	private Log log = Logs.get();
	private Ioc ioc;

	public void init() {
		log.debug(ioc);
	}
}
