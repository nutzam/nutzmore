package org.nutz.test.junit43;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Runner;
import org.nutz.test.NutTestRunnerPlugin;

public class NutJunit43RunnerPlugin implements NutTestRunnerPlugin{

	public boolean canWork() {
		try {
			TestClassRunner.class.toString();
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public Runner make(Class<?> klass) throws InitializationError {
		return new NutzJUnit43ClassRunner(klass);
	}

}
