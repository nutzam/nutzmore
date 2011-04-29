/**
 * 
 */
package org.nutz.validation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.validation.meta.Bean;
import org.nutz.validation.meta.ServiceDemo;

/**
 * 通过 AOP 对方法的调用进行拦截验证，且将验证结果注入到方法的参数中。
 * 
 * @author QinerG(QinerG@gmail.com)
 */
public class AopValidationTest {
	
	@Test
	public void testAop() {		
		Ioc ioc = new NutIoc(new AnnotationIocLoader("org.nutz.validation"));
		
		ServiceDemo sd = ioc.get(ServiceDemo.class);
		Bean b = new Bean();
		Errors ers = sd.test(b, null);
		assertEquals(10, ers.errorCount());
	}

}
