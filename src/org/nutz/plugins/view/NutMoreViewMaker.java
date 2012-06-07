package org.nutz.plugins.view;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.plugins.view.smarty.SmartyView;

public class NutMoreViewMaker implements ViewMaker {

	@Override
	public View make(Ioc ioc, String type, String value) {
		if ("st".equals(type)) {
			try {
				return new SmartyView(value);
			} catch (Throwable e) {
				throw Lang.wrapThrow(e);
			}
		}
		
		return null;
	}

}
