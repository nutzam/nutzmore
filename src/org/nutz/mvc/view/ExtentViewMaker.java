package org.nutz.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class ExtentViewMaker implements ViewMaker {

	@Override
	public View make(Ioc ioc, String type, String value) {
		if ("ftl".equals(type))
			return new FreemarkerView(value);
		return null;
	}

}
