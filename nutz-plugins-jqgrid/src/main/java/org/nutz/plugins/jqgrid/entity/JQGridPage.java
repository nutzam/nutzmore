package org.nutz.plugins.jqgrid.entity;
import java.io.Serializable;


/**
 * JQGrid分页
 * 
 * @author 邓华锋
 * @date 2016年6月27日 下午3:43:11
 *
 */
public class JQGridPage implements Serializable{
	private static final long serialVersionUID = -6779387545446523955L;
	
	private boolean _search;
	
    private JQGridFilter filters;
    
    private String nd;
    
    /**
     * 当前页码
     */
    private int page=1;
    
    /**
     * 分页行数
     */
    private int rows=10;
    
    private String searchField;
    
    private String searchOper;
    
    private String searchString; 
    
    /**
     * 排序字段
     */
    private String sidx;
    
    /**
     * 排序方式：1.asc 2.desc
     */
    private String sord;

	public boolean is_search() {
		return _search;
	}

	public void set_search(boolean _search) {
		this._search = _search;
	}

	public JQGridFilter getFilters() {
		return filters;
	}

	public void setFilters(JQGridFilter filters) {
		this.filters = filters;
	}

	public String getNd() {
		return nd;
	}

	public void setNd(String nd) {
		this.nd = nd;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public String getSearchOper() {
		return searchOper;
	}

	public void setSearchOper(String searchOper) {
		this.searchOper = searchOper;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}
}