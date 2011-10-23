package org.nutz.test.junit48;

import java.lang.reflect.Method;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runners.model.FrameworkMethod;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.test.JustRollback;
import org.nutz.test.NutTest;
import org.nutz.test.NutTestContext;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * 为Nutz定制的测试方法运行器
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class NutTestInvokeMethod extends InvokeMethod {
	
	private Method method;

	public NutTestInvokeMethod(FrameworkMethod testMethod, Object target) {
		super(testMethod, target);
		this.method = testMethod.getMethod();
	}

	private static final Log log = Logs.get();

	@SuppressWarnings("unchecked")
	public void evaluate() throws Throwable {
		// 处理事务回滚问题
		NutTest nutTest = method.getAnnotation(NutTest.class);
		if (nutTest == null)
			nutTest = (NutTest) NutTestContext.me().mirror.getAnnotation(NutTest.class);
		final boolean needRollback = nutTest != null && nutTest.rollback();
		
		try {
			// 检查Ioc支持
			IocBy iocBy = (IocBy) NutTestContext.me().mirror.getAnnotation(IocBy.class);
			if (iocBy != null)
				NutTestContext.me().ioc = Mirror.me(iocBy.type()).born().create(null, iocBy.args());
			else
				NutTestContext.me().ioc = null;

			// 打印调试信息
			if (log.isDebugEnabled()) {
				log.debug("->" + method + " -> auto-rollback=" + needRollback);
				if (NutTestContext.me().ioc == null)
					log.debug("@IocBy not found ,run without Ioc support !!");
				else
					log.debug("@IocBy found ,run with Ioc support ^_^");
			}
			
			if (needRollback)
				try {
					Trans.exec(new Atom() {
						@Override
						public void run() {
							try {
								NutTestInvokeMethod.super.evaluate();
							} catch (Throwable e) {
								throw Lang.wrapThrow(e);
							}
							throw JustRollback.me();// 这样,无论原方法是否跑异常,事务模板都能收到异常,并回滚
						}
					});
				}
				catch (JustRollback e) {}
			else
				// 按传统方法执行,无需通过事务模板
				super.evaluate();
		}
		finally {
			//确保Ioc容器被关闭
			if (NutTestContext.me().ioc != null) {
				try {
					NutTestContext.me().ioc.depose();
				} finally {
					NutTestContext.me().ioc = null;
				}
			}
		}
	}
}
