package org.nutz.integration.json4excel.issue;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.TestUtil;
import org.nutz.integration.json4excel.bean.HuromProduct;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class HuromRun extends TestUtil {

    @Test
    public void loadExcel() throws Exception {
        InputStream in = this.getClass().getResourceAsStream("原汁机规格参数.xlsx");
        List<HuromProduct> ps = J4E.fromExcel(in, HuromProduct.class, null);
        System.out.println(Json.toJson(ps, JsonFormat.full()));
    }
}
