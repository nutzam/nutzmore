package org.nutz.plugins.view;

/**
 * 定义视图的对应的属性，实现ViewResolver视图接口。
 * @author denghuafeng(it@denghuafeng.com)
 *
 */
public abstract class AbstractUrlBasedView implements ViewResolver{
	private String name;
	
	private String prefix = "";

	private String suffix = "";

	private String contentType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return "AbstractUrlBasedView [name=" + name + ", prefix=" + prefix
				+ ", suffix=" + suffix + ", contentType=" + contentType + "]";
	}
}
