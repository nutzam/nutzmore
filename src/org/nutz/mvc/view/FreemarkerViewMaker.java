package org.nutz.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class FreemarkerViewMaker implements ViewMaker {

	public View make(Ioc ioc, String type, String value) {
		if("fm".equalsIgnoreCase(type)){
			return new FreemarkerView(value);
		}
		return null;
	}

}
