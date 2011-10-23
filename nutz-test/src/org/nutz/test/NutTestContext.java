package org.nutz.test;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;

public class NutTestContext {

	private static final NutTestContext me = new NutTestContext();
	
	public Ioc ioc;
	
	@SuppressWarnings("rawtypes")
	public Mirror mirror;
	
	public Class<?> klass;
	
	private NutTestContext() {}
	
	public static final NutTestContext me() {
		return me;
	}
	
	public void initTestContext(Class<?> klass) {
		mirror = Mirror.me(klass);
		ioc = null;
		this.klass = klass;
	}
	
	public Object makeTestObject() {
		if (ioc != null && klass.getAnnotation(IocBean.class) != null)
			return NutTestContext.me().ioc.get(klass);
		return null;
	}
}
