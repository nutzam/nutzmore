package org.nutz.shiro.ext.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
public class ConfigFun implements Function {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.beetl.core.Function#call(java.lang.Object[],
	 * org.beetl.core.Context)
	 */
	@Override
	public Object call(Object[] paras, Context ctx) {
		PropertiesProxy config = Mvcs.getIoc().get(PropertiesProxy.class, "config");
		if (paras == null) {
			return null;
		}
		switch (paras[0].toString()) {
		case "debug":
			return Strings.equalsIgnoreCase("localhost", Mvcs.getReq().getServerName())
					|| Strings.equalsIgnoreCase("127.0.0.1", Mvcs.getReq().getServerName())
					|| config.getBoolean("debug");
		default:
			return config.get(paras[0].toString());
		}

	}

}
