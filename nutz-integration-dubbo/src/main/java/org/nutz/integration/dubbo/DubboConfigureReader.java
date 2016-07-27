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

public class DubboConfigureReader {

    private static final Log log = Logs.get();
    
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
