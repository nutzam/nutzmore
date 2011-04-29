package org.nutz.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * Smarty4j 视图解析器
 * 
 * @auther 弓勤
 */
public class SmartyViewMaker implements ViewMaker {

	public View make(Ioc ioc, String type, String value) {
		if ("st".equalsIgnoreCase(type)) {
			return new SmartyView(value);
		}
		return null;
	}

}
