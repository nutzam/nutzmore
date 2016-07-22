package org.nutz.integration.dubbo.service;

import org.junit.Test;
import org.nutz.integration.dubbo.DubboConfigureReader;

public class DubboXmlIocLoaderTest {

    @Test
    public void test_load_xml() {
        DubboConfigureReader loader = new DubboConfigureReader("simple-dubbo.xml");
    }
}
