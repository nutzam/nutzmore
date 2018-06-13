package org.nutz.integration.json4excel.issue;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.TestUtil;
import org.nutz.integration.json4excel.bean.HuromProduct;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;

public class HuromRun extends TestUtil {

    @Test
    public void loadExcel() throws Exception {
        File file = new File(Disks.absolute("./issue/hdata.xlsx"));
        List<HuromProduct> ps = J4E.fromExcel(Streams.fileIn(file), HuromProduct.class, null);
        assertEquals(2, ps.size());
    }
}
