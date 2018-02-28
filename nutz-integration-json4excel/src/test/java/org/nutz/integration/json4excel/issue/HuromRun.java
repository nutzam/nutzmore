package org.nutz.integration.json4excel.issue;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.TestUtil;
import org.nutz.integration.json4excel.bean.HuromProduct;

public class HuromRun extends TestUtil {

    @Test
    public void loadExcel() throws Exception {
        InputStream in = this.getClass().getResourceAsStream("hdata.xlsx");
        List<HuromProduct> ps = J4E.fromExcel(in, HuromProduct.class, null);
        assertEquals(2, ps.size());
    }
}
