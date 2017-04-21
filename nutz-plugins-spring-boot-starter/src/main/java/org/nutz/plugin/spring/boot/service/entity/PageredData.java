package org.nutz.plugin.spring.boot.service.entity;

import java.util.List;

import org.nutz.dao.pager.Pager;

public class PageredData<T> {
	
	/**
	 * 分页信息
	 */
	private Pager pager;
	
	/**
	 * 数据列表
	 */
	private List<T> dataList;

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	
	
	

}
