package org.nutz.test;

import java.lang.reflect.Method;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.IocBy;

public class NutTestContext {

	private static final Log log = Logs.get();

	private static final NutTestContext me = new NutTestContext();

	public Ioc ioc;

	@SuppressWarnings("rawtypes")
	public Mirror mirror;

	public Class<?> klass;

	private NutTestContext() {
	}

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

	@SuppressWarnings("unchecked")
	public boolean needRollback(Method method) {
		NutTest nutTest = method.getAnnotation(NutTest.class);
		if (nutTest == null)
			nutTest = (NutTest) mirror.getAnnotation(NutTest.class);
		boolean needRollback = nutTest != null && nutTest.rollback();
		// 打印调试信息
		if (log.isDebugEnabled())
			log.debug("->" + method + " -> auto-rollback=" + needRollback);
		return needRollback;
	}

	@SuppressWarnings("unchecked")
	public void initIoc() {
		// 检查Ioc支持
		IocBy iocBy = (IocBy) mirror.getAnnotation(IocBy.class);
		if (iocBy != null)
			ioc = Mirror.me(iocBy.type()).born().create(null, iocBy.args());
		else
			ioc = null;
		// 打印调试信息
		if (log.isDebugEnabled()) {
			if (ioc == null)
				log.debug("@IocBy not found ,run without Ioc support !!");
			else
				log.debug("@IocBy found ,run with Ioc support ^_^");
		}
	}

	public void closeIoc() {
		// 确保Ioc容器被关闭
		if (NutTestContext.me().ioc != null)
			try {
				NutTestContext.me().ioc.depose();
			} finally {
				NutTestContext.me().ioc = null;
			}
	}
}
