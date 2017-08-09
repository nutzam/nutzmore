package org.nutz.plugins.sqlmanager.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.SqlManager;
import org.nutz.dao.SqlNotFoundException;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Xmls;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlSqlManager implements SqlManager {

    private static final Log log = Logs.get();

    protected Map<String, String> sqls = new ConcurrentHashMap<String, String>();

    protected String[] paths;

    public XmlSqlManager() {

    }

    public XmlSqlManager(String... paths) {
        setPaths(paths);
    }

    public String get(String key) throws SqlNotFoundException {
        return sqls.get(key);
    }

    public Sql create(String key) throws SqlNotFoundException {
        return Sqls.create(get(key));
    }

    public List<Sql> createCombo(String... keys) {
        List<Sql> list = new ArrayList<>(keys.length);
        for (String key : keys) {
            list.add(create(key));
        }
        return list;
    }

    public int count() {
        return sqls.size();
    }

    public String[] keys() {
        return sqls.keySet().toArray(new String[sqls.size()]);
    }

    public void refresh() {
        sqls.clear();
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

    public void addSql(String key, String value) {
        sqls.put(key, value);
    }

    public void remove(String key) {
        sqls.remove(key);
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
        refresh();
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
