package org.nutz.test.junit48;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.nutz.lang.Lang;
import org.nutz.test.JustRollback;
import org.nutz.test.NutTestContext;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * 为Nutz定制的测试方法运行器
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
class NutTestInvokeMethod extends Statement {

	private final FrameworkMethod fTestMethod;
	private Object fTarget;

	public NutTestInvokeMethod(FrameworkMethod testMethod, Object target) {
		fTestMethod = testMethod;
		fTarget = target;
	}

	public void evaluate() throws Throwable {
		try {
			// 处理事务回滚问题
			final boolean needRollback = NutTestContext.me().needRollback(
					fTestMethod.getMethod());
			if (needRollback)
				try {
					Trans.exec(new Atom() {
						@Override
						public void run() {
							try {
								fTestMethod.invokeExplosively(fTarget);
							} catch (Throwable e) {
								throw Lang.wrapThrow(e);
							}
							throw JustRollback.me();// 这样,无论原方法是否跑异常,事务模板都能收到异常,并回滚
						}
					});
				} catch (JustRollback e) {
				}
			else
				// 按传统方法执行,无需通过事务模板
				fTestMethod.invokeExplosively(fTarget);
		} finally {
			// 确保Ioc容器被关闭
			NutTestContext.me().closeIoc();
		}
	}
}
