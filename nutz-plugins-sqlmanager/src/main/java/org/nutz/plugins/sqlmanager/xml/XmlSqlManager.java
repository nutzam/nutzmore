package org.nutz.plugins.sqlmanager.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.nutz.dao.impl.FileSqlManager;
import org.nutz.lang.Xmls;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlSqlManager extends FileSqlManager {

    private static final Log log = Logs.get();

    public XmlSqlManager() {
    }

    public XmlSqlManager(String... paths) {
        super(paths);
    }

    public void refresh() {
        if (paths != null) {
            for (String path : paths) {
                List<NutResource> res = Scans.me().scan(path, "^.+[.]xml$");
                for (NutResource nr : res) {
                    log.debug("add xml " + nr.getName());
                    try {
                        add(nr.getInputStream());
                    }
                    catch (IOException e) {
                        log.debug("fail at " + nr.getName(), e);
                    }
                }
            }
        }
    }

    public void add(InputStream ins) {
        Document doc = Xmls.xml(ins);
        doc.normalize();
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if ("sql".equals(node.getNodeName())) {
                Element ele = (Element) node;
                String key = ele.getAttribute("key");
                String value = Xmls.getText(ele);
                addSql(key, value);
            }
        }
    }
}
