package org.nutz.mvc.view;

public class FreemarkerView extends ForwardView {

	public FreemarkerView(String value) {
		super(value);
	}
	
	@Override
	protected String getExt() {
		return "ftl";
	}
}
