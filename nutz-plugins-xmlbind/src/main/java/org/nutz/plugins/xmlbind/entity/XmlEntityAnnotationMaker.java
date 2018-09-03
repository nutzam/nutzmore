package org.nutz.plugins.xmlbind.entity;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.plugins.xmlbind.annotation.XmlAttr;
import org.nutz.plugins.xmlbind.annotation.XmlEle;

public class XmlEntityAnnotationMaker {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> XmlEntity<T> makeEntity(XmlEle ele, Class<T> klass) {
        if (ele == null)
            ele = klass.getAnnotation(XmlEle.class);
        if (ele == null)
            return null;
        XmlEntity en = new XmlEntity();
        en.setName(ele.value());
        en.attrs = new LinkedHashMap<String, XmlEntityAttr>();
        en.nodes = new LinkedHashMap<String, XmlEntity<?>>();
        //en.setExtAttrs(ele.extAttrs());
        //en.setExtNodes(ele.extNodes());
        en.setSimpleNode(ele.simpleNode());
        Mirror mirror = Mirror.me(klass);
        for (Field field : mirror.getFields()) {
            XmlEle ele2 = field.getAnnotation(XmlEle.class);
            if (ele2 != null) {
                XmlEntity en2 = null;
                if (List.class.isAssignableFrom(field.getType())) {
                    en2 = makeEntity(ele2, Mirror.getGenericTypes(field, 0));
                    en2.setUseList(true);
                }
                else {
                    en2 = makeEntity(ele2, field.getType());
                }
                en2.setName(Strings.sBlank(ele2.value(), field.getName()));
                en2.setEjecting(mirror.getEjecting(field.getName()));
                en2.setInjecting(mirror.getInjecting(field.getName()));
                en.nodes.put(en2.getName(), en2);
            }
            XmlAttr attr = field.getAnnotation(XmlAttr.class);
            if (attr != null) {
                XmlEntityAttr enAttr = makeEntityAttr(attr, field.getType());
                if (enAttr != null) {
                    enAttr.setName(Strings.sBlank(attr.value(), field.getName()));
                    enAttr.setEjecting(mirror.getEjecting(field.getName()));
                    enAttr.setInjecting(mirror.getInjecting(field.getName()));
                    en.attrs.put(enAttr.name, enAttr);
                }
            }
        }
        en.setBorning(mirror.getBorning());
        if (List.class.isAssignableFrom(klass))
            en.setUseList(true);
        en.setKlass(klass);
        return en;
    }
    
    public XmlEntityAttr makeEntityAttr(XmlAttr attr, Class<?> klass) {
        if (attr == null)
            return null;
        XmlEntityAttr enAttr = new XmlEntityAttr();
        enAttr.setName(attr.value());
        enAttr.setIgnoreBlank(attr.ignoreBlank());
        enAttr.setIgnoreZero(attr.ignoreZero());
        return enAttr;
    }
}
