package org.nutz.plugins.zcron;

import java.util.Calendar;

/**
 * 判断月
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class CrnItem_MM extends CrnDateItem {

    public CrnItem_MM(CrnStdItem... prevs) {
        super(prevs);
    }

    @Override
    protected int eval4override(String str) {
        return super.__eval(str, MONTH_OF_YEAR, 1);
    }

    @Override
    boolean match(Calendar c) {
        int MM = c.get(Calendar.MONTH) + 1;
        return super._match_(MM, super.prepare(13));
    }

}
