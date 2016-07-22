package org.nutz.integration.dubbo;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Xmls;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ArgumentConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

public class DubboConfigureReader {

    private static final Log log = Logs.get();
    
    protected Map<String, ApplicationConfig> applications = new HashMap<>();
    protected Map<String, ProtocolConfig> protocols = new HashMap<>();
    protected Map<String, ServiceConfig> services = new HashMap<>();
    protected Map<String, RegistryConfig> registries = new HashMap<>();
    protected Map<String, ConsumerConfig> consumers = new HashMap<>();
    protected Map<String, ProviderConfig> providers = new HashMap<>();
    protected Map<String, ReferenceConfig> references = new HashMap<>();
    protected Map<String, MethodConfig> methods = new HashMap<>();
    protected Map<String, ArgumentConfig> arguments = new HashMap<>();
    //protected Map<String, ParameterConfig> parameters = new HashMap<>();
    
    @SuppressWarnings("rawtypes")
    public DubboConfigureReader(String xmlpath) {
        Document doc = Xmls.xml(getClass().getClassLoader().getResourceAsStream(xmlpath));
        doc.normalizeDocument();
        Element top = doc.getDocumentElement();
        NodeList list = top.getChildNodes();
        int count = list.getLength();

        String interfaceClass = null;
        for (int i = 0; i < count; i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element ele = (Element)node;
                String eleName = ele.getNodeName();
                if (!eleName.startsWith("dubbo:"))
                    continue;
                String typeName = eleName.substring("dubbo:".length());
                NutMap attrs = toAttrMap(ele.getAttributes());
                switch (typeName) {
                case "application":
                    ApplicationConfig application = Lang.map2Object(attrs, ApplicationConfig.class);
                    applications.put(application.getName(), application);
                    break;
                case "service":
                    interfaceClass = (String)attrs.remove("interface");
                    String _application = (String) attrs.remove("application");
                    String _ref = (String) attrs.remove("ref");
                    ServiceConfig service = Lang.map2Object(attrs, ServiceConfig.class);
                    service.setInterface(interfaceClass);
                    service.setApplication(applications.get(_application));
                    if (service.getId() == null) {
                        service.setId("service_"+services.size());
                    }
                    services.put(service.getId(), service);
                    break;
                case "protocol":
                    ProtocolConfig protocol = Lang.map2Object(attrs, ProtocolConfig.class);
                    if (protocol.getId() == null)
                        protocol.setId("protocol_"+protocols.size());
                    protocols.put(protocol.getId(), protocol);
                    break;
                case "registry":
                    RegistryConfig registry = Lang.map2Object(attrs, RegistryConfig.class);
                    if (registry.getId() == null) {
                        registry.setId("registry_"+registries.size());
                    }
                    registries.put(registry.getId(), registry);
                    break;
                case "consumer":
                    ConsumerConfig consumer = Lang.map2Object(attrs, ConsumerConfig.class);
                    if (consumer.getId() == null)
                        consumer.setId("consumer_"+consumers.size());
                    consumers.put(consumer.getId(), consumer);
                    break;
                case "provider":
                    ProviderConfig provider = Lang.map2Object(attrs, ProviderConfig.class);
                    if (provider.getId() == null)
                        provider.setId("provider_"+providers.size());
                    providers.put(provider.getId(), provider);
                    break;
                case "reference":
                    ReferenceConfig reference = Lang.map2Object(attrs, ReferenceConfig.class);
                    if (reference.getId() == null)
                        reference.setId("reference_"+references.size());
                    references.put(reference.getId(), reference);
                    break;
                case "method":
                    MethodConfig method = Lang.map2Object(attrs, MethodConfig.class);
                    if (method.getId() == null)
                        method.setId("method_"+methods.size());
                    methods.put(method.getId(), method);
                    break;
//                case "argument":
//                    ArgumentConfig argument = Lang.map2Object(attrs, ArgumentConfig.class);
//                    if (argument.getId() == null)
//                        argument.setId("argument_"+arguments.size());
//                    arguments.put(argument.getId(), reference);
//                    break;
                default:
                    log.warn(typeName+" is not support yet");
                    break;
                }
            }
        }
    }
    
    public static NutMap toAttrMap(NamedNodeMap attrs) {
        NutMap map = new NutMap();
        int len = attrs.getLength();
        for (int j = 0; j < len; j++) {
            Attr attr = (Attr)attrs.item(j);
            map.put(attr.getName(), attr.getValue());
        }
        return map;
    }
}
