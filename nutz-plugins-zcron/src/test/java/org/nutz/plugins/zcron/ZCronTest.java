package org.nutz.plugins.zcron;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.plugins.zcron.CronObj;
import org.nutz.plugins.zcron.CronOverlapor;
import org.nutz.plugins.zcron.ZCron;

public class ZCronTest {

    @Test
    public void test_set_part() {
        ae_set_part("0 5,8 0 * * ?", "T[12:23,18:32]{0/30m} * * ?", "0:: T[12:23,18:32]{0/30m}");
        ae_set_part("0 0 0 * * ?", "0 3/6 8-10,13 * * ?", "1:: 0 3/6 8-10,13");
        ae_set_part("0 0 0 * * ?", "0 0 0 W 6,8 ? 2017-2019", "2:: W 6,8 ? 2017-2019");
        ae_set_part("D[20170725,20170921) T[12:23,18:32]{0/30m}",
                    "T[12:23,18:32]{0/30m} D(20180725,20180921]",
                    "3:: D(20180725,20180921]");
        ae_set_part("D[20170725,20170921) 0 0 0 * * 1,5",
                    "0 0 0 * * 1,5 D(20180725,20180921]",
                    "3:: D(20180725,20180921]");
        ae_set_part("D[20170725,20170921) 0 0 0 * * 1,5",
                    "0 0 0 * * 1,5 D(20180725,20180921]",
                    "3:: D(20180725,20180921]");
        ae_set_part("D[20170725,20170921) 0 0 0 * * 1,5",
                    "T{2:00} * * 1,5",
                    "0:: T{2:00}",
                    "3:: null");
        ae_set_part("T{2:00} 0 0 0 * * 1,5", "0 5,9 12 * * 1,5", "0:: null", "1:: 0 5,9 12");
    }

    @Test
    public void test_toString_0() {
        aeS("D[20170725,20170921) T[12:23,18:32]{0/30m}",
            "T[12:23,18:32]{0/30m} D[20170725,20170921)");
        aeS("T[12:23,18:32] 0 5/7 0 * * ? *", "T[12:23,18:32] 0 5/7 0 * * ?");
        aeS("D[20170725,20170921) 0 5/7 0 * * ? * ", "0 5/7 0 * * ? D[20170725,20170921)");
        aeS("D[20170725,20170921) 0 0 0 * * ? * ", "0 0 0 * * ? D[20170725,20170921)");
        aeS("D[20170725,20170921) 0 0 0 * * ? * T{2:00,3:00}", "T{2:00,3:00} D[20170725,20170921)");
        aeS("D[20170725,20170921) 0 0 0 * * ? * T[12:23,16:46){0/20m}",
            "T[12:23,16:46){0/20m} D[20170725,20170921)");
        aeS("0 0 0 * * ?", "0 0 0 * * ?");
        aeS("0 0 ? * * ?", "0 0 ? * * ?");
        aeS("0 ? ? * * ?", "0 ? ? * * ?");
        aeS("0 0 8-11,13-18 * * ?", "0 0 8-11,13-18 * * ?");
        aeS("0 0 0 7-13 JUL ?", "0 0 0 7-13 JUL ?");
        aeS("0 0 0 1,3,5 * ?", "0 0 0 1,3,5 * ?");
        aeS("0 0 8/3 * * ?", "0 0 8/3 * * ?");
        aeS("0 0 0 4L * ?", "0 0 0 4L * ?");
        aeS("0 0 0 W * ?", "0 0 0 W * ?");
        aeS("0 0 0 1LW * ?", "0 0 0 1LW * ?");
        aeS("0 0 0 6LW * ?", "0 0 0 6LW * ?");
        aeS("0 0 0 * * 1-3", "0 0 0 * * 1-3");
        aeS("0 0 0 * * 1,2-4,7", "0 0 0 * * 1,2-4,7");
        aeS("0 0 0 * * FRI#2", "0 0 0 * * FRI#2");
        aeS("0 0 0 * * 2#4", "0 0 0 * * 2#4");
        aeS("0 0/5 8,10-14,23 * * ?", "0 0/5 8,10-14,23 * * ?");
        aeS("0 0 0,1 * * ?", "0 0 0,1 * * ?");
    }

