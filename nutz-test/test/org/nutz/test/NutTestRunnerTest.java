package org.nutz.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.test.junit48.NutJunit48Runner;

@RunWith(NutJunit48Runner.class)
@IocBean
@IocBy(type=ComboIocProvider.class,args={"*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
	"org.nutz.test"})
public class NutTestRunnerTest {
	
	@Inject("sys:file.encoding")
	private String encoding;

	@Test
	public void test() {
		assertNotNull(encoding);
	}

	@Test
	public void test2() {
		;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
