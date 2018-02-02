package org.nutz.integration.json4excel.bean;

import org.nutz.castor.Castors;
import org.nutz.integration.json4excel.J4ECellToExcel;

public class CellFunStatus implements J4ECellToExcel {

    @Override
    public Object toExecl(Object fieldVal) {
        Integer status = Castors.me().castTo(fieldVal, Integer.class);
        if (status == 1) {
            return "审核通过";
        }
        if (status == 2) {
            return "未通过";
        }
        return "未知状态";
    }

}
