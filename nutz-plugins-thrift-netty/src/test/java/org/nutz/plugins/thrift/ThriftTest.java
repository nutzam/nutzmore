package org.nutz.plugins.thrift;

import org.apache.thrift.protocol.TCompactProtocol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;

public class ThriftTest {

	NutIoc ioc;

	@Before
	public void setUp() throws Exception {
		// 模拟Mvc环境下的@IocBy
		ioc = new NutIoc(new ComboIocLoader("*org.nutz.plugins.thrift.ThriftIocLoader"));
	}

	@After
	public void tearDown() throws Exception {
		if (ioc != null)
			ioc.depose();
	}

	@Test
	public void testCronStringClassOfQ() {
		ioc.get(NutThriftNettyFactory.class, "thriftFactory").serverPort(8080).tProtocolFactory(new TCompactProtocol.Factory()).load("org.nutz.plugins.thrift.netty.demo");
	}

}
