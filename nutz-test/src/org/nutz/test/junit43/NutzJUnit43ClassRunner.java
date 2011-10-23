package org.nutz.test.junit43;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;

public class NutzJUnit43ClassRunner extends TestClassRunner {

	public NutzJUnit43ClassRunner(final Class<?> klass) throws InitializationError {
		super(klass, new NutTestClassMethodsRunner(klass));
	}
}
