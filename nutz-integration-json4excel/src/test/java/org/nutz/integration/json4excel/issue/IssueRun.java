package org.nutz.integration.json4excel.issue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.TestUtil;
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

        J4E.toExcel(Files.createFileIfNoExists2(Disks.normalize("~/issue68.xls")), dataList, null);
    }
}
