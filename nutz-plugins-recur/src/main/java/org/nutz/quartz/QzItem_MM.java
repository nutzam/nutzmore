package org.nutz.quartz;

import java.util.Calendar;

/**
 * 判断月
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QzItem_MM extends QzDateItem {

	@Override
	protected int eval4override(String str) {
		return super.eval(str, MONTH_OF_YEAR, 1);
	}

	@Override
	boolean match(Calendar c) {
		int MM = c.get(Calendar.MONTH) + 1;
		return super._match_(MM, super.prepare(13));
	}

}