    @Test
    public void test_text_ext_0() {
        aeT("D[20170725,) 0 0 0 * * ?", "从2017年7月25日开始，每天的0点0分0秒");
        aeT("D[,20170725) 0 0 0 * * ?", "截止至2017年7月25日（不包含），每天的0点0分0秒");
        aeT("T[12:00,) 0 0 0 * * ?", "每天的12:00到23:59:59（不包含），0点0分0秒");
        aeT("T[,12:00) 0 0 0 * * ?", "每天的00:00到12:00（不包含），0点0分0秒");
        aeT("D[20170725,20170921) T[12:23,18:32]{0/30m}",
            "2017年7月25日至9月21日（不包含），每天的12:23到18:32，每隔30分钟；");
        aeT("D(20170725,20170921] T(12:23,18:32]{4/30m}",
            "2017年7月25日（不包含）至9月21日，每天的12:23（不包含）到18:32，经过4分钟后开始，每隔30分钟；");
        aeT("D(20170725,20170921] T(12:23,18:32]{4s/2h}",
            "2017年7月25日（不包含）至9月21日，每天的12:23（不包含）到18:32，经过4秒钟后开始，每隔2小时；");
        aeT("D(20170725,20170921] T(12:23,18:32]{>1/2h}",
            "2017年7月25日（不包含）至9月21日，每天的12:23（不包含）到18:32，起始时间按1小时全天对齐，每隔2小时；");
        aeT("D(20170725,20170921] T{1:20,2:3,18:05}",
            "2017年7月25日（不包含）至9月21日，每天的01:20, 02:03, 18:05");
        aeT("* * ? T[6:00,8:30]{0/30m} T[22:00,22:30]{0/10m}",
            "每天的06:00到08:30，每隔30分钟；22:00到22:30，每隔10分钟；");
    }

    @Test
    public void test_text_std_1() {
        aeT("0 0 ? * * ? *", "每天的每小时的0分0秒");
        aeT("0 0 ? * 1,5,6 1-3 *", "每年的一月,五月,六月的周日至周二的每天每小时的0分0秒");
        aeT("0 0 ? * * ? 2017-2020", "2017年至2020年的每天的每小时的0分0秒");
        aeT("0 0 ? * * ? 2017/2", "从2017年开始每隔2年的每天的每小时的0分0秒");
        aeT("0 0 ? * * ? 2017,2019,2034", "2017年,2019年,2034年的每天的每小时的0分0秒");
    }

    @Test
    public void test_text_std_0() {
        aeT("0 0 0 * * ?", "每天的0点0分0秒");
        aeT("0 0 ? * * ?", "每天的每小时的0分0秒");
        aeT("0 ? ? * * ?", "每天的每小时的每分钟的0秒");
        aeT("0 0 8-11,13-18 * * ?", "每天的8点至11点,13点至18点0分0秒");
        aeT("0 0 0 7-13 JUL ?", "每年的七月的7号至13号的0点0分0秒");
        aeT("0 0 0 1,3,5 * ?", "每月的1号,3号,5号的0点0分0秒");
        aeT("0 0 8/3 * * ?", "每天的从8点开始每3小时0分0秒");
        aeT("0 0 0 4L * ?", "每月的倒数第4日的0点0分0秒");
        aeT("0 0 0 W * ?", "每月的所有工作日的0点0分0秒");
        aeT("0 0 0 1LW * ?", "每月的最后一日最近的工作日的0点0分0秒");
        aeT("0 0 0 6LW * ?", "每月的倒数第6日最近的工作日的0点0分0秒");
        aeT("0 0 0 * * 1-3", "每月的周日至周二的每天0点0分0秒");
        aeT("0 0 0 * * 1,2-4,7", "每月的周日,周一至周三,周六的每天0点0分0秒");
        aeT("0 0 0 * * FRI#2", "每月的第2个周五的每天0点0分0秒");
        aeT("0 0 0 * * 2#4", "每月的第4个周一的每天0点0分0秒");
        aeT("0 0/5 8,10-14,23 * * ?", "每天的8点,10点至14点,23点从0分开始每5分钟");
        aeT("0 0 0,1 * * ?", "每天的0点,1点0分0秒");
    }

    @Test
    public void test_ext_multi_time_region() {
        ZCron cr;
        // ............................................
        cr = new ZCron("* * ? T[6:00,8:30]{0/30m} T[22:00,22:30]{0/10m}");

        assertTrue(cr.matchTime("6:00"));
        assertTrue(cr.matchTime("6:30"));
        assertTrue(cr.matchTime("7:00"));
        assertTrue(cr.matchTime("7:30"));
        assertTrue(cr.matchTime("8:00"));
        assertTrue(cr.matchTime("8:30"));
        assertTrue(cr.matchTime("22:00"));
        assertTrue(cr.matchTime("22:10"));
        assertTrue(cr.matchTime("22:20"));
        assertTrue(cr.matchTime("22:30"));

        assertFalse(cr.matchTime("5:59"));
        assertFalse(cr.matchTime("6:01"));
        assertFalse(cr.matchTime("6:59"));
        assertFalse(cr.matchTime("7:01"));
        assertFalse(cr.matchTime("7:59"));
        assertFalse(cr.matchTime("8:01"));
        assertFalse(cr.matchTime("8:59"));
        assertFalse(cr.matchTime("21:59"));
        assertFalse(cr.matchTime("22:01"));
        assertFalse(cr.matchTime("22:11"));
        assertFalse(cr.matchTime("22:13"));
        assertFalse(cr.matchTime("22:31"));
    }

