package org.nutz.plugins.ioc.loader;

import org.nutz.ioc.impl.PropertiesProxy;

public class TestThreadIocLoader {
	public static void main(String[] args) {
		//ThreadIocLoader.getIoc();
		//IocLoader daoIocLoader=new DaoIocLoader("dao","t_iocbean","nm","val");
		//ThreadIocLoader.comboIocLoader.addLoader(daoIocLoader);
		System.out.println(ThreadIocLoader.getIoc().get(PropertiesProxy.class, "config").get("db.url"));
	}
}
