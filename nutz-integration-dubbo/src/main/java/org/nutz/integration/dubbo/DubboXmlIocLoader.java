package org.nutz.integration.dubbo;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;
import org.nutz.lang.Xmls;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;

public class DubboXmlIocLoader {
    
    public DubboXmlIocLoader() {}
    
    protected Map<String, ApplicationConfig> applications = new HashMap<>();
    protected Map<String, ProtocolConfig> protocols = new HashMap<>();
    
    public DubboXmlIocLoader(String xmlpath) {
        
    }

}
