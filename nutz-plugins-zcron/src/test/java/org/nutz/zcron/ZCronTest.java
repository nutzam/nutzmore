package org.nutz.zcron;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.zcron.ZCron;
import org.nutz.zcron.CronObj;
import org.nutz.zcron.CronOverlapor;

public class ZCronTest {

    @Test
    public void test_month_XXX_1_5() throws Exception {
        ZCron cr;
        // ............................................
        cr = ZCron.parse("0 0 0 1-5 * ?");
        assertTrue(cr.matchDate("2015-07-01"));
        assertTrue(cr.matchDate("2015-07-05"));
        assertFalse(cr.matchDate("2015-07-06"));
        assertFalse(cr.matchDate("2015-06-30"));
    }

    @Test
    public void test_month_jul_1_5() throws Exception {
        ZCron cr;
        // ............................................
        cr = ZCron.parse("0 0 0 1-5 JUL ?");
        assertTrue(cr.matchDate("2015-07-01"));
        assertTrue(cr.matchDate("2015-07-05"));
        assertFalse(cr.matchDate("2015-07-06"));
        assertFalse(cr.matchDate("2015-06-30"));
    }

    @Test
    public void test_month_7_1_5() throws Exception {
        ZCron cr;
        // ............................................
        cr = ZCron.parse("0 0 0 1-5 7 ?");
        assertTrue(cr.matchDate("2015-07-01"));
        assertTrue(cr.matchDate("2015-07-05"));
        assertFalse(cr.matchDate("2015-07-06"));
        assertFalse(cr.matchDate("2015-06-30"));
    }

    @Test
    public void test_multi_date_scope() {
        ZCron cr;

        // ............................................
        cr = ZCron.parse("0 0 0 1-3,6-8 * ?");
        assertTrue(cr.matchDate("2012-04-02"));
        assertTrue(cr.matchDate("2014-06-07"));
        assertFalse(cr.matchDate("2011-05-04"));
    }

    @Test
    public void test_simple_match_day() {
        ZCron cr;

        // ............................................
        cr = ZCron.parse("0 0 0 * 4,6 ?");
        assertTrue(cr.matchDate("2012-04-05"));
        assertTrue(cr.matchDate("2014-06-06"));
        assertFalse(cr.matchDate("2011-05-07"));

        // ............................................
        cr = ZCron.parse("0 0 0 * * SUN#2");
        assertTrue(cr.matchDate("2012-02-12"));
        assertFalse(cr.matchDate("2012-02-05"));
        assertFalse(cr.matchDate("2012-02-19"));

        // ............................................
        cr = ZCron.parse("0 0 0 * * ?");
        assertTrue(cr.matchDate("2012-03-15"));

        // ............................................
        cr = ZCron.parse("0 0 0 W * ?");
        assertTrue(cr.matchDate("2012-02-07"));
        assertFalse(cr.matchDate("2012-02-05"));
        assertFalse(cr.matchDate("2012-02-11"));

        // ............................................
        cr = ZCron.parse("0 0 0 * * SUN-MON");
        assertTrue(cr.matchDate("2012-02-05"));
        assertTrue(cr.matchDate("2012-02-06"));
        assertFalse(cr.matchDate("2012-02-07"));

        // ............................................
        cr = ZCron.parse("0 0 0 * * 1-2");
        assertTrue(cr.matchDate("2012-02-05"));
        assertTrue(cr.matchDate("2012-02-06"));
        assertFalse(cr.matchDate("2012-02-07"));

        // ............................................
        cr = ZCron.parse("0 0 0 * 4-6 ?");
        assertTrue(cr.matchDate("2012-04-05"));
        assertTrue(cr.matchDate("2018-05-06"));
        assertTrue(cr.matchDate("2016-06-06"));
        assertFalse(cr.matchDate("2012-03-07"));
        assertFalse(cr.matchDate("2012-07-07"));

        // ............................................
        cr = ZCron.parse("0 0 0 1 * ?");
        assertTrue(cr.matchDate("2012-02-01"));
        assertTrue(cr.matchDate("2012-03-01"));
        assertFalse(cr.matchDate("2012-02-29"));
        assertFalse(cr.matchDate("2012-05-14"));

        // ............................................
        cr = ZCron.parse("0 0 0 1-7 * ?");
        assertTrue(cr.matchDate("2012-02-01"));
        assertTrue(cr.matchDate("2012-03-07"));
        assertFalse(cr.matchDate("2012-02-29"));
        assertFalse(cr.matchDate("2012-05-14"));

        // ............................................
        cr = ZCron.parse("0 0 0 1L * ?");
        assertTrue(cr.matchDate("2012-02-29"));
        assertTrue(cr.matchDate("2012-03-31"));
        assertFalse(cr.matchDate("2012-02-28"));
        assertFalse(cr.matchDate("2012-05-14"));

    }

