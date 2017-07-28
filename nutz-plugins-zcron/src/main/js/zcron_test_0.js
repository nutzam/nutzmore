log("###############################################");
log("# ZCron Testing S0");
log("# @author zozoh@gmail.com");
log("# @version alpha");
log("# @2016");
var ms_begin = Date.now();
log("========================= Test of setPart\n");
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
            "T{2:00} * * 1,5",
            "0:: T{2:00}",
            "3:: null");
ae_set_part("T{2:00} 0 0 0 * * 1,5", "0 5,9 12 * * 1,5", "0:: null", "1:: 0 5,9 12");
log("========================= Test of toText\n");
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
log("---");
aeT("0 0 ? * * ? *", "每天的每小时的0分0秒");
aeT("0 0 ? * 1,5,6 1-3 *", "每年的一月,五月,六月的周日至周二的每天每小时的0分0秒");
aeT("0 0 ? * * ? 2017-2020", "2017年至2020年的每天的每小时的0分0秒");
aeT("0 0 ? * * ? 2017/2", "从2017年开始每隔2年的每天的每小时的0分0秒");
aeT("0 0 ? * * ? 2017,2019,2034", "2017年,2019年,2034年的每天的每小时的0分0秒");
log("---");
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
aeT("D(20170725,20170921] T{1:20,2:3,18:05}", "2017年7月25日（不包含）至9月21日，每天的01:20, 02:03, 18:05");

log("========================= Test of toString\n");
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
//.......................................................
log("\n========================= test_ext_multi_time_region:");
Crn("* * ? T[6:00,8:30]{0/30m} T[22:00,22:30]{0/10m}");

assert_match_time(true, "6:00");
assert_match_time(true, "6:30");
assert_match_time(true, "7:00");
assert_match_time(true, "7:30");
assert_match_time(true, "8:00");
assert_match_time(true, "8:30");
assert_match_time(true, "22:00");
assert_match_time(true, "22:10");
assert_match_time(true, "22:20");
assert_match_time(true, "22:30");

assert_match_time(false, "5:59");
assert_match_time(false, "6:01");
assert_match_time(false, "6:59");
assert_match_time(false, "7:01");
assert_match_time(false, "7:59");
assert_match_time(false, "8:01");
assert_match_time(false, "8:59");
assert_match_time(false, "21:59");
assert_match_time(false, "22:01");
assert_match_time(false, "22:11");
assert_match_time(false, "22:13");
assert_match_time(false, "22:31");

//.......................................................
log("\n========================= test_ext_normal:");
Crn("D[20170725,20170921] T[12:23,18:32]{0/30m}");
assert_match_date(true, "2017-07-25");
assert_match_date(true, "2017-09-21");
assert_match_date(false, "2017-07-24");
assert_match_date(false, "2015-09-22");

assert_match_time(false, "11:53");
assert_match_time(true, "12:23");
assert_match_time(true, "12:53");
assert_match_time(true, "13:23");
assert_match_time(true, "13:53");
assert_match_time(true, "14:23");
assert_match_time(true, "14:53");
assert_match_time(true, "16:23");
assert_match_time(true, "16:53");
assert_match_time(true, "17:23");
assert_match_time(true, "17:53");
assert_match_time(true, "18:23");
assert_match_time(false, "18:53");
//.......................................................
log("\n========================= Test of matchDate");
Crn("0 0 0 1-5 * ?");
assert_match_date(true, "2015-07-01");
assert_match_date(true, "2015-07-05");
assert_match_date(false, "2015-07-06");
assert_match_date(false, "2015-06-30");
// ............................................
Crn("0 0 0 1-5 JUL ?");
assert_match_date(true, "2015-07-01");
assert_match_date(true, "2015-07-05");
assert_match_date(false, "2015-07-06");
assert_match_date(false, "2015-06-30");
// ............................................
Crn("0 0 0 1-5 7 ?");
assert_match_date(true, "2015-07-01");
assert_match_date(true, "2015-07-05");
assert_match_date(false, "2015-07-06");
assert_match_date(false, "2015-06-30");
// ............................................
Crn("0 0 0 1-3,6-8 * ?");
assert_match_date(true, "2012-04-02");
assert_match_date(true, "2014-06-07");
assert_match_date(false, "2011-05-04");
// ............................................
Crn("0 0 0 * 4,6 ?");
assert_match_date(true, "2012-04-05");
assert_match_date(true, "2014-06-06");
assert_match_date(false, "2011-05-07");
// ............................................
Crn("0 0 0 * * SUN#2");
assert_match_date(true, "2012-02-12");
assert_match_date(false, "2012-02-05");
assert_match_date(false, "2012-02-19");
// ............................................
Crn("0 0 0 * * ?");
assert_match_date(true, "2012-03-15");
// ............................................
Crn("0 0 0 W * ?");
assert_match_date(true, "2012-02-07");
assert_match_date(false, "2012-02-05");
assert_match_date(false, "2012-02-11");
// ............................................
Crn("0 0 0 * * SUN-MON");
assert_match_date(true, "2012-02-05");
assert_match_date(true, "2012-02-06");
assert_match_date(false, "2012-02-07");
// ............................................
Crn("0 0 0 * * 1-2");
assert_match_date(true, "2012-02-05");
assert_match_date(true, "2012-02-06");
assert_match_date(false, "2012-02-07");
// ............................................
Crn("0 0 0 * 4-6 ?");
assert_match_date(true, "2012-04-05");
assert_match_date(true, "2018-05-06");
assert_match_date(true, "2016-06-06");
assert_match_date(false, "2012-03-07");
assert_match_date(false, "2012-07-07");
// ............................................
Crn("0 0 0 1 * ?");
assert_match_date(true, "2012-02-01");
assert_match_date(true, "2012-03-01");
assert_match_date(false, "2012-02-29");
assert_match_date(false, "2012-05-14");
// ............................................
Crn("0 0 0 1-7 * ?");
assert_match_date(true, "2012-02-01");
assert_match_date(true, "2012-03-07");
assert_match_date(false, "2012-02-29");
assert_match_date(false, "2012-05-14");
// ............................................
Crn("0 0 0 1L * ?");
assert_match_date(true, "2012-02-29");
assert_match_date(true, "2012-03-31");
assert_match_date(false, "2012-02-28");
assert_match_date(false, "2012-05-14");
//.......................................................
log("\n========================= Test of matchTime");
Crn("* 9/25 * * * ?");
assert_match_time(false, "00:00:00");

