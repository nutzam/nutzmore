package org.nutz.integration.json4excel.issue;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.J4EColumn;
import org.nutz.integration.json4excel.J4EConf;
import org.nutz.integration.json4excel.TestUtil;
import org.nutz.integration.json4excel.bean.DashayuChild;
import org.nutz.integration.json4excel.bean.I100Bean;
import org.nutz.integration.json4excel.bean.I68Bean;
import org.nutz.integration.json4excel.bean.I91Bean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;

public class IssueRun extends TestUtil {

    // https://github.com/nutzam/nutzmore/issues/16
    @Test
    public void testIssue16() throws Exception {
        File e = new File(Disks.absolute("./issue/Book1.xlsx"));
        List<VideoRate> vrList = J4E.fromExcel(Streams.fileIn(e),
                                               VideoRate.class,
                                               null);
        assertEquals(31, vrList.size());
        VideoRate vr1 = vrList.get(0);
        assertEquals("20170715", vr1.getTime());
        assertEquals(828, vr1.getAiVideoClick());
        assertEquals(4498, vr1.getRefresh());
        assertEquals(25823, vr1.getTotalClick());
        assertEquals(354, vr1.getSearchClick());
        assertEquals(1015, vr1.getSearchCount());
    }

    // https://github.com/nutzam/nutzmore/issues/68
    @Test
    public void testIssue68() throws Exception {
        I68Bean d1 = new I68Bean();
        d1.setKeyWorld("嗯嗯").setNumber(100);
        I68Bean d2 = new I68Bean();
        d2.setKeyWorld("呵呵").setNumber(200);
        List<I68Bean> dataList = new ArrayList<I68Bean>();
        dataList.add(d1);
        dataList.add(d2);

        J4E.toExcel(Files.createFileIfNoExists2(Disks.normalize("~/tmp/issue68.xls")),
                    dataList,
                    null);
    }

    // https://github.com/nutzam/nutzmore/issues/86
    @Test
    public void testIssue86() throws Exception {
        // 准备数据
        DashayuChild dChild = new DashayuChild();
        dChild.id = 1000;
        dChild.nm = "pw";
        dChild.createAt = System.currentTimeMillis();
        dChild.status = 1;

        DashayuChild dChild2 = new DashayuChild();
        dChild2.id = 1001;
        dChild2.nm = "dsy";
        dChild2.createAt = System.currentTimeMillis();
        dChild2.status = 2;

        List<DashayuChild> dataList = new ArrayList<DashayuChild>();
        dataList.add(dChild);
        dataList.add(dChild2);

        // 手动设置配置
        J4EConf j4eConf = J4EConf.from(DashayuChild.class);
        List<J4EColumn> jcols = j4eConf.getColumns();
        for (J4EColumn j4eColumn : jcols) {
            // id不需要
            if ("id".equals(j4eColumn.getFieldName())) {
                j4eColumn.setIgnore(true);
            }
            // nm需要中文名称
            if ("nm".equals(j4eColumn.getFieldName())) {
                j4eColumn.setColumnName("名称");
            }
        }
        // 导出
        J4E.toExcel(Files.createFileIfNoExists2(Disks.normalize("~/tmp/issue86.xls")),
                    dataList,
                    j4eConf);
    }