    @Test
    public void test_simple_match_time() {
        ZCron cr;

        // ............................................
        cr = ZCron.parse("* 9/25 * * * ?");
        assertFalse(cr.matchTime("00:00:00"));

        assertFalse(cr.matchTime("12:08:23"));
        assertTrue(cr.matchTime("09:09:45"));
        assertFalse(cr.matchTime("12:10:23"));

        assertFalse(cr.matchTime("12:33:23"));
        assertTrue(cr.matchTime("09:34:45"));
        assertFalse(cr.matchTime("12:35:23"));

        assertFalse(cr.matchTime("12:58:23"));
        assertTrue(cr.matchTime("09:59:45"));
        assertFalse(cr.matchTime("12:00:23"));

        assertTrue(cr.matchTime("23:59:59"));

        // ............................................
        cr = ZCron.parse("* 1-10 * * * ?");
        assertTrue(cr.matchTime("12:01:23"));
        assertFalse(cr.matchTime("00:00:00"));
        assertTrue(cr.matchTime("09:03:45"));
        assertTrue(cr.matchTime("15:10:28"));
        assertFalse(cr.matchTime("23:59:59"));

        // ............................................
        cr = ZCron.parse("0 1-10 0 * * ?");
        assertTrue(cr.matchTime("00:01:00"));
        assertFalse(cr.matchTime("00:00:00"));
        assertTrue(cr.matchTime("00:03:00"));
        assertTrue(cr.matchTime("00:10:00"));
        assertFalse(cr.matchTime("23:59:59"));

        // ............................................
        cr = ZCron.parse("0 0 0 * * ?");
        assertTrue(cr.matchTime("00:00:00"));
        assertFalse(cr.matchTime("00:00:01"));
        assertFalse(cr.matchTime("00:00:02"));
        assertFalse(cr.matchTime("23:59:59"));

        // ............................................
        cr = ZCron.parse("* 1,6,10 * * * ?");
        assertFalse(cr.matchTime("00:00:00"));

        assertFalse(cr.matchTime("12:00:23"));
        assertTrue(cr.matchTime("12:01:23"));
        assertFalse(cr.matchTime("12:02:23"));

        assertFalse(cr.matchTime("12:05:23"));
        assertTrue(cr.matchTime("09:06:45"));
        assertFalse(cr.matchTime("12:07:23"));

        assertFalse(cr.matchTime("12:09:23"));
        assertTrue(cr.matchTime("15:10:28"));
        assertFalse(cr.matchTime("12:11:23"));

        assertFalse(cr.matchTime("23:59:59"));

    }

    @Test
    public void test_overlap_by_hour() {
        OVERL(24,
              "2012-02-08",
              Lang.array("0 0 5-7 * * ?", "0 0 5-6 * 2 ?", "0 0 5-8 * 3 ?"),
              Lang.array("5:0.E,1.E", "6:0.E,1.E", "7:0.E"));

        OVERL(24,
              "2012-03-08",
              Lang.array("0 0 5-7 * * ?", "0 0 5-6 * 2 ?", "0 0 5-8 * 3 ?"),
              Lang.array("5:0.E,2.E", "6:0.E,2.E", "7:0.E,2.E", "8:2.E"));
    }

