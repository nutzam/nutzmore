package org.nutz.plugins.sqlmanager.xml;

import org.junit.Assert;
import org.junit.Test;

public class XmlSqlManagerTest extends Assert {

    @Test
    public void testXmlSqlManager() {
        XmlSqlManager sqlManager = new XmlSqlManager();
        sqlManager.add(getClass().getClassLoader().getResourceAsStream("sqls/simple.xml"));

        assertTrue(sqlManager.count() > 0);
        assertNotNull(sqlManager.get("user.fetch.by_name"));
        System.out.println(sqlManager.get("user.fetch.by_name"));
        assertNotNull(sqlManager.get("user.count"));
        System.out.println(sqlManager.get("user.count"));

        sqlManager.setPaths(new String[]{"sqls/"});

        assertTrue(sqlManager.count() > 0);
        assertNotNull(sqlManager.get("user.fetch.by_name"));
        System.out.println(sqlManager.get("user.fetch.by_name"));
        assertNotNull(sqlManager.get("user.count"));
        System.out.println(sqlManager.get("user.count"));
    }

}
