package org.nutz.integration.json4excel.bean;

import org.nutz.integration.json4excel.J4EEmptyRow;
import org.nutz.lang.Strings;

public class HuromEmptyRow implements J4EEmptyRow<HuromProduct> {

    @Override
    public boolean isEmpty(HuromProduct rowData) {
        return Strings.isBlank(rowData.getName());
    }

}
