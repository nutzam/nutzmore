log("###############################################");
log("# ZCron Testing S0");
log("# @author zozoh@gmail.com");
log("# @version alpha");
log("# @2016");
var ms_begin = Date.now();
log("========================= Test of parsing\n");
explain("0 0 0 * * ?");
explain("0 0 ? * * ?");
explain("0 ? ? * * ?");
explain("0 0 8-11,13-18 * * ?");
explain("0 0 0 7-13 JUL ?");
explain("0 0 0 1,3,5 * ?");
explain("0 0 8/3 * * ?");
explain("0 0 0 4L * ?");
explain("0 0 0 W * ?");
explain("0 0 0 1LW * ?");
explain("0 0 0 6LW * ?");
explain("0 0 0 * * 1-3");
explain("0 0 0 * * 1,2-4,7");
explain("0 0 0 * * FRI#2");
explain("0 0 0 * * 2#4");
explain("0 0/5 8,10-14,23 * * ?");
explain("0 0 0,1 * * ?");
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