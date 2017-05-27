package org.nutz.plugins.undertow.query;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.Param;

/**
 * 封装从 Web Client 来的查询字符串，它接受如下格式的名值对或者 JSON 字符串
 * 
 * <pre>
 *  kwd   : "xxxxxx",
 *  pn    : 1,         // 当前页，<=0 被认为非法，强制等于 1
 *  pgsz  : 50,        // 一页多少数据，如果 <1 则强制等于 50
 *  order : "ASC:$name,..."    // 根据字段来排序
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebQuery {

    @Param("kwd")
    protected String keyword;

    @Param("pn")
    protected int pageNumber;

    @Param("pgsz")
    protected int pageSize;

    @Param("order")
    protected String order;

    protected WebOrderField[] orderFields;

    public boolean hasKeyword() {
        return !Strings.isBlank(keyword);
    }

    public char[] getKeywordChars() {
        if (null != keyword)
            return keyword.toCharArray();
        return new char[0];
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String kwd) {
        this.keyword = kwd;
    }

    public WebQuery keywordf(String fmt, Object... args) {
        this.keyword = String.format(fmt, args);
        return this;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pn) {
        this.pageNumber = pn;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pgsz) {
        this.pageSize = pgsz;
    }

    public int offset() {
        return (pageNumber - 1) * pageSize;
    }

    public String getOrder() {
        return order;
    }

    public WebQuery normalize(int minPn, int maxPgsz, String dftOrder) {
        pageNumber = Math.max(pageNumber, minPn);
        pageSize = pageSize > 0 ? Math.min(maxPgsz, pageSize) : maxPgsz / 2;

        if (Strings.isBlank(order)) {
            this.setOrder(dftOrder);
        }
        return this;
    }

    public void setOrder(String order) {
        this.order = order;
        String[] ss = Strings.splitIgnoreBlank(order, ",");
        if (null != ss) {
            orderFields = new WebOrderField[ss.length];
            for (int i = 0; i < ss.length; i++) {
                orderFields[i] = WebOrderField.valueOf(ss[i]);
            }
        }
    }

    public WebOrderField[] getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(WebOrderField[] orderFields) {
        this.orderFields = orderFields;
    }

    public boolean hasOrder() {
        return null != orderFields && orderFields.length > 0;
    }

    private transient String _source;
    
    public boolean equals(Object obj) {
    	if (obj == null)
    		return false;
    	if (obj instanceof WebQuery) {
    		if (this._source == null)
    			this._source = Json.toJson(this, JsonFormat.compact());
    		WebQuery other = (WebQuery)obj;
    		if (other._source == null)
    			other._source = Json.toJson(other, JsonFormat.compact());
    		return this._source.equals(other._source);
    	} else {
    		return false;
    	}
    }
    
    public int hashCode() {
    	if (this._source == null)
    		this._source = Json.toJson(this, JsonFormat.compact());
    	return this._source.hashCode();
    }
}
