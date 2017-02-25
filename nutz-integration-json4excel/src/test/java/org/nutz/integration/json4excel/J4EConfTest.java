package org.nutz.integration.json4excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nutz.integration.json4excel.bean.Person;
import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;

public class J4EConfTest {

    @Test
    public void test_from_class() throws Exception {
        _test_(J4EConf.from(Person.class));
    }

    @Test
    public void test_from_file() throws Exception {
        _test_(J4EConf.from(J4EConfTest.class.getResourceAsStream("/PersonConf.js")));
    }

    @Test
    public void test_from_path() throws Exception {
        _test_(J4EConf.from("PersonConf.js"));
    }

    @Test
    public void test_from_confStr() throws Exception {
        String confStr = Files.read(Disks.absolute("PersonConf.js"));
        _test_(J4EConf.fromConf(confStr));
    }

    public void _test_(J4EConf jc) {
        assertTrue(jc.getSheetName().equals("人员"));
        assertEquals(jc.getColumns().size(), 4);

        // 检查column
        J4EColumn jc1 = jc.getColumns().get(0);
        assertTrue(jc1.getFieldName().equals("name"));
        assertTrue(jc1.getColumnName().equals("姓名"));

        J4EColumn jc2 = jc.getColumns().get(1);
        assertTrue(jc2.getFieldName().equals("age"));
        assertTrue(jc2.getColumnName().equals("年龄"));

        J4EColumn jc3 = jc.getColumns().get(2);
        assertTrue(jc3.getFieldName().equals("birthday"));
        assertTrue(jc3.getColumnName().equals("birthday"));

        J4EColumn jc4 = jc.getColumns().get(3);
        assertTrue(jc4.getFieldName().equals("income"));
        assertTrue(jc4.getColumnName().equals("收入"));
    }
}
