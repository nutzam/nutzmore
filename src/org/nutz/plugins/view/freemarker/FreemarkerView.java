package org.nutz.plugins.view.freemarker;

import org.nutz.mvc.view.ForwardView;

public class FreemarkerView extends ForwardView {

	public FreemarkerView(String value) {
		super(value);
	}
	
	protected String getExt() {
		return "ftl";
	}
}
