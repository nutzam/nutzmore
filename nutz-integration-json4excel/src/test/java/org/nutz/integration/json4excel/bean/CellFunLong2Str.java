package org.nutz.integration.json4excel.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.nutz.castor.Castors;
import org.nutz.integration.json4excel.J4ECellToExcel;

public class CellFunLong2Str implements J4ECellToExcel {

    @Override
    public Object toExecl(Object fieldVal) {
        Long createAt = Castors.me().castTo(fieldVal, Long.class);
        Date dateAt = new Date(createAt);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateAt);
    }

}
