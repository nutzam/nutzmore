package org.nutz.plugins.view;

/**
 * 定义视图的对应的属性，实现ViewResolver视图接口。
 * @author denghuafeng(it@denghuafeng.com)
 *
 */
public abstract class AbstractUrlBasedView implements ViewResolver{
	
	private String prefix = "";

	private String suffix = "";

	private String contentType;
	
	private String configPath;

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
	
	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	@Override
	public String toString() {
		return "AbstractUrlBasedView [prefix=" + prefix + ", suffix=" + suffix
				+ ", contentType=" + contentType + ", configPath=" + configPath
				+ "]";
	}
}
