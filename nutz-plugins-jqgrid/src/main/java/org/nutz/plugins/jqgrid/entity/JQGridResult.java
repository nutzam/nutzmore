package org.nutz.plugins.jqgrid.entity;
import java.io.Serializable;
import java.util.List;

import org.nutz.dao.pager.Pager;
/**
 * jqGrid返回的结果
 * 
 * @author 邓华锋
 * @date 2016年6月27日 下午4:22:19
 *
 */
public class JQGridResult implements Serializable{
    private static final long serialVersionUID = 5939846263450399192L;
    
    /**
     * 页码
     */
    private int page;
    
    /**
     * 总页数
     */
    private int total;
    
    /**
     * 总行数
     */
    private int records;

    /**
     * 当前页的数据
     */
    private List<?> rows;
    
    public JQGridResult(Pager page,List<?> rows) {
        this.page=page.getPageNumber();
        this.total=page.getPageCount();
        this.records=page.getRecordCount();
        this.rows = rows;
    }

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}
}