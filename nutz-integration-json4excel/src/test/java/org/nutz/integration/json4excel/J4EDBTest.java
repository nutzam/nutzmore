package org.nutz.integration.json4excel;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.integration.json4excel.bean.Person;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;

public class J4EDBTest extends DbUtil {

    @Override
    @Before
    public void before() {
        dao.create(Person.class, true);
    }

    @Test
    public void test_fromExceltoDB() throws Exception {
        File e = new File(Disks.absolute("test.xls"));
        List<Person> plist = J4E.fromExcel(Streams.fileIn(e), Person.class, null);
        dao.insert(plist);
        _test_person(dao.fetch(Person.class, Cnd.where("name", "=", "pw")), 0);
        _test_person(dao.fetch(Person.class, Cnd.where("name", "=", "zozoh")), 1);
        _test_person(dao.fetch(Person.class, Cnd.where("name", "=", "wendal")), 2);
    }

    @Test
    public void test_toExcelfromDB() throws Exception {
        File e = new File(Disks.absolute("test.xls"));
        List<Person> plist = J4E.fromExcel(Streams.fileIn(e), Person.class, null);
        dao.insert(plist);
        // 导出到本地
        List<Person> people = dao.query(Person.class, null);
        File exportF = Files.createFileIfNoExists2("~/人员.xls");
        J4E.toExcel(exportF, people, null);
    }
}