assert_match_time(false, "12:08:23");
assert_match_time(true, "09:09:45");
assert_match_time(false, "12:10:23");

assert_match_time(false, "12:33:23");
assert_match_time(true, "09:34:45");
assert_match_time(false, "12:35:23");

assert_match_time(false, "12:58:23");
assert_match_time(true, "09:59:45");
assert_match_time(false, "12:00:23");

assert_match_time(true, "23:59:59");

// ............................................
Crn("* 1-10 * * * ?");
assert_match_time(true, "12:01:23");
assert_match_time(false, "00:00:00");
assert_match_time(true, "09:03:45");
assert_match_time(true, "15:10:28");
assert_match_time(false, "23:59:59");

// ............................................
Crn("0 1-10 0 * * ?");
assert_match_time(true, "00:01:00");
assert_match_time(false, "00:00:00");
assert_match_time(true, "00:03:00");
assert_match_time(true, "00:10:00");
assert_match_time(false, "23:59:59");

// ............................................
Crn("0 0 0 * * ?");
assert_match_time(true, "00:00:00");
assert_match_time(false, "00:00:01");
assert_match_time(false, "00:00:02");
assert_match_time(false, "23:59:59");

// ............................................
Crn("* 1,6,10 * * * ?");
assert_match_time(false, "00:00:00");

assert_match_time(false, "12:00:23");
assert_match_time(true, "12:01:23");
assert_match_time(false, "12:02:23");

assert_match_time(false, "12:05:23");
assert_match_time(true, "09:06:45");
assert_match_time(false, "12:07:23");

assert_match_time(false, "12:09:23");
assert_match_time(true, "15:10:28");
assert_match_time(false, "12:11:23");

assert_match_time(false, "23:59:59");
//.......................................................
log("\n========================= Test of fill_by_hour\n");
Fhh("2012-02-08", "0 0 5-8 * * ?", [ "5:E", "6:E", "7:E", "8:E" ]);
Fhh("2012-02-08", "0,3,4,5 0/1 4-8 * * ?", [ "4:E", "5:E", "6:E", "7:E", "8:E" ]);

Fhh("2012-02-08", "0 0 5,8 * * ?", [ "5:E", "8:E" ]);
Fhh("2012-02-08", "0,3,4,5 0/1 5,8 * * ?", [ "5:E", "8:E" ]);
//.......................................................
log("\n========================= Test of test_fill_by_min\n");
Fmm("2012-02-08", "0 1,2 0,1 * * ?", [ "1:E", "2:E", "61:E", "62:E" ]);
//.......................................................
log("\n========================= Test of test_fill_by_sec\n");
Fss("2012-02-08", "0,1 0 0,1 * * ?", [ "0:E", "1:E", "3600:E", "3601:E" ]);
//.......................................................
log("\n========================= Test of test_fill_partly\n");
Fhh("2012-02-08", "0 0 8-10 * * ?", [ "9:E", "10:E" ], 9, 3);
//.......................................................
log("\n========================= Test of test_overlap_by_hour\n");
OVERL(24, "2012-02-08", [ "0 0 5-7 * * ?", "0 0 5-6 * 2 ?", "0 0 5-8 * 3 ?" ], [ "5:0.E,1.E",
	"6:0.E,1.E", "7:0.E" ]);

OVERL(24, "2012-03-08", [ "0 0 5-7 * * ?", "0 0 5-6 * 2 ?", "0 0 5-8 * 3 ?" ], [ "5:0.E,2.E",
	"6:0.E,2.E", "7:0.E,2.E", "8:2.E" ]);
//.......................................................
var ms_end = Date.now();
log("\n################################################");
log("# ALL TEST(S0) DONE IN " + (ms_end - ms_begin) + "ms");
log("# ZCron Beta;");
log("#     by zozoh @ 2016");
log("#################################################\n");