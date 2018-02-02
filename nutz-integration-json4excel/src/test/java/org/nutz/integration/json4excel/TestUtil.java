package org.nutz.integration.json4excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.nutz.integration.json4excel.bean.Person;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;

public abstract class TestUtil {

    public static final String TMPDIR = Disks.absolute("~/tmp/");

    public static String FD(Date d) {
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    // date by format
    public static Date DBF(String dstr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dstr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // person-list
    public static List<Person> PL(int num) {
        List<Person> plist = new ArrayList<Person>();
        if (num >= 1) {
            Person p = new Person();
            p.setName("pw");
            p.setAge(28);
            p.setBirthday(DBF("1986-08-26"));
            p.setIncome(199.23321);
            plist.add(p);
        }
        if (num >= 2) {
            Person p = new Person();
            p.setName("zozoh");
            p.setAge(37);
            p.setBirthday(DBF("1979-09-21"));
            p.setIncome(3888.98325);
            plist.add(p);
        }
        if (num >= 3) {
            Person p = new Person();
            p.setName("wendal");
            p.setAge(29);
            p.setBirthday(DBF("1985-10-01"));
            p.setIncome(348823.29223);
            plist.add(p);
        }

        return plist;
    }

    public static void _test_excel_(File ef, int dn) {
        List<Person> plist = J4E.fromExcel(Streams.fileIn(ef), Person.class, null);
        if (dn > 0) {
            _test_person(plist.get(0), 0);
        }
        if (dn > 1) {
            _test_person(plist.get(1), 1);
        }
        if (dn > 2) {
            _test_person(plist.get(2), 2);
        }
    }

    public static void _test_person(Person p, int dn) {
        if (dn == 0) {
            assertTrue("pw".equals(p.getName()));
            assertEquals(28, p.getAge());
            assertTrue("1986-08-26".equals(FD(p.getBirthday())));
            assertEquals(199.23321, p.getIncome(), 0);
        }
        if (dn == 1) {
            assertTrue("zozoh".equals(p.getName()));
            assertEquals(37, p.getAge());
            assertTrue("1979-09-21".equals(FD(p.getBirthday())));
            assertEquals(3888.98325, p.getIncome(), 0);
        }
        if (dn == 2) {
            assertTrue("wendal".equals(p.getName()));
            assertEquals(29, p.getAge());
            assertTrue("1985-10-01".equals(FD(p.getBirthday())));
            assertEquals(348823.29223, p.getIncome(), 0);
        }
    }
}
