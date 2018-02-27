package org.nutz.integration.json4excel.bean;

import org.nutz.integration.json4excel.J4ECellFromExcel;

public class HuromWeight implements J4ECellFromExcel {

    @Override
    public Object fromExcel(Object cellVal) {
        return cellVal.toString() + "kg";
    }

}
