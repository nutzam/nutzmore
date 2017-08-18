package org.nutz.integration.json4excel.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.TestUtil;

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
}
