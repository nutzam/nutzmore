package org.nutz.test.junit48;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.nutz.test.NutTestContext;

public class NutzJUnit48ClassRunner extends BlockJUnit4ClassRunner {

	public NutzJUnit48ClassRunner(final Class<?> klass) throws InitializationError {
		super(klass);
		NutTestContext.me().initTestContext(klass);
	}
	
	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new NutTestInvokeMethod(method, test);
	}
	
	@Override
	protected Object createTest() throws Exception {
		Object obj = NutTestContext.me().makeTestObject();
		return obj != null ? obj : super.createTest();
	}
}