    @Test
    public void test_fill_by_hour() {
        Fhh("2012-02-08", "0 0 5-8 * * ?", "5:E", "6:E", "7:E", "8:E");
        Fhh("2012-02-08", "0,3,4,5 0/1 4-8 * * ?", "4:E", "5:E", "6:E", "7:E", "8:E");

        Fhh("2012-02-08", "0 0 5,8 * * ?", "5:E", "8:E");
        Fhh("2012-02-08", "0,3,4,5 0/1 5,8 * * ?", "5:E", "8:E");
    }

    @Test
    public void test_fill_by_min() {
        Fmm("2012-02-08", "0 1,2 0,1 * * ?", "1:E", "2:E", "61:E", "62:E");
    }

    @Test
    public void test_fill_by_sec() {
        Fss("2012-02-08", "0,1 0 0,1 * * ?", "0:E", "1:E", "3600:E", "3601:E");
    }

    @Test
    public void test_fill_partly() {
        FILL(hh(), "2012-02-08", "0 0 8-10 * * ?", 9, 3, "9:E", "10:E");
    }

    @Test
    public void test_simple_parse() {
        ZCron.parse("0 0 0 7-13 JUL ?");
    }

    /*----------------------------------------------------------帮助函数们-------*/

    private static void Fhh(String ds, String rus, String... expect) {
        FILL(hh(), ds, rus, expect);
    }

    private static void Fmm(String ds, String rus, String... expect) {
        FILL(mm(), ds, rus, expect);
    }

    private static void Fss(String ds, String rus, String... expect) {
        FILL(ss(), ds, rus, expect);
    }

    private static void FILL(String[] array,
                             String ds,
                             String rus,
                             int off,
                             int len,
                             String... expect) {
        FILL(array, ds, rus, off, len, 86400 / array.length, expect);
    }

    private static void FILL(String[] array, String ds, String rus, String... expect) {
        FILL(array, ds, rus, 0, array.length, 86400 / array.length, expect);
    }

    private static void FILL(String[] array,
                             String ds,
                             String rus,
                             int off,
                             int len,
                             int unit,
                             String... expect) {
        // 创建+解析
        ZCron cr = ZCron.parse(rus);

        // 执行
        cr.fill(array, "E", Times.C(ds), off, len, unit);

        // 压缩结果
        List<CronObj<String>> qzos = ZCron.compactAll(array);
        String[] ss = new String[qzos.size()];

        // 结果可显示
        int i = 0;
        for (CronObj<String> qzo : qzos) {
            ss[i++] = qzo.toString();
        }

        // 验证
        if (ss.length != expect.length) {
            fail(FMT("expect length %d, but is was %s !", expect.length, ss.length));
        }
        for (int i1 = 0; i1 < ss.length; i1++) {
            if (!ss[i1].equals(expect[i1])) {
                fail(FMT("!expect '%s' but '%s'", expect[i1], ss[i1]));
            }
        }
    }

    private static void OVERL(int scale, String ds, String[] russ, String[] expect) {
        CronOverlapor[] qos = new CronOverlapor[scale];
        int i = 0;
        for (String rus : russ) {
            // 创建
            ZCron qz = ZCron.parse(rus);
            // 执行
            qz.overlapBy(qos, (i++) + ".E", ds);
        }

        // 压缩结果
        List<CronObj<CronOverlapor>> qzos = ZCron.compactAll(qos);
        String[] ss = new String[qzos.size()];

        // 结果可显示
        i = 0;
        for (CronObj<CronOverlapor> qzo : qzos) {
            ss[i++] = qzo.toString();
        }

        // 验证
        if (ss.length != expect.length) {
            fail(FMT("expect length %d, but is was %s !", expect.length, ss.length));
        }
        for (int i1 = 0; i1 < ss.length; i1++) {
            if (!ss[i1].equals(expect[i1])) {
                fail(FMT("!expect '%s' but '%s'", expect[i1], ss[i1]));
            }
        }
    }

    private static String FMT(String fmt, Object... args) {
        return String.format(fmt, args);
    }

    private static String[] hh() {
        return new String[24];
    }

    private static String[] mm() {
        return new String[24 * 60];
    }

    private static String[] ss() {
        return new String[24 * 60 * 60];
    }
}
