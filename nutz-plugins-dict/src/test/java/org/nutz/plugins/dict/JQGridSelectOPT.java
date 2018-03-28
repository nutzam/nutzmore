package org.nutz.plugins.dict;

import org.nutz.plugins.dict.Select;

/**
 * JQGrid查询操作符
 * 
 * @author 邓华锋 http://dhf.ink
 *
 */
@Select
public enum JQGridSelectOPT {
	eq("等于","="),ne("不等于","<>"),bw("开始于","LIKE ${value}%"),bn("不开始于","NOT LIKE ${value}%"),ew("结束于","LIKE %${value}"),en("不结束于","NOT LIKE %${value}"),
	cn("包含","LIKE"),nc("不包含","NOT LIKE"),nu("空值于","IS NULL"),nn("非空值","IS NOT NULL"),in("属于","IN"),ni("不属于","NOT IN"),
	lt("小于","<"),le("小于等于","<="),gt("大于",">"),ge("大于等于",">=");

	private String text;

	private String value;

	private JQGridSelectOPT(String text, String value) {
		this.text = text;
		this.value = value;
	}

	public String text() {
		return text;
	}

	public String value() {
		return value;
	}

}