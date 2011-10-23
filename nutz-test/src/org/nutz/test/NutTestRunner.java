package org.nutz.test;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.nutz.plugin.SimplePluginManager;

/**
 * 本Runner旨在简化使用Nutz时的单元测试
 * <p/>当前支持的注解:
 * <p/>@NutTest(rollback=true) 当前仅有rollback属性,默认是false
 * <p/>查找顺序: 具体测试方法(不查找被override的父类方法),所在的类,递归查找父类
 * <p/>@IocBy @Iocbean @Inject 为测试了构建一个Ioc环境,语法与Mvc中@Iocby用法一致
 * <p/>查找顺序: 所在的类,递归查找父类. 当前类必须声明@Iocbean,其@Inject才会生效.
 * 即当前类必须使用注解定义为一个Iocbean
 * @author wendal
 *
 */
public class NutTestRunner extends Runner {
	
	private Runner _runner;
	
	private static NutTestRunnerPlugin plugin;
	static {
		plugin = new SimplePluginManager<NutTestRunnerPlugin>(
				"org.nutz.test.junit48.NutJunit48RunnerPlugin",
				"org.nutz.test.junit43.NutJunit43RunnerPlugin").get();
	}
	
	public NutTestRunner(Class<?> klass) throws InitializationError {
		try {
			_runner = plugin.make(klass);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public Description getDescription() {
		return _runner.getDescription();
	}

	public void run(RunNotifier notifier) {
		_runner.run(notifier);
	}

}
