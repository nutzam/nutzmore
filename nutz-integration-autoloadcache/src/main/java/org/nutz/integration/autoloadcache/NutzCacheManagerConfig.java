package org.nutz.integration.autoloadcache;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;

public class NutzCacheManagerConfig {

	private Ioc ioc;

	public Ioc getIoc() {
		if (this.ioc == null) {
			this.ioc = Mvcs.getIoc();
		}
		return this.ioc;
	}
}
