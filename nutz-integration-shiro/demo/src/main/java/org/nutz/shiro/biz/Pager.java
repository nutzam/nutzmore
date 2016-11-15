package org.nutz.shiro.biz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

/**
 * @author 王贵源
 *
 *         create at 2014年9月30日 上午11:21:12
 */
public class Pager<T> {

	/**
	 * 默认分页大小
	 */
	public int pageSize = 15;
	/**
	 * 默认第一页
	 */
	private int page = 1;
	/**
	 * 默认数据记录数
	 */
	private long count = 0;
	/**
	 * 分页内容列表
	 */
	private List<T> entities;
	/**
	 * 分页url
	 */
	private String url;
	/**
	 * 分页参数(带有一堆参数的分页)
	 */
	private Map<String, Object> paras;

	private boolean simplePager = false;

	/**
	 * 分页条最大长度
	 */
	private long maxLength = 10;

	/**
	 * 分页条尺寸
	 */
	private String size;

	/**
	 * 默认构造
	 */
	public Pager() {
	}

	/**
	 * 
	 * @param pageSize
	 *            分页大小
	 * @param page
	 *            当前页
	 */
	public Pager(int pageSize, int page) {
		super();
		this.pageSize = pageSize;
		this.page = page;
	}

	/**
	 * @param pageSize
	 *            页面大小
	 * @param page
	 *            页码
	 * @param count
	 *            总记录数
	 * @param entities
	 *            实体列表
	 */
	public Pager(int pageSize, int page, int count, List<T> entities) {
		super();
		this.pageSize = pageSize;
		this.page = page;
		this.count = count;
		this.entities = entities;
	}

	public void addParas(String key, Object value) {
		if (paras == null) {
			paras = new NutMap();
		}
		paras.put(key, value);
	}

	/**
	 * 
	 * 生成分页节点
	 * 
	 * @param url
	 *            分页URL
	 * @param start
	 *            开始节点index
	 * @param end
	 *            结束节点index
	 * @param page
	 *            当前页
	 * @return
	 */
	private String genPagerBarNode(String url, long start, long end, int page) {
		String target = "";
		for (long i = start; i <= end; i++) {
			target += "<li " + (page == i ? "class='active'" : "") + "><a href='" + url + i + "'>" + i + (page == i ? "<span class='sr-only'>(current)</span>" : "") + "</a></li>";
		}
		return target;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @return the entities
	 */
	public List<T> getEntities() {
		return entities;
	}

	/**
	 * @return the maxLength
	 */
	public long getMaxLength() {
		return maxLength;
	}

	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * 获取分页条
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getPagerBar() throws UnsupportedEncodingException {
		// url = Mvcs.getReq().getAttribute("base") + url;
		if (getPages() == 0) {
			return "";
		}
		if (!isSimplePager()) {
			// 非简单分页,组装分页参数
			url = url.indexOf('?') > 0 ? url : url + "?";
			if (paras != null) {
				Iterator it = paras.keySet().iterator();// 此时不用判断参数为空,只要属于非简单分页参数肯定不会空
				while (it.hasNext()) {
					String key = it.next().toString();
					String val = paras.get(key).toString();
					url += key + "=" + URLEncoder.encode(val, "UTF-8") + "&";
				}
			}
			url += "page=";
		} else {
			url = url.charAt(url.length() - 1) == '/' ? url : url + "/";// 判断传入的url是否加了'/'
		}
		// 处理url结束
		String bar = "<ul class='pagination " + (size == null ? "" : "pagination-" + size) + "'>";
		bar += "<li " + (page <= 1 ? "class='disabled'" : "") + "><a href='" + url + (page - 1) + "'>&laquo;</a></li>";

		if (getPages() < maxLength) {
			maxLength = getPages();
		}

		// 页码小于分页条的一半的时候，从第一页开始显示到barLength页//page 1 barLength 2
		if (page <= maxLength / 2) {
			bar += genPagerBarNode(url, 1, maxLength, page);
		}
		// 页码大于页数减去分页长度的一半的时候 显示 pages - maxLength到pages页
		else if (page >= getPages() - maxLength / 2) {
			bar += genPagerBarNode(url, getPages() - maxLength == 0 ? 1 : getPages() - maxLength, getPages(), page);
		}
		// 中间情况 显示 page - maxLength/2到page+maxLength/2页
		else {
			bar += genPagerBarNode(url, page - maxLength / 2, page + maxLength / 2, page);
		}
		// 处理结尾
		bar += "<li " + (page == getPages() ? "class='disabled'" : "") + "><a href='" + url + (page + 1) + "'>&raquo;</a></li>";
		bar += "</ul>";
		return bar;
	}

	/**
	 * @return the pages
	 */
	public long getPages() {
		return count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the paras
	 */
	public Map<String, Object> getParas() {
		return paras;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 简单分页
	 * 
	 * @return
	 */
	public boolean isSimplePager() {
		return simplePager && (paras == null || paras.size() == 0);
	}

	/**
	 * @param count
	 *            the count to set 记录条数,将根据记录条数生成数据页数,用count()从数据库查询
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * @param entities
	 *            the entities to set 数据列表,根据条件从数据库分页查出
	 */
	public void setEntities(List<T> entities) {
		this.entities = entities;
	}

	/**
	 * @param maxLength
	 *            the maxLength to set 分页条的最大长度
	 *            默认10也就是说页数大于10的时候最多显示10个分页节点(显示当前页码的前后X页,尽量让当前页在中间部位)
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @param page
	 *            the page to set 当前页码
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set 页面大小,默认15
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @param paras
	 *            the paras to set 设置分页查询参数一些查询的筛选条件 按照mvc参数key-value的形式
	 */
	public void setParas(Map<String, Object> paras) {
		this.paras = paras;
	}

	public void setSimplePager(boolean simplePager) {
		this.simplePager = simplePager;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @param url
	 *            the url to set 设置分页路径,建议绝对路径
	 *            server:port/contextPath/nameSpace/method
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return
	 */
	public int start() {
		return (page - 1) * pageSize;
	}

	@Override
	public String toString() {
		return Json.toJson(this);
	}

}
