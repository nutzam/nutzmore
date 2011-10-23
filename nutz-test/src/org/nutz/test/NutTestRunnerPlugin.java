package org.nutz.test;

import org.junit.runner.Runner;
import org.nutz.plugin.Plugin;

public interface NutTestRunnerPlugin extends Plugin {

	Runner make(Class<?> klass) throws Throwable;
	
}
