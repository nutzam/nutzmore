package org.nutz.integration.json4excel.issue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.J4EColumn;
import org.nutz.integration.json4excel.J4EConf;
import org.nutz.integration.json4excel.TestUtil;
import org.nutz.integration.json4excel.bean.DashayuChild;
import org.nutz.integration.json4excel.bean.I68Bean;
import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;

public class IssueRun extends TestUtil {

    @Test
    public void testIssue16() throws Exception {
        List<VideoRate> vrList = J4E.fromExcel(this.getClass().getResourceAsStream("Book1.xlsx"),
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
}
