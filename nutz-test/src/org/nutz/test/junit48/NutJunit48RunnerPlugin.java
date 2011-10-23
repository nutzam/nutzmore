package org.nutz.test.junit48;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.nutz.test.NutTestRunnerPlugin;

public class NutJunit48RunnerPlugin implements NutTestRunnerPlugin{

	public boolean canWork() {
		try {
			BlockJUnit4ClassRunner.class.toString();
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public Runner make(Class<?> klass) throws Throwable {
		return new NutzJUnit48ClassRunner(klass);
	}

}
