package org.nutz.plugins.xmlbind;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.nutz.lang.Xmls;
import org.nutz.plugins.xmlbind.entity.XmlEntityAnnotationMaker;
import org.w3c.dom.Element;

public class XmlBind {

    protected static XmlEntityAnnotationMaker maker = new XmlEntityAnnotationMaker();

    public static <T> T fromXml(Class<T> klass, Element ele) {
        return maker.makeEntity(null, klass).read(ele);
    }

    public static <T> T fromXml(Class<T> klass, InputStream ins) {
        return maker.makeEntity(null, klass).read(Xmls.xml(ins).getDocumentElement());
    }

    public static <T> T fromXml(Class<T> klass, String str) {
        return maker.makeEntity(null, klass).read(Xmls.xml(new ByteArrayInputStream(str.getBytes())).getDocumentElement());
    }

    public static <T> String toXml(Object t) {
        return maker.makeEntity(null, t.getClass()).write(t, null);
    }

    public static <T> String toXml(T t, String tagName) {
        return maker.makeEntity(null, t.getClass()).write(t, tagName);
    }

    public static <T> void toXml(T t, String tagName, StringBuilder sb) {
        maker.makeEntity(null, t.getClass()).write(t, tagName, sb);
    }
}