    // https://github.com/nutzam/nutzmore/issues/91
    @Test
    public void testIssue91() throws Exception {
        // 默认配置
        J4EConf econf = J4EConf.from(I91Bean.class);
        // 制作点假数据，三行就够了
        I91Bean c1 = new I91Bean();
        c1.name = "电耗（度）";
        c1.ref = 20030.23d;
        c1.d1 = 40277.39d;
        c1.d2 = 35.00d;
        c1.d3 = 20050.00d;

        I91Bean c2 = new I91Bean();
        c2.name = "新水耗（kg）";
        c2.ref = 20039.00d;
        c2.d1 = 40531.00d;
        c2.d2 = 0.00d;
        c2.d3 = 19833.00d;

        I91Bean c3 = new I91Bean();
        c3.name = "中水耗（kg）";
        c3.ref = 18372.34d;
        c3.d1 = 49883.03d;
        c3.d2 = 2.00d;
        c3.d3 = 20001.00d;

        List<I91Bean> clist = new ArrayList<I91Bean>();
        clist.add(c1);
        clist.add(c2);
        clist.add(c3);

        // 加载模板
        File e = new File(Disks.absolute("./issue/i91.xls"));
        Workbook wb = J4E.loadExcel(Streams.fileIn(e));
        // 第一排左边
        econf.setPassColumn(0).setPassRow(1);
        J4E.toExcel(wb, null, clist, econf);
        // 第一排右边
        econf.setPassColumn(5).setPassRow(1);
        J4E.toExcel(wb, null, clist, econf);

        // 第二排左边
        econf.setPassColumn(0).setPassRow(9);
        J4E.toExcel(wb, null, clist, econf);
        // 第二排右边
        econf.setPassColumn(5).setPassRow(9);
        J4E.toExcel(wb, null, clist, econf);

        File outFile = Files.createFileIfNoExists2(Disks.normalize("~/tmp/issue91.xls"));
        J4E.saveExcel(new FileOutputStream(outFile), wb);
    }

    @Test
    public void testIssue91Read() throws Exception {
        // 默认配置
        J4EConf econf = J4EConf.from(I91Bean.class);
        // 加载模板
        File e = new File(Disks.absolute("./issue/i91Read.xls"));
        Workbook wb = J4E.loadExcel(Streams.fileIn(e));
        // 第一个sheet
        Sheet sheet1 = wb.getSheet("第一个");
        // 第一排左边
        econf.setPassColumn(0).setPassRow(1).setMaxRead(3); // 只读3行数据
        List<I91Bean> dlist01 = J4E.fromSheet(sheet1, I91Bean.class, econf, false);
        assertEquals(3, dlist01.size());
        assertEquals("我是上面1", dlist01.get(0).name);

        // 第一排右边
        econf.setPassColumn(5).setPassRow(1).setMaxRead(2); // 只读2行数据
        List<I91Bean> dlist02 = J4E.fromSheet(sheet1, I91Bean.class, econf, false);
        assertEquals(2, dlist02.size());
        assertEquals("我是右边的2", dlist02.get(1).name);

        // 第二排左边
        econf.setPassColumn(0).setPassRow(9).setMaxRead(3); // 只读3行数据
        List<I91Bean> dlist03 = J4E.fromSheet(sheet1, I91Bean.class, econf, false);
        assertEquals(3, dlist03.size());
        assertEquals("我是下面3", dlist03.get(2).name);

        // 第二排右边
        econf.setPassColumn(5).setPassRow(9).setMaxRead(2); // 只读2行数据
        List<I91Bean> dlist04 = J4E.fromSheet(sheet1, I91Bean.class, econf, false);
        assertEquals(2, dlist04.size());
        assertEquals("我是下面右边2", dlist04.get(1).name);

        // 第二个sheet
        Sheet sheet2 = wb.getSheet("第二个");
        // 第一排左边
        econf.setPassColumn(0).setPassRow(1).setMaxRead(1); // 只读1行数据
        List<I91Bean> dlist05 = J4E.fromSheet(sheet2, I91Bean.class, econf, false);
        assertEquals(1, dlist05.size());

    }

    // https://github.com/nutzam/nutzmore/issues/100
    @Test
    public void testIssue100() throws Exception {
        File e = new File(Disks.absolute("./issue/i100.xls"));
        List<I100Bean> list = J4E.fromExcel(Streams.fileIn(e),
                                            I100Bean.class,
                                            null);
        for (I100Bean flashSale : list) {
            String fromStr = flashSale.getFrom();
            String toStr = flashSale.getTo();
            System.out.println(fromStr + " ," + toStr);
        }
    }
}
