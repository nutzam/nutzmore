package org.nutz.plugin.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nutz.json")
public class NutzJsonProperties {

	/**
	 * 模式
	 * 
	 * @author kerbores kerbores@gmail.com
	 *
	 */
	public static enum Mode {
		COMPACT, FULL, NICE, FORLOOK, TIDY;
	}

	/**
	 * json模式
	 */
	private Mode mode = Mode.COMPACT;

	/**
	 * 时间格式
	 */
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 输出的key筛选正则表达式
	 */
	private String actived;

	/**
	 * 自动Unicode转码
	 */
	private boolean autoUnicode = false;

	/**
	 * 缩进
	 */
	private int indent = 4;

	/**
	 * 是否给字段添加双引号
	 */
	private boolean quoteName;
	/**
	 * 是否忽略null值
	 */
	private boolean ignoreNull;

	/**
	 * 不输出的字段的正则表达式
	 */
	private String locked;

	/**
	 * unicode编码用大写还是小写
	 */
	private boolean unicodeLower;

	/**
	 * 遇到空值的时候写入字符串
	 */
	private boolean nullAsEmtry;

	/**
	 * 缩进字符串
	 */
	private String indentBy = "\t";

	/**
	 * @return the quoteName
	 */
	public boolean isQuoteName() {
		return quoteName;
	}

	/**
	 * @param quoteName
	 *            the quoteName to set
	 */
	public void setQuoteName(boolean quoteName) {
		this.quoteName = quoteName;
	}

	/**
	 * @return the ignoreNull
	 */
	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	/**
	 * @param ignoreNull
	 *            the ignoreNull to set
	 */
	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	/**
	 * @return the locked
	 */
	public String getLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            the locked to set
	 */
	public void setLocked(String locked) {
		this.locked = locked;
	}


	/**
	 * @return the unicodeLower
	 */
	public boolean isUnicodeLower() {
		return unicodeLower;
	}

	/**
	 * @param unicodeLower
	 *            the unicodeLower to set
	 */
	public void setUnicodeLower(boolean unicodeLower) {
		this.unicodeLower = unicodeLower;
	}


	/**
	 * @return the nullAsEmtry
	 */
	public boolean isNullAsEmtry() {
		return nullAsEmtry;
	}

	/**
	 * @param nullAsEmtry
	 *            the nullAsEmtry to set
	 */
	public void setNullAsEmtry(boolean nullAsEmtry) {
		this.nullAsEmtry = nullAsEmtry;
	}

	/**
	 * @return the indent
	 */
	public int getIndent() {
		return indent;
	}

	/**
	 * @param indent
	 *            the indent to set
	 */
	public void setIndent(int indent) {
		this.indent = indent;
	}

	/**
	 * @return the indentBy
	 */
	public String getIndentBy() {
		return indentBy;
	}

	/**
	 * @param indentBy
	 *            the indentBy to set
	 */
	public void setIndentBy(String indentBy) {
		this.indentBy = indentBy;
	}

	/**
	 * @return the autoUnicode
	 */
	public boolean isAutoUnicode() {
		return autoUnicode;
	}

	/**
	 * @param autoUnicode
	 *            the autoUnicode to set
	 */
	public void setAutoUnicode(boolean autoUnicode) {
		this.autoUnicode = autoUnicode;
	}

	/**
	 * @return the actived
	 */
	public String getActived() {
		return actived;
	}

	/**
	 * @param actived
	 *            the actived to set
	 */
	public void setActived(String actived) {
		this.actived = actived;
	}

	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat
	 *            the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

}
