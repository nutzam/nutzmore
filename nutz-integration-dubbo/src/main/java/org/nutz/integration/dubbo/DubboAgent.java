package org.nutz.integration.dubbo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;

public class DubboAgent {
    
    @SuppressWarnings("unchecked")
    public static <V> Map<String, V> getByType(Ioc ioc, Map<String, IocObject> iobjs, Class<V> klass) {
        Map<String, V> map = new HashMap<String, V>();
        for (Entry<String, IocObject> en : iobjs.entrySet()) {
            IocObject iobj = en.getValue();
            String name = en.getKey();
            
            if (klass.isAssignableFrom(iobj.getType())) {
                map.put(name, (V) ioc.get(iobj.getType(), name));
            }
        }
        return map;
    }
    
    public static void checkIocObject(String beanName, IocObject iobj) {
        if (iobj.getType() == ServiceBean.class 
                || iobj.getType() == ReferenceBean.class
                || iobj.getType() == AnnotationBean.class
                || iobj.getType() == DubboManager.class) {
            IocEventSet events = new IocEventSet();
            events.setCreate("_init");
            events.setDepose("depose");
            iobj.setEvents(events);
            iobj.addField(_field("ioc", _ref("$ioc")));
            iobj.addField(_field("iobjs", _ref("dubbo_iobjs")));
            iobj.addField(_field("beanName", new IocValue(IocValue.TYPE_NORMAL, beanName)));
        }
    }

    public static IocValue _ref(String beanName) {
        return new IocValue(IocValue.TYPE_REFER, beanName);
    }

    public static IocField _field(String fieldName, IocValue val) {
        IocField field = new IocField();
        field.setName(fieldName);
        field.setValue(val);
        return field;
    }
}
