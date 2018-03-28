package org.nutz.plugins.jqgrid.dict;
/**
 * jqgrid按钮
 * @author 邓华锋 http://dhf.ink
 */
public enum JQGridOper {
    edit("编辑",0),del("删除",1),add("增加",2);
	
	private String text;

	private int value;

	private JQGridOper(String text, int value) {
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
