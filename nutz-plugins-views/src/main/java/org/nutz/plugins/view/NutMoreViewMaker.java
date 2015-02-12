package org.nutz.plugins.view;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.ViewMaker2;
import org.nutz.mvc.view.ForwardView;
import org.nutz.plugins.view.smarty.SmartyView;

public class NutMoreViewMaker implements ViewMaker, ViewMaker2 {

	public View make(Ioc ioc, final String type, String value) {
		if ("st".equals(type)) {
			try {
				return new SmartyView(value);
			} catch (Throwable e) {
				throw Lang.wrapThrow(e);
			}
		}
		
		if ("ftl".equals(type))
			return new ForwardView(value) {
			protected String getExt() {
				return "ftl";
			}
		};
		
		if (type.startsWith("->") && type.length() > 2) {
			final String myType = type.substring(1);
			return new ForwardView(value) {
				protected String getExt() {
					return myType;
				}
			};
		}
		return null;
	}

	public View make(NutConfig conf, ActionInfo ai, String type, String value) {
		if ("map".equals(type))
			return new MapView(conf, ai, type, value);
		return null;
	}

}
