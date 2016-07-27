package org.nutz.integration.dubbo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.Protocol;

public class DubboIocLoader implements IocLoader {
    
    private static final Log log = Logs.get();
    
    Map<String, NutMap> maps;
    
    protected Map<String, IocObject> iobjs = new HashMap<>();
    
    protected String applicationId;
    protected String registryId;
    
    protected DubboIocLoader() {}
    
    public DubboIocLoader(String xmlpath) {
        maps = DubboConfigureReader.read(xmlpath);
        init();
    }
    
    protected void init() {
        for (Entry<String, NutMap> en : maps.entrySet()) {
            String id = en.getKey();
            NutMap map = en.getValue();
            String typeName = (String) map.remove("_typeName");
            add(id, typeName, map);
        }
        
        // 检查一下application/registry是不是都设置了
        for (IocObject iobj : iobjs.values()) {
            if (!iobj.getFields().containsKey("application")) {
                try {
                    iobj.getType().getMethod("setApplication", ApplicationConfig.class);
                    IocField field = new IocField();
                    field.setName("application");
                    field.setValue(new IocValue(IocValue.TYPE_REFER, applicationId));
                    iobj.addField(field);
                }
                catch (Exception e) {
                }
            }
            if (!iobj.getFields().containsKey("registry") && !iobj.getFields().containsKey("registries")) {
                try {
                    iobj.getType().getMethod("setRegistry", RegistryConfig.class);
                    IocField field = new IocField();
                    field.setName("registry");
                    field.setValue(new IocValue(IocValue.TYPE_REFER, registryId));
                    iobj.addField(field);
                }
                catch (Exception e) {
                }
            }
        }
    }
    
    protected void add(String id, String typeName, NutMap attrs) {

        switch (typeName) {
        case "protocol":
            // 填充其他bean的protocol
            for (IocObject iobj : iobjs.values()) {
                IocField field = iobj.getFields().get("protocol");
                if (field != null && attrs.getString("name", "").equals(field.getValue().getValue())) {
                    field.setValue(new IocValue(IocValue.TYPE_REFER_TYPE, id));
                }
            }
            break;
        case "service":
            // 直接定义一个bean,暂不支持
            String className = attrs.getString("class");
            if (className != null) {
                throw Lang.noImplement();
            }
            break;
        case "provider":
            throw Lang.noImplement(); // 看上去是provider里面定义一个service,没搞懂
        case "consumer":
            throw Lang.noImplement(); // consumer里面有个ref?
        }
        try {
            IocObject iobj = new IocObject();
            Class<?> beanClass = Class.forName("com.alibaba.dubbo.config."+Strings.upperFirst(typeName)+"Config");
            iobj.setType(beanClass);
            Set<String> props = new HashSet<String>();
            //NutMap parameters = null;
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
                        // parameters = parseParameters(element.getChildNodes(), beanDefinition);
                    } else if ("methods".equals(property)) {
                        // parseMethods(id, element.getChildNodes(), beanDefinition, parserContext);
                    } else if ("arguments".equals(property)) {
                        // parseArguments(id, element.getChildNodes(), beanDefinition, parserContext);
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
                                        //int index = value.lastIndexOf(".");
                                        //String returnRef = value.substring(0, index);
                                        //String returnMethod = value.substring(index + 1);
                                        //reference = new RuntimeBeanReference(returnRef);
                                        //beanDefinition.getPropertyValues().addPropertyValue("onreturnMethod", returnMethod);
                                        throw Lang.noImplement();
                                    } else if ("onthrow".equals(property)) {
                                        //int index = value.lastIndexOf(".");
                                        //String throwRef = value.substring(0, index);
                                        //String throwMethod = value.substring(index + 1);
                                        //reference = new RuntimeBeanReference(throwRef);
                                        //beanDefinition.getPropertyValues().addPropertyValue("onthrowMethod", throwMethod);
                                        throw Lang.noImplement();
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
            //if (parameters != null) {
            //    IocField field = new IocField();
            //    field.setName("parameters");
            //    IocValue value = new IocValue(IocValue.TYPE_NORMAL, parameters);
            //    iobj.addField(field);
            //}
            
            // 补充events定义
            IocEventSet events = new IocEventSet();
            switch (typeName) {
            case "service":
                events.setCreate("export");
                events.setDepose("unexport");
                iobj.setEvents(events);
                break;
            case "eference":
              // 关闭服务时需要销毁
                events.setDepose("destroy");
                iobj.setEvents(events);
                break;
            default:
                break;
            }
            
            if ("application".equals(typeName)) {
                applicationId = id;
            }
            else if ("registry".equals(typeName) && attrs.getBoolean("default", true)) {
                registryId = id;
            }
            // 如果是reference, 那么需要生成2个bean
            else if ("reference".equals(typeName)) {
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
}
