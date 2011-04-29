package org.nutz.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 集成测试验证框架
 * 
 * @author QinerG(QinerG@gmail.com)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { AopValidationTest.class, MvcValidationTest.class,
		ValidationTest.class })
public class All { }
