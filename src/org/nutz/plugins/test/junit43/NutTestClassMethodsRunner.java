package org.nutz.plugins.test.junit43;

import java.lang.reflect.Method;

import org.junit.runner.notification.RunNotifier;
import org.nutz.plugins.test.JustRollback;
import org.nutz.plugins.test.NutTestContext;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * 为Nutz定制的测试方法运行器
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
class NutTestClassMethodsRunner extends TestClassMethodsRunner {

	public NutTestClassMethodsRunner(Class<?> klass) {
		super(klass);
		NutTestContext.me().initTestContext(klass);
	}

	protected void invokeTestMethod(final Method method,
			final RunNotifier notifier) {
		try {
			// 处理事务回滚问题
			final boolean needRollback = NutTestContext.me().needRollback(
					method);
			NutTestContext.me().initIoc();

			if (needRollback)
				try {
					Trans.exec(new Atom() {
						@Override
						public void run() {
							NutTestClassMethodsRunner.super.invokeTestMethod(
									method, notifier);
							throw JustRollback.me();// 这样,无论原方法是否跑异常,事务模板都能收到异常,并回滚
						}
					});
				} catch (JustRollback e) {
				}
			else
				// 按传统方法执行,无需通过事务模板
				super.invokeTestMethod(method, notifier);
		} finally {
			NutTestContext.me().closeIoc();
		}
	}

	protected Object createTest() throws Exception {
		Object obj = NutTestContext.me().makeTestObject();
		return obj != null ? obj : super.createTest();
	}
}
