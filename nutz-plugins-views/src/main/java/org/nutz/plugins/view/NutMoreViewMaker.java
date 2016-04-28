package org.nutz.plugins.view;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.ViewMaker2;

public class NutMoreViewMaker implements ViewMaker, ViewMaker2 {

	@Override
	public View make(Ioc ioc, final String type, String value) {
	    return null;
	}

	@Override
	public View make(NutConfig conf, ActionInfo ai, String type, String value) {
		if ("map".equals(type))
			return new MapView(conf, ai, type, value);
		return null;
	}

}
