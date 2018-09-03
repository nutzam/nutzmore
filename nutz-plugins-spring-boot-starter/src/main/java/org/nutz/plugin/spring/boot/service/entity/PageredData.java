package org.nutz.plugin.spring.boot.service.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.pager.Pager;

public class PageredData<T> {

	/**
	 * 分页信息
	 */
	private Pager pager;

	/**
	 * 分页参数(带有一堆参数的分页)
	 */
	private Map<String, Object> paras;

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

	/**
	 * @return the paras
	 */
	public Map<String, Object> getParas() {
		return paras;
	}

	/**
	 * @param paras
	 *            the paras to set 设置分页查询参数一些查询的筛选条件 按照mvc参数key-value的形式
	 */
	public void setParas(Map<String, Object> paras) {
		this.paras = paras;
	}

	public PageredData<T> addParam(String key, Object value) {
		if (this.paras == null) {
			this.paras = new HashMap<>();
		}
		this.paras.put(key, value);
		return this;
	}

}
