package org.nutz.integration.json4excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.bean.Person;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;

public class J4ETest2007 extends TestUtil {

    @Test
    public void test_toExcel_01() throws Exception {
        System.out.println(TMPDIR);
        int pn = 3;
        File e = new File(TMPDIR, "test2007.xlsx");
        Files.createFileIfNoExists(e);
        assertTrue(J4E.toExcel(new FileOutputStream(e),
                               PL(pn),
                               J4EConf.from(Person.class).setUse2007(true)));
        _test_excel_(e, pn);
    }

    @Test
    public void test_fromExcel() throws Exception {
        File e = new File(Disks.absolute("test2007.xlsx"));
        List<Person> plist = J4E.fromExcel(Streams.fileIn(e), Person.class, null);
        assertEquals(3, plist.size());
        _test_excel_(e, 3);
    }
}
