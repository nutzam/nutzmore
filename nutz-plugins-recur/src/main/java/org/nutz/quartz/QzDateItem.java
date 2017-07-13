package org.nutz.quartz;

import java.util.Calendar;

import org.nutz.lang.Lang;

abstract class QzDateItem extends QzItem {

	/**
	 * 是否匹配一个日期
	 * 
	 * @param c
	 *            日期
	 * @return 是否匹配
	 */
	abstract boolean match(Calendar c);

	@Override
	public boolean match(int v, int min, int max) {
		throw Lang.makeThrow("Please use : match(Calendar c)");
	}

}
