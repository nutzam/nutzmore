package org.nutz.plugins.validation;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * 验证错误信息汇总类
 * 
 * @author QinerG(QinerG@gmail.com)
 */
public class Errors {
	/*
	 * 存放错误信息的 map
	 */
	private Map<String, String> errorMap = new TreeMap<String, String>();

	/**
	 * @return 是否存在验证错误
	 */
	public boolean hasError() {
		return errorMap.size() > 0;
	}
	
	/**
	 * @return　返回存在错误的总数
	 */
	public int errorCount() {
		return errorMap.size();
	}

	/**
	 * 增加一个错误信息
	 * 
	 * @param fieldName
	 *            存在错误的字段名
	 * @param errorMessage
	 *            错误的详细信息
	 */
	public void add(String fieldName, String errorMessage) {
		errorMap.put(fieldName, errorMessage);
	}

	/**
	 * 返回错误信息列表
	 * @return
	 */
	public Collection<String> getErrorsList() {
		return errorMap.values();
	}

	/**
	 * 返回详细的错误信息列表，含验证错误的字段名和提示语
	 * @return
	 */
	public Map<String, String> getErrorsMap() {
		return errorMap;
	}

}