    @Test
    public void test_ext_normal() {
        ZCron cr;
        // ............................................
        cr = new ZCron("D[20170725,20170921] T[12:23,18:32]{0/30m}");
        assertTrue(cr.matchDate("2017-07-25"));
        assertTrue(cr.matchDate("2017-09-21"));
        assertFalse(cr.matchDate("2017-07-24"));
        assertFalse(cr.matchDate("2015-09-22"));

        assertFalse(cr.matchTime("11:53"));
        assertTrue(cr.matchTime("12:23"));
        assertTrue(cr.matchTime("12:53"));
        assertTrue(cr.matchTime("13:23"));
        assertTrue(cr.matchTime("13:53"));
        assertTrue(cr.matchTime("14:23"));
        assertTrue(cr.matchTime("14:53"));
        assertTrue(cr.matchTime("16:23"));
        assertTrue(cr.matchTime("16:53"));
        assertTrue(cr.matchTime("17:23"));
        assertTrue(cr.matchTime("17:53"));
        assertTrue(cr.matchTime("18:23"));
        assertFalse(cr.matchTime("18:53"));

    }

    @Test
    public void test_year() throws Exception {
        ZCron cr;
        // ............................................
        cr = new ZCron("0 0 ? * * ? 2017-2020");
        assertFalse(cr.matchDate("2016-12-31"));
        assertTrue(cr.matchDate("2017-12-20"));
        assertTrue(cr.matchDate("2018-01-12"));
        assertTrue(cr.matchDate("2019-08-29"));
        assertTrue(cr.matchDate("2020-11-18"));
        assertFalse(cr.matchDate("2021-01-01"));

        // ............................................
        cr = new ZCron("0 0 ? * * ? 2017/2");
        assertFalse(cr.matchDate("2016-12-31"));
        assertTrue(cr.matchDate("2017-01-01"));
        assertFalse(cr.matchDate("2018-12-31"));
        assertTrue(cr.matchDate("2019-01-01"));
        assertFalse(cr.matchDate("2020-12-31"));
        assertTrue(cr.matchDate("2021-01-01"));
        assertFalse(cr.matchDate("2022-12-31"));
        assertTrue(cr.matchDate("2023-01-01"));
        assertFalse(cr.matchDate("2024-12-31"));
        assertTrue(cr.matchDate("2025-01-01"));
    }

    @Test
    public void test_month_XXX_1_5() throws Exception {
        ZCron cr;
        // ............................................
        cr = new ZCron("0 0 0 1-5 * ?");
        assertTrue(cr.matchDate("2015-07-01"));
        assertTrue(cr.matchDate("2015-07-05"));
        assertFalse(cr.matchDate("2015-07-06"));
        assertFalse(cr.matchDate("2015-06-30"));
    }

    @Test
    public void test_month_jul_1_5() throws Exception {
        ZCron cr;
        // ............................................
        cr = new ZCron("0 0 0 1-5 JUL ?");
        assertTrue(cr.matchDate("2015-07-01"));
        assertTrue(cr.matchDate("2015-07-05"));
        assertFalse(cr.matchDate("2015-07-06"));
        assertFalse(cr.matchDate("2015-06-30"));
    }

    @Test
    public void test_month_7_1_5() throws Exception {
        ZCron cr;
        // ............................................
        cr = new ZCron("0 0 0 1-5 7 ?");
        assertTrue(cr.matchDate("2015-07-01"));
        assertTrue(cr.matchDate("2015-07-05"));
        assertFalse(cr.matchDate("2015-07-06"));
        assertFalse(cr.matchDate("2015-06-30"));
    }

    @Test
    public void test_multi_date_scope() {
        ZCron cr;

        // ............................................
        cr = new ZCron("0 0 0 1-3,6-8 * ?");
        assertTrue(cr.matchDate("2012-04-02"));
        assertTrue(cr.matchDate("2014-06-07"));
        assertFalse(cr.matchDate("2011-05-04"));
    }

