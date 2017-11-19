package org.nutz.integration.dubbo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
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

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.Protocol;

public class DubboIocLoader implements IocLoader {
    
    private static final Log log = Logs.get();
    
    protected Map<String, IocObject> iobjs = new HashMap<>();
    
    public DubboIocLoader() {}
    
    public DubboIocLoader(String ... paths) {
        List<Element> tops = new ArrayList<>();
        for (String xmlpath : paths) {
            Document doc = Xmls.xml(getClass().getClassLoader().getResourceAsStream(xmlpath));
            doc.normalizeDocument();
            Element top = doc.getDocumentElement();
            tops.add(top);
        }
        load(tops.toArray(new Element[tops.size()]));
    }
    
    public void load(Element ... tops) {
        for (Element top : tops) {
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
                    load(typeName, ele);
                }
            }
        }
        
        IocObject dubbo_iobjs = Iocs.wrap(iobjs);
        dubbo_iobjs.setType(Object.class);
        iobjs.put("dubbo_iobjs", dubbo_iobjs);
        for (Entry<String, IocObject> en : iobjs.entrySet()) {
            IocObject iobj = en.getValue();
            String beanName = en.getKey();
            DubboAgent.checkIocObject(beanName, iobj);
        }
        
        // 填充DubboManager
        DubboManager dubboManager = new DubboManager();
        dubboManager.iobjs = iobjs;
        IocObject dubbo_manager = Iocs.wrap(dubboManager);
        dubbo_manager.setType(DubboManager.class);
        dubbo_manager.addField(DubboAgent._field("ioc", DubboAgent._ref("$ioc")));
        IocEventSet events = new IocEventSet();
        events.setCreate("init");
        events.setDepose("depose");
        dubbo_manager.setEvents(events);
        iobjs.put("dubboManager", dubbo_manager);
    }
    
    public String load(String typeName, Element ele) {
        String genBeanName = ele.getAttribute("id");
        String id = genBeanName;
        if (Strings.isBlank(genBeanName)) {
            if ("protocol".equals(typeName))
                genBeanName = "dubbo";
            else {
                genBeanName = ele.getAttribute("interface");
                if (Strings.isBlank(genBeanName)) {
                    genBeanName = "dubbo_"+typeName;
                }
            }
            id = genBeanName;
            if (iobjs.containsKey(id)) {
                int count = 2;
                while (!iobjs.containsKey(id)) {
                    id = genBeanName + "_"+count;
                }
            }
        }
        try {
            add(id, typeName, ele);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
        return id;
    }
    
    protected void add(String id, String typeName, Element element) throws Exception {
        IocObject iobj = new IocObject();
        NutMap attrs = toAttrMap(element.getAttributes());
        switch (typeName) {
        case "protocol":
            // 填充其他bean的protocol
            for (IocObject _iobj : iobjs.values()) {
                IocField field = _iobj.getFields().get("protocol");
                if (field != null && attrs.getString("name", "").equals(field.getValue().getValue())) {
                    field.setValue(new IocValue(IocValue.TYPE_REFER, id));
                }
            }
            break;
        case "service":
            String className = attrs.getString("class");
            if (className != null) {
                IocObject inner = new IocObject();
                String uu32 = id+"Impl";
                inner.setType(Class.forName(className));
                parseProperties(element.getChildNodes(), inner);
                iobj.addField(DubboAgent._field("ref", new IocValue(IocValue.TYPE_REFER, uu32)));
                iobjs.put(uu32, inner);
            }
            break;
        case "provider":
            parseNested(element, "service", true, "service", "provider", id, iobj);
            break;
        case "consumer":
            parseNested(element, "reference", false, "reference", "consumer", id, iobj);
            break;
        }
        try {
            Class<?> beanClass = null;
            switch (typeName) {
            case "reference":
                beanClass = ReferenceBean.class;
                break;
            case "service":
                beanClass = ServiceBean.class;
                break;
            case "annotation":
                beanClass = AnnotationBean.class;
                break;
            default:
                beanClass = Class.forName("com.alibaba.dubbo.config."+Strings.upperFirst(typeName)+"Config");
                break;
            }
            iobj.setType(beanClass);
            Set<String> props = new HashSet<String>();
            NutMap parameters = null;
            for (Method setter : beanClass.getMethods()) {
                String name = setter.getName();
                if (name.length() > 3 && name.startsWith("set")
                        && Modifier.isPublic(setter.getModifiers())
                        && setter.getParameterTypes().length == 1) {
                    Class<?> type = setter.getParameterTypes()[0];
                    String property = StringUtils.camelToSplitName(name.substring(3, 4).toLowerCase() + name.substring(4), "-");
                    props.add(property);
                    Method getter = null;
                    try {
                        getter = beanClass.getMethod("get" + name.substring(3), new Class<?>[0]);
                    } catch (NoSuchMethodException e) {
                        try {
                            getter = beanClass.getMethod("is" + name.substring(3), new Class<?>[0]);
                        } catch (NoSuchMethodException e2) {
                        }
                    }
                    if (getter == null 
                            || ! Modifier.isPublic(getter.getModifiers())
                            || ! type.equals(getter.getReturnType())) {
                        continue;
                    }
                    if ("parameters".equals(property)) {
                        parameters = parseParameters(element.getChildNodes(), iobj);
                    } else if ("methods".equals(property)) {
                        parseMethods(id, element.getChildNodes(), iobj);
                    } else if ("arguments".equals(property)) {
                        parseArguments(id, element.getChildNodes(), iobj);
                    } else {
                        String value = attrs.getString(property);
                        if (value != null) {
                            value = value.trim();
                            if (value.length() > 0) {
                                if ("registry".equals(property) && RegistryConfig.NO_AVAILABLE.equalsIgnoreCase(value)) {
                                    RegistryConfig registryConfig = new RegistryConfig();
                                    registryConfig.setAddress(RegistryConfig.NO_AVAILABLE);
                                    IocField _field = new IocField();
                                    _field.setName(property);
                                    _field.setValue(new IocValue(IocValue.TYPE_NORMAL, registryConfig));
                                    iobj.addField(_field);
                                } else if ("registry".equals(property) && value.indexOf(',') != -1) {
                                    parseMultiRef("registries", value, iobj);
                                } else if ("provider".equals(property) && value.indexOf(',') != -1) {
                                    parseMultiRef("providers", value, iobj);
                                } else if ("protocol".equals(property) && value.indexOf(',') != -1) {
                                    parseMultiRef("protocols", value, iobj);
                                } else {
                                    Object reference;
                                    if (isPrimitive(type)) {
                                        if ("async".equals(property) && "false".equals(value)
                                                || "timeout".equals(property) && "0".equals(value)
                                                || "delay".equals(property) && "0".equals(value)
                                                || "version".equals(property) && "0.0.0".equals(value)
                                                || "stat".equals(property) && "-1".equals(value)
                                                || "reliable".equals(property) && "false".equals(value)) {
                                            // 兼容旧版本xsd中的default值
                                            value = null;
                                        }
                                        reference = value;
                                    } else if ("protocol".equals(property) 
                                            && ExtensionLoader.getExtensionLoader(Protocol.class).hasExtension(value)
                                            && (! iobjs.containsKey(value)
                                                    || ! ProtocolConfig.class.getName().equals(iobjs.get(value).getType().getName()))) {
                                        if ("dubbo:provider".equals(typeName)) {
                                            log.warn("Recommended replace <dubbo:provider protocol=\"" + value + "\" ... /> to <dubbo:protocol name=\"" + value + "\" ... />");
                                        }
                                        // 兼容旧版本配置
                                        ProtocolConfig protocol = new ProtocolConfig();
                                        protocol.setName(value);
                                        reference = protocol;
                                    } else if ("monitor".equals(property) 
                                            && (! iobjs.containsKey(value)
                                                    || ! MonitorConfig.class.getName().equals(iobjs.get(value).getType().getName()))) {
                                        // 兼容旧版本配置
                                        reference = convertMonitor(value);
                                    } else if ("onreturn".equals(property)) {
                                        int index = value.lastIndexOf(".");
                                        String returnRef = value.substring(0, index);
                                        String returnMethod = value.substring(index + 1);
                                        reference = DubboAgent._ref(returnRef);
                                        iobj.addField(DubboAgent._field("onreturnMethod", new IocValue(IocValue.TYPE_NORMAL, returnMethod)));
                                        //beanDefinition.getPropertyValues().addPropertyValue("onreturnMethod", returnMethod);
                                    } else if ("onthrow".equals(property)) {
                                        int index = value.lastIndexOf(".");
                                        String throwRef = value.substring(0, index);
                                        String throwMethod = value.substring(index + 1);
                                        reference = DubboAgent._ref(throwRef);
                                        iobj.addField(DubboAgent._field("onthrowMethod", new IocValue(IocValue.TYPE_NORMAL, throwMethod)));
                                        //beanDefinition.getPropertyValues().addPropertyValue("onthrowMethod", throwMethod);
                                        //throw Lang.noImplement();
                                    } else {
                                        if ("ref".equals(property) && iobjs.containsKey(value)) {
                                            IocObject _iobj = iobjs.get(value);
                                            if (! _iobj.isSingleton()) {
                                                throw new IllegalStateException("The exported service ref " + value + " must be singleton! Please set the " + value + " bean scope to singleton, eg: <bean id=\"" + value+ "\" scope=\"singleton\" ...>");
                                            }
                                        }
                                        reference = new IocValue(IocValue.TYPE_REFER, value);
                                    }
                                    IocField _field = new IocField();
                                    if ("interface".equals(property)) {
                                        attrs.remove(property);
                                        property = "interfaceName";
                                    }
                                    
                                    _field.setName(property);
                                    if (reference != null && reference instanceof IocValue) {
                                        _field.setValue((IocValue)reference);
                                    } else {
                                        _field.setValue(new IocValue(IocValue.TYPE_NORMAL, reference));
                                    }
                                    iobj.addField(_field);
                                }
                            }
                        }
                    }
                }
            }
            for (Entry<String, Object> _en : attrs.entrySet()) {
                if (iobj.getFields().containsKey(_en.getKey()))
                    continue;
                String fieldName = _en.getKey();
                IocField field = new IocField();
                field.setName(fieldName);
                field.setValue(new IocValue(IocValue.TYPE_NORMAL, _en.getValue()));
                iobj.addField(field);
            }
            if (parameters != null) {
                IocField field = new IocField();
                field.setName("parameters");
                IocValue value = new IocValue(IocValue.TYPE_NORMAL, parameters);
                field.setValue(value);
                iobj.addField(field);
            }

            // 如果是reference, 那么需要生成2个bean
            if ("reference".equals(typeName)) {
                IocObject refBean = new IocObject();
                refBean.setFactory("$dubbo_reference_"+id+"#get");
                refBean.setType(Class.forName((String)iobj.getFields().get("interfaceName").getValue().getValue()));
                iobjs.put(id, refBean);
                id = "dubbo_reference_"+id;
            }
            
            iobjs.put(id, iobj);
        }
        catch (ClassNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
    }
    
    private static void parseMultiRef(String property, String value, IocObject iobj) {
        String[] values = value.split("\\s*[,]+\\s*");
        ArrayList<Object> list = null;
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            if (v != null && v.length() > 0) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                IocValue iocValue = new IocValue(IocValue.TYPE_REFER, v);
                list.add(iocValue);
            }
        }
        IocField _field = new IocField();
        _field.setName(property);
        _field.setValue(new IocValue(IocValue.TYPE_NORMAL, list));
        iobj.addField(_field);
    }
    
    private static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == Boolean.class || cls == Byte.class
                || cls == Character.class || cls == Short.class || cls == Integer.class
                || cls == Long.class || cls == Float.class || cls == Double.class
                || cls == String.class || cls == Date.class || cls == Class.class;
    }

    public String[] getName() {
        int count = iobjs.size();
        return iobjs.keySet().toArray(new String[count]);
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        IocObject obj = iobjs.get(name);
        if (obj == null)
            throw new ObjectLoadException("Object '" + name + "' without define!");
        return obj;
    }

    public boolean has(String name) {
        return iobjs.containsKey(name);
    }
    private static final Pattern GROUP_AND_VERION = Pattern.compile("^[\\-.0-9_a-zA-Z]+(\\:[\\-.0-9_a-zA-Z]+)?$");
    
    protected static MonitorConfig convertMonitor(String monitor) {
        if (monitor == null || monitor.length() == 0) {
            return null;
        }
        if (GROUP_AND_VERION.matcher(monitor).matches()) {
            String group;
            String version;
            int i = monitor.indexOf(':');
            if (i > 0) {
                group = monitor.substring(0, i);
                version = monitor.substring(i + 1);
            } else {
                group = monitor;
                version = null;
            }
            MonitorConfig monitorConfig = new MonitorConfig();
            monitorConfig.setGroup(group);
            monitorConfig.setVersion(version);
            return monitorConfig;
        }
        return null;
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
    
    private static void parseProperties(NodeList nodeList, IocObject iobj) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("property".equals(node.getNodeName())
                            || "property".equals(node.getLocalName())) {
                        String name = ((Element) node).getAttribute("name");
                        if (name != null && name.length() > 0) {
                            String value = ((Element) node).getAttribute("value");
                            String ref = ((Element) node).getAttribute("ref");
                            if (value != null && value.length() > 0) {
                                iobj.addField(DubboAgent._field(name, new IocValue(IocValue.TYPE_NORMAL, value)));
                                //beanDefinition.getPropertyValues().addPropertyValue(name, value);
                            } else if (ref != null && ref.length() > 0) {
                                iobj.addField(DubboAgent._field(name, new IocValue(IocValue.TYPE_REFER, ref)));
                                //beanDefinition.getPropertyValues().addPropertyValue(name, new RuntimeBeanReference(ref));
                            } else {
                                throw new UnsupportedOperationException("Unsupported <property name=\"" + name + "\"> sub tag, Only supported <property name=\"" + name + "\" ref=\"...\" /> or <property name=\"" + name + "\" value=\"...\" />");
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static NutMap parseParameters(NodeList nodeList, IocObject iobj) {
        if (nodeList != null && nodeList.getLength() > 0) {
            NutMap parameters = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("parameter".equals(node.getNodeName())
                            || "parameter".equals(node.getLocalName())) {
                        if (parameters == null) {
                            parameters = new NutMap();
                        }
                        String key = ((Element) node).getAttribute("key");
                        String value = ((Element) node).getAttribute("value");
                        boolean hide = "true".equals(((Element) node).getAttribute("hide"));
                        if (hide) {
                            key = Constants.HIDE_KEY_PREFIX + key;
                        }
                        parameters.put(key, value);
                    }
                }
            }
            return parameters;
        }
        return null;
    }
    
    protected void parseMethods(String id, NodeList nodeList, IocObject iobj) throws Exception {
        if (nodeList != null && nodeList.getLength() > 0) {
            NutMap methods = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if ("method".equals(node.getNodeName()) || "method".equals(node.getLocalName())) {
                        String methodName = element.getAttribute("name");
                        if (methodName == null || methodName.length() == 0) {
                            throw new IllegalStateException("<dubbo:method> name attribute == null");
                        }
                        if (methods == null) {
                            methods = new NutMap();
                        }
                        //BeanDefinition methodBeanDefinition = parse(((Element) node),
                        //        parserContext, MethodConfig.class, false);
                        String name = id + "." + methodName;

                        add(name, "method", (Element) node);
                        //BeanDefinitionHolder methodBeanDefinitionHolder = new BeanDefinitionHolder(
                        //        methodBeanDefinition, name);
                        methods.put(methodName, DubboAgent._ref(name));
                    }
                }
            }
            if (methods != null) {
                iobj.addField(DubboAgent._field("methods", new IocValue(IocValue.TYPE_NORMAL, methods)));
                //beanDefinition.getPropertyValues().addPropertyValue("methods", methods);
            }
        }
    }
    
    private static void parseArguments(String id, NodeList nodeList, IocObject iobj) {
        if (nodeList != null && nodeList.getLength() > 0) {
            NutMap arguments = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if ("argument".equals(node.getNodeName()) || "argument".equals(node.getLocalName())) {
                        String argumentIndex = element.getAttribute("index");
                        if (arguments == null) {
                            arguments = new NutMap();
                        }
                        //BeanDefinition argumentBeanDefinition = parse(((Element) node),
                         //       parserContext, ArgumentConfig.class, false);
                        String name = id + "." + argumentIndex;
                        //BeanDefinitionHolder argumentBeanDefinitionHolder = new BeanDefinitionHolder(
                        //        argumentBeanDefinition, name);
                        arguments.put(argumentIndex, DubboAgent._ref(name));
                    }
                }
            }
            if (arguments != null) {
                iobj.addField(DubboAgent._field("arguments", new IocValue(IocValue.TYPE_NORMAL, arguments)));
                //beanDefinition.getPropertyValues().addPropertyValue("arguments", arguments);
            }
        }
    }
    
    protected void parseNested(Element element, String typeName, boolean required, String tag, String property, String ref, IocObject iobj) {
        NodeList nodeList = element.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            boolean first = true;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if (tag.equals(node.getNodeName())
                            || tag.equals(node.getLocalName())) {
                        if (first) {
                            first = false;
                            String isDefault = element.getAttribute("default");
                            if (isDefault == null || isDefault.length() == 0) {
                                iobj.addField(DubboAgent._field("default", new IocValue(IocValue.TYPE_NORMAL, "false")));
                                //beanDefinition.getPropertyValues().addPropertyValue("default", "false");
                            }
                        }
                        String subDefinition = load(typeName, (Element) node);
                        //BeanDefinition subDefinition = parse((Element) node, parserContext, beanClass, required);
                        if (subDefinition != null && ref != null && ref.length() > 0) {
                            iobjs.get(subDefinition).addField(DubboAgent._field(property, DubboAgent._ref(ref)));
                            //subDefinition.getPropertyValues().addPropertyValue(property, new RuntimeBeanReference(ref));
                        }
                    }
                }
            }
        }
    }
}
