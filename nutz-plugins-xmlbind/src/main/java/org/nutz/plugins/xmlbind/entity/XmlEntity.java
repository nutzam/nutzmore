package org.nutz.plugins.xmlbind.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;
import org.nutz.lang.util.Tag;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlEntity<T> {

    protected String name;
    protected Map<String, XmlEntityAttr> attrs;
    protected Map<String, XmlEntity<?>> nodes;
    protected String extAttrs;
    protected String extNodes;
    protected boolean simpleNode;
    protected transient Ejecting ejecting;
    protected transient Injecting injecting;
    protected transient Borning<T> borning;
    protected boolean useList;
    protected transient Class<T> klass;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Map<String, XmlEntityAttr> getAttrs() {
        return attrs;
    }
    public void setAttrs(Map<String, XmlEntityAttr> attrs) {
        this.attrs = attrs;
    }
    public Map<String, XmlEntity<?>> getNodes() {
        return nodes;
    }
    public void setNodes(Map<String, XmlEntity<?>> nodes) {
        this.nodes = nodes;
    }
    public String getExtAttrs() {
        return extAttrs;
    }
    public void setExtAttrs(String extAttrs) {
        this.extAttrs = extAttrs;
    }
    public String getExtNodes() {
        return extNodes;
    }
    public void setExtNodes(String extNodes) {
        this.extNodes = extNodes;
    }
    public boolean isSimpleNode() {
        return simpleNode;
    }
    public void setSimpleNode(boolean simpleNode) {
        this.simpleNode = simpleNode;
    }
    public Ejecting getEjecting() {
        return ejecting;
    }
    public void setEjecting(Ejecting ejecting) {
        this.ejecting = ejecting;
    }
    public Injecting getInjecting() {
        return injecting;
    }
    public void setInjecting(Injecting injecting) {
        this.injecting = injecting;
    }
    public Borning<T> getBorning() {
        return borning;
    }
    public void setBorning(Borning<T> borning) {
        this.borning = borning;
    }
    public boolean isUseList() {
        return useList;
    }
    public void setUseList(boolean useList) {
        this.useList = useList;
    }
    public Class<T> getKlass() {
        return klass;
    }
    public void setKlass(Class<T> klass) {
        this.klass = klass;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public T read(Element _ele) {
        T t = borning.born();
        // 处理attr属性
        for (Map.Entry<String, XmlEntityAttr> en : attrs.entrySet()) {
            String key = en.getKey();
            String value = _ele.getAttribute(key);
            if (!Strings.isBlank(value)) {
                en.getValue().getInjecting().inject(t, value);
            }
        }
        // 处理节点数据
        NodeList allChildren = _ele.getChildNodes();
        for (Map.Entry<String, XmlEntity<?>> en : nodes.entrySet()) {
            String key = en.getKey();
            List<Element> eles = new ArrayList<Element>();
            for (int i = 0; i < allChildren.getLength(); i++) {
                Node node = allChildren.item(i);
                if (node instanceof Element) {
                    if (node.getNodeName().equalsIgnoreCase(key)) {
                        eles.add((Element) node);
                    }
                }
            }
            if (eles.isEmpty())
                continue;
            XmlEntity<?> en2 = en.getValue();
            if (en2.isSimpleNode()) {
                if (en2.isUseList()) {
                    List<String> strs = new ArrayList<>(eles.size());
                    for (Element ele : eles) {
                        strs.add(ele.getTextContent());
                    }
                    en2.getInjecting().inject(t, strs);
                }
                else {
                    String value = eles.get(0).getTextContent();
                    en2.getInjecting().inject(t, value);
                }
            }
            else {
                ArrayList list = new ArrayList(eles.size());
                for (Element element : eles) {
                    list.add(en2.read(element));
                }
                if (en2.isUseList()) {
                    en2.getInjecting().inject(t, list);
                }
                else {
                    en2.getInjecting().inject(t, list.get(0));
                }
            }
        }
        return t;
    }
    
    @SuppressWarnings("unchecked")
    public String write(Object t, String tagName) {
        Tag tag = asTag((T)t, tagName);
        StringBuilder sb = new StringBuilder();
        tag.toXml(sb, 0);
        //return Xmls.HEAD + tag.toString(0);
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    public void write(Object t, String tagName, StringBuilder sb) {
        asTag((T)t, tagName).toXml(sb, 0);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Tag asTag(T t, String tagName) {
        if (tagName == null)
            tagName = name;
        Tag tag = Tag.NEW(tagName);
        // 处理attr属性
        for (Map.Entry<String, XmlEntityAttr> en : attrs.entrySet()) {
            String key = en.getKey();
            String value = (String)en.getValue().getEjecting().eject(t);
            if (!Strings.isBlank(value) || !en.getValue().isIgnoreBlank()) {
                tag.attr(key, value);
            }
        }
        // 处理节点数据
        for (Map.Entry<String, XmlEntity<?>> en : nodes.entrySet()) {
            String key = en.getKey();
            Object tmp = en.getValue().getEjecting().eject(t);
            if (tmp == null)
                continue;
            XmlEntity en2 = en.getValue();
            if (en2.isSimpleNode()) {
                if (en2.useList) {
                    Lang.each(tmp, new Each<Object>() {
                        public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                            tag.add(key).setText(String.valueOf(ele));
                        }
                    });
                }
                else {
                    tag.add(key).setText(tmp.toString());
                }
            }
            else if (en2.isUseList()) {
                List list = (List)tmp;
                for (Object obj : list) {
                    tag.add(en2.asTag(obj, en2.getName()));
                }
            }
            else {
                tag.add(en2.asTag(tmp, en2.getName()));
            }
        }
        return tag;
    }
}
