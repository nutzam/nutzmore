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
    public enum Mode {
        COMPACT, FULL, NICE, FORLOOK, TIDY;
    }

    /**
     * json模式,配置此模式意味着其他属性失效
     */
    private Mode mode;

    /**
     * 缩进
     */
    private int indent;
    /**
     * 缩进时用的字符串
     */
    private String indentBy;
    /**
     * 是否使用紧凑模式输出
     */
    private boolean compact;
    /**
     * 是否给字段添加双引号
     */
    private boolean quoteName;

    /**
     * 是否启用nutz.json
     */
    private boolean enabled;

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 是否忽略null值
     */
    private boolean ignoreNull;
    /**
     * 仅输出的字段的正则表达式
     */
    private String actived;
    /**
     * 不输出的字段的正则表达式
     */
    private String locked;

    /**
     * 分隔符
     */
    private char separator = '\"';
    /**
     * 是否自动将值应用Unicode编码
     */
    private boolean autoUnicode;
    /**
     * unicode编码用大写还是小写
     */
    private boolean unicodeLower;
    /**
     * 日期格式
     */
    private String dateFormat;
    /**
     * 数字格式
     */
    private String numberFormat;

    /**
     * 遇到空值的时候写入字符串
     */
    private boolean nullAsEmtry;

    /**
     * 列表空值的时候写入字符串
     */
    private boolean nullListAsEmpty;

    /**
     * 字符串空值的时候写入字符串
     */
    private boolean nullStringAsEmpty;

    /**
     * Boolean空值作为false
     */
    private boolean nullBooleanAsFalse;

    /**
     * Number空值作0
     */
    private boolean nullNumberAsZero;

    /**
     * 不使用nutzjson解析的类的全限定名正则表达式,比如 .*springfox.*
     */
    private String ignoreType;

    public String getIgnoreType() {
        return ignoreType;
    }

    public void setIgnoreType(String ignoreType) {
        this.ignoreType = ignoreType;
    }

    private String timeZone;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public String getIndentBy() {
        return indentBy;
    }

    public void setIndentBy(String indentBy) {
        this.indentBy = indentBy;
    }

    public boolean isCompact() {
        return compact;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public boolean isQuoteName() {
        return quoteName;
    }

    public void setQuoteName(boolean quoteName) {
        this.quoteName = quoteName;
    }

    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    public void setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
    }

    public String getActived() {
        return actived;
    }

    public void setActived(String actived) {
        this.actived = actived;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public boolean isAutoUnicode() {
        return autoUnicode;
    }

    public void setAutoUnicode(boolean autoUnicode) {
        this.autoUnicode = autoUnicode;
    }

    public boolean isUnicodeLower() {
        return unicodeLower;
    }

    public void setUnicodeLower(boolean unicodeLower) {
        this.unicodeLower = unicodeLower;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public boolean isNullAsEmtry() {
        return nullAsEmtry;
    }

    public void setNullAsEmtry(boolean nullAsEmtry) {
        this.nullAsEmtry = nullAsEmtry;
    }

    public boolean isNullListAsEmpty() {
        return nullListAsEmpty;
    }

    public void setNullListAsEmpty(boolean nullListAsEmpty) {
        this.nullListAsEmpty = nullListAsEmpty;
    }

    public boolean isNullStringAsEmpty() {
        return nullStringAsEmpty;
    }

    public void setNullStringAsEmpty(boolean nullStringAsEmpty) {
        this.nullStringAsEmpty = nullStringAsEmpty;
    }

    public boolean isNullBooleanAsFalse() {
        return nullBooleanAsFalse;
    }

    public void setNullBooleanAsFalse(boolean nullBooleanAsFalse) {
        this.nullBooleanAsFalse = nullBooleanAsFalse;
    }

    public boolean isNullNumberAsZero() {
        return nullNumberAsZero;
    }

    public void setNullNumberAsZero(boolean nullNumberAsZero) {
        this.nullNumberAsZero = nullNumberAsZero;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
