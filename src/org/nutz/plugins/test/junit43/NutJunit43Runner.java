package org.nutz.plugins.test.junit43;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;

public class NutJunit43Runner extends TestClassRunner {

	public NutJunit43Runner(final Class<?> klass) throws InitializationError {
		super(klass, new NutTestClassMethodsRunner(klass));
	}
}