    @Test
    public void test_simple_match_day() {
        ZCron cr;

        // ............................................
        cr = new ZCron("0 0 0 * 4,6 ?");
        assertTrue(cr.matchDate("2012-04-05"));
        assertTrue(cr.matchDate("2014-06-06"));
        assertFalse(cr.matchDate("2011-05-07"));

        // ............................................
        cr = new ZCron("0 0 0 * * SUN#2");
        assertTrue(cr.matchDate("2012-02-12"));
        assertFalse(cr.matchDate("2012-02-05"));
        assertFalse(cr.matchDate("2012-02-19"));

        // ............................................
        cr = new ZCron("0 0 0 * * ?");
        assertTrue(cr.matchDate("2012-03-15"));

        // ............................................
        cr = new ZCron("0 0 0 W * ?");
        assertTrue(cr.matchDate("2012-02-07"));
        assertFalse(cr.matchDate("2012-02-05"));
        assertFalse(cr.matchDate("2012-02-11"));

        // ............................................
        cr = new ZCron("0 0 0 * * SUN-MON");
        assertTrue(cr.matchDate("2012-02-05"));
        assertTrue(cr.matchDate("2012-02-06"));
        assertFalse(cr.matchDate("2012-02-07"));

        // ............................................
        cr = new ZCron("0 0 0 * * 1-2");
        assertTrue(cr.matchDate("2012-02-05"));
        assertTrue(cr.matchDate("2012-02-06"));
        assertFalse(cr.matchDate("2012-02-07"));

        // ............................................
        cr = new ZCron("0 0 0 * 4-6 ?");
        assertTrue(cr.matchDate("2012-04-05"));
        assertTrue(cr.matchDate("2018-05-06"));
        assertTrue(cr.matchDate("2016-06-06"));
        assertFalse(cr.matchDate("2012-03-07"));
        assertFalse(cr.matchDate("2012-07-07"));

        // ............................................
        cr = new ZCron("0 0 0 1 * ?");
        assertTrue(cr.matchDate("2012-02-01"));
        assertTrue(cr.matchDate("2012-03-01"));
        assertFalse(cr.matchDate("2012-02-29"));
        assertFalse(cr.matchDate("2012-05-14"));

        // ............................................
        cr = new ZCron("0 0 0 1-7 * ?");
        assertTrue(cr.matchDate("2012-02-01"));
        assertTrue(cr.matchDate("2012-03-07"));
        assertFalse(cr.matchDate("2012-02-29"));
        assertFalse(cr.matchDate("2012-05-14"));

        // ............................................
        cr = new ZCron("0 0 0 1L * ?");
        assertTrue(cr.matchDate("2012-02-29"));
        assertTrue(cr.matchDate("2012-03-31"));
        assertFalse(cr.matchDate("2012-02-28"));
        assertFalse(cr.matchDate("2012-05-14"));

    }

    @Test
    public void test_simple_match_time() {
        ZCron cr;

        // ............................................
        cr = new ZCron("* 9/25 * * * ?");
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
        cr = new ZCron("* 1-10 * * * ?");
        assertTrue(cr.matchTime("12:01:23"));
        assertFalse(cr.matchTime("00:00:00"));
        assertTrue(cr.matchTime("09:03:45"));
        assertTrue(cr.matchTime("15:10:28"));
        assertFalse(cr.matchTime("23:59:59"));

        // ............................................
        cr = new ZCron("0 1-10 0 * * ?");
        assertTrue(cr.matchTime("00:01:00"));
        assertFalse(cr.matchTime("00:00:00"));
        assertTrue(cr.matchTime("00:03:00"));
        assertTrue(cr.matchTime("00:10:00"));
        assertFalse(cr.matchTime("23:59:59"));

        // ............................................
        cr = new ZCron("0 0 0 * * ?");
        assertTrue(cr.matchTime("00:00:00"));
        assertFalse(cr.matchTime("00:00:01"));
        assertFalse(cr.matchTime("00:00:02"));
        assertFalse(cr.matchTime("23:59:59"));

        // ............................................
        cr = new ZCron("* 1,6,10 * * * ?");
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
        new ZCron("0 0 0 7-13 JUL ?");
    }

    /*----------------------------------------------------------读取i18n-------*/
    private static ZCroni18n i18n;

    static {
        i18n = Json.fromJsonFile(ZCroni18n.class,
                                 Files.findFile("org/nutz/plugins/zcron/i18n/zh_cn.js"));
    }

    /*----------------------------------------------------------帮助函数们-------*/
    private static void ae_set_part(String cron, String expect, String... parts) {
        ZCron cr = new ZCron().parse(cron);
        for (String part : parts) {
            int index = Integer.parseInt(part.substring(0, 1));
            String str = part.substring(3).trim();
            cr.__set_part(index, "null".equals(str) ? null : str);
        }
        aeS(cr.toString(), expect);
    }

    private static void aeT(String cron, String expect) {
        ZCron cr = new ZCron(cron);
        String txt = cr.toText(i18n);
        assertEquals(expect, txt);
    }

    private static void aeS(String cron, String expect) {
        ZCron cr = new ZCron(cron);
        String str = cr.toString();
        assertEquals(expect, str);
    }

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
        ZCron cr = new ZCron(rus);

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
            ZCron qz = new ZCron(rus);
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
