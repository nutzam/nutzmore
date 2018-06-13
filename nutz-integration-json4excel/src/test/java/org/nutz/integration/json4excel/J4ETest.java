package org.nutz.integration.json4excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.bean.Hero;
import org.nutz.integration.json4excel.bean.Person;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;

public class J4ETest extends TestUtil {

    public static final String TMPDIR = Disks.absolute("~/tmp/");

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

    @Test
    public void test_exportImage() throws Exception {
        List<Hero> heroList = new ArrayList<Hero>();
        Hero h1 = new Hero();
        h1.name = "金刚狼";
        h1.avatar1 = Streams.fileIn(new File(Disks.absolute("金刚狼.jpg")));
        Hero h2 = new Hero();
        h2.name = "死侍";
        h2.avatar1 = Streams.fileIn(new File(Disks.absolute("死侍.jpg")));

        heroList.add(h1);
        heroList.add(h2);

        File outFile = Files.createFileIfNoExists2(Disks.normalize("~/tmp/hero.xls"));

        J4E.toExcel(outFile, heroList, null);
    }

}
