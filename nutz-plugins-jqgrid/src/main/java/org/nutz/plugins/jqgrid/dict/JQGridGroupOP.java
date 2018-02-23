package org.nutz.plugins.jqgrid.dict;

/**
 * jqgrid分组操作
 * @author 邓华锋 http://dhf.ink
 *
 */
//@Select(value=Fields.NAME)
public enum JQGridGroupOP {
	or("或者",0),and("并且",1);
	private String text;

	private int value;

	private JQGridGroupOP(String text, int value) {
		this.text = text;
		this.value = value;
	}

	public String text() {
		return text;
	}

	public int value() {
		return value;
	}
}