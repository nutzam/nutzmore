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

public class J4ETest extends TestUtil {

    public static final String TMPDIR = System.getProperty("java.io.tmpdir");

    @Test
    public void test_fromExcel() throws Exception {
        File e = new File(Disks.absolute("test.xls"));
        List<Person> plist = J4E.fromExcel(Streams.fileIn(e), Person.class, null);
        assertEquals(3, plist.size());
        _test_excel_(e, 3);
    }

    @Test
    public void test_toExcel_01() throws Exception {
        int pn = 1;
        File e = new File(TMPDIR, "e.xls");
        Files.createFileIfNoExists(e);
        assertTrue(J4E.toExcel(new FileOutputStream(e), PL(pn), null));
        _test_excel_(e, pn);
        Files.deleteFile(e);
    }

    @Test
    public void test_toExcel_02() throws Exception {
        int pn = 2;
        File e = new File(TMPDIR, "e2.xls");
        Files.createFileIfNoExists(e);
        assertTrue(J4E.toExcel(new FileOutputStream(e), PL(pn), null));
        _test_excel_(e, pn);
        Files.deleteFile(e);
    }

    @Test
    public void test_toExcel_03() throws Exception {
        int pn = 3;
        File e = new File(TMPDIR, "e3.xls");
        Files.createFileIfNoExists(e);
        assertTrue(J4E.toExcel(new FileOutputStream(e), PL(pn), null));
        _test_excel_(e, pn);
        // Files.deleteFile(e);
    }

}
