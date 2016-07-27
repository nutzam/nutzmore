package org.nutz.integration.dubbo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
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

@SuppressWarnings("rawtypes")
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
    
    protected Map<String, Object> maps = new HashMap<>();
    
    public static Map<String, NutMap> read(String path) {
        Map<String, NutMap> maps = new LinkedHashMap<>();
        Document doc = Xmls.xml(DubboConfigureReader.class.getClassLoader().getResourceAsStream(path));
        doc.normalizeDocument();
        Element top = doc.getDocumentElement();
        NodeList list = top.getChildNodes();
        int count = list.getLength();

        for (int i = 0; i < count; i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element ele = (Element)node;
                String eleName = ele.getNodeName();
                if (!eleName.startsWith("dubbo:"))
                    continue; // 跳过非dubbo节点
                String typeName = eleName.substring("dubbo:".length());
                NutMap attrs = toAttrMap(ele.getAttributes());
                log.debug("found " + typeName);
                String genBeanName = ele.getAttribute("id");
                if (Strings.isBlank(genBeanName)) {
                    if ("protocol".equals(typeName))
                        genBeanName = "dubbo";
                    else {
                        genBeanName = ele.getAttribute("interface");
                        if (Strings.isBlank(genBeanName)) {
                            try {
                                genBeanName = Class.forName("com.alibaba.dubbo.config."+Strings.upperFirst(typeName)+"Config").getName();
                            }
                            catch (ClassNotFoundException e) {
                                throw Lang.wrapThrow(e);
                            }
                        }
                    }
                    if (maps.containsKey(genBeanName)) {
                        int _count = 2;
                        while (true) {
                            String key = genBeanName+"_"+_count;
                            if (maps.containsKey(key)) {
                                _count++;
                                continue;
                            }
                            genBeanName += "_"+_count;
                            break;
                        }
                    }
                }
                attrs.put("_typeName", typeName);
                maps.put(genBeanName, attrs);
            }
        }
        return maps;
    }
    
    @SuppressWarnings("unchecked")
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
                    continue; // 跳过非dubbo节点
                String typeName = eleName.substring("dubbo:".length());
                NutMap attrs = toAttrMap(ele.getAttributes());
                log.debug("found " + typeName);
                String genBeanName = ele.getAttribute("id");
                if (Strings.isBlank(genBeanName)) {
                    if ("protocol".equals(typeName))
                        genBeanName = "dubbo";
                    else {
                        genBeanName = ele.getAttribute("interface");
                        if (Strings.isBlank(genBeanName)) {
                            try {
                                genBeanName = Class.forName("com.alibaba.dubbo.config."+Strings.upperFirst(typeName)+"Config").getName();
                            }
                            catch (ClassNotFoundException e) {
                                throw Lang.wrapThrow(e);
                            }
                        }
                    }
                    if (maps.containsKey(genBeanName)) {
                        int _count = 2;
                        while (true) {
                            String key = genBeanName+"_"+_count;
                            if (maps.containsKey(key)) {
                                _count++;
                                continue;
                            }
                            genBeanName += "_"+_count;
                            break;
                        }
                    }
                }
                String id = genBeanName;
                switch (typeName) {
                case "application":
                    ApplicationConfig application = Lang.map2Object(attrs, ApplicationConfig.class);
                    applications.put(application.getName(), application);
                    maps.put(id, application);
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
                    service.setRef(_ref); // 临时的
                    services.put(service.getId(), service);
                    maps.put(id, service);
                    break;
                case "protocol":
                    ProtocolConfig protocol = Lang.map2Object(attrs, ProtocolConfig.class);
                    protocols.put(id, protocol);
                    maps.put(id, protocol);
                    break;
                case "registry":
                    RegistryConfig registry = Lang.map2Object(attrs, RegistryConfig.class);
                    registries.put(id, registry);
                    maps.put(id, registry);
                    break;
                case "consumer":
                    ConsumerConfig consumer = Lang.map2Object(attrs, ConsumerConfig.class);
                    consumers.put(id, consumer);
                    maps.put(id, consumer);
                    break;
                case "provider":
                    ProviderConfig provider = Lang.map2Object(attrs, ProviderConfig.class);
                    providers.put(id, provider);
                    maps.put(id, provider);
                    break;
                case "reference":
                    ReferenceConfig reference = Lang.map2Object(attrs, ReferenceConfig.class);
                    references.put(id, reference);
                    maps.put(id, reference);
                    break;
                case "method":
                    MethodConfig method = Lang.map2Object(attrs, MethodConfig.class);
                    methods.put(id, method);
                    maps.put(id, method);
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
