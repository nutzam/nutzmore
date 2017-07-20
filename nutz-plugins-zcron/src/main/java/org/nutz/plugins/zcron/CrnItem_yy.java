package org.nutz.plugins.zcron;

import java.util.Calendar;

public class CrnItem_yy extends CrnDateItem {

    public CrnItem_yy(CrnStdItem... prevs) {
        super(prevs);
    }

    @Override
    protected int eval4override(String str) {
        return super.__eval(str, null, 1);
    }

    @Override
    boolean match(Calendar c) {
        int yy = c.get(Calendar.YEAR);
        return super._match_(yy, super.prepare(0));
    }

}
