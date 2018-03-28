package org.nutz.plugins.dict;
import org.nutz.plugins.dict.Select;
import org.nutz.plugins.dict.Select.Fields;

/**
 * jqgrid排序
 * @author 邓华锋 http://dhf.ink
 */
@Select(value=Fields.NAME)
public enum JQGridOrder {
	asc("升序",0),desc("降序",1) ;
	private String text;

	private int value;

	private JQGridOrder(String text, int value) {
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