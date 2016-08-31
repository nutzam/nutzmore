package org.nutz.plugins.nop.core;

import java.util.Iterator;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

/**
 * 携带数据的操作结果描述<br>
 * operationState 为操作状态<br>
 * data为携带的数据,用map来保存key-value形式的各种数据便于根据key进行获取<br>
 * 
 * @author Ixion
 *
 *         create at 2014年8月22日
 */
public class NOPData {

	public static enum OperationState {
		/**
		 * 成功
		 */
		SUCCESS,
		/**
		 * 失败
		 */
		FAIL,
		/**
		 * 默认
		 */
		DEFAULT,
		/**
		 * 异常
		 */
		EXCEPTION,
		/**
		 * 未登录
		 */
		UNLOGINED
	}

	/**
	 * 创建一个异常结果
	 * 
	 * @return 一个异常结果实例,不携带异常信息
	 */
	public static NOPData exception() {
		return NOPData.me().setOperationState(OperationState.EXCEPTION);
	}

	/**
	 * 未登录
	 * 
	 * @return
	 */
	public static NOPData unlogin() {
		return NOPData.me().setOperationState(OperationState.UNLOGINED);
	}

	/**
	 * 创建一个异常结果
	 * 
	 * @param e
	 *            异常
	 * @return 一个异常结果实例,包含参数异常的信息
	 */
	public static NOPData exception(Exception e) {
		return NOPData.exception(e.getMessage());
	}

	/**
	 * 创建一个异常结果
	 * 
	 * @param msg
	 *            异常信息
	 * @return 一个异常结果实例,不携带异常信息
	 */
	public static NOPData exception(String msg) {
		return NOPData.exception().addData("reason", msg);
	}

	/**
	 * 创建一个带失败信息的NOPData
	 * 
	 * @param reason
	 *            失败原因
	 * @return NOPData实例
	 */
	public static NOPData fail(String reason) {
		Map data = new NutMap();
		data.put("reason", reason);
		return NOPData.me().setOperationState(OperationState.FAIL).setData(data);
	}

	/**
	 * 获取一个NOPData实例
	 * 
	 * @return 一个不携带任何信息的NOPData实例
	 */
	public static NOPData me() {
		return new NOPData();
	}

	/**
	 * 创建一个成功结果
	 * 
	 * @return NOPData实例状态为成功无数据携带
	 */
	public static NOPData success() {
		return NOPData.me().setOperationState(OperationState.SUCCESS);
	}

	/**
	 * 创建一个成功结果
	 * 
	 * @param data
	 *            需要携带的数据
	 * @return NOPData实例状态为成功数据位传入参数
	 */
	public static NOPData success(Map data) {
		return NOPData.success().setData(data);
	}

	/**
	 * 操作结果数据 假设一个操作要返回很多的数据 一个用户名 一个产品 一个相关产品列表 一个产品的评论信息列表 我们以key
	 * value形式进行保存，页面获取data对象读取其对于的value即可
	 */
	private NutMap data = new NutMap();

	/**
	 * 带状态的操作 比如登录有成功和失败
	 */
	private OperationState operationState = OperationState.DEFAULT;

	public NOPData() {
		super();
	}

	public NOPData(OperationState operationState, Map data, String title) {
		super();
		this.operationState = operationState;
		this.data = NutMap.WRAP(data);
	}

	/**
	 * 添加更多的数据
	 * 
	 * @param data
	 *            待添加的数据
	 * @return 结果实例
	 */
	public NOPData addData(Map data) {
		Iterator iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			this.data.put(key, data.get(key));
		}
		return this;
	}

	/**
	 * 添加数据
	 * 
	 * @param key
	 * @param object
	 * @return
	 */
	public NOPData addData(String key, Object object) {
		if (this.data == null) {
			data = new NutMap();
		}
		data.put(key, object);
		return this;
	}

	/**
	 * 清空结果
	 */
	public NOPData clear() {
		this.operationState = OperationState.DEFAULT;
		if (data != null) {
			this.data.clear();
		}
		return this;
	}

	public NutMap getData() {
		return data;
	}

	/**
	 * 以nutmap包装数据
	 * 
	 * @return
	 */
	public NutMap getNutMapData() {
		return NutMap.WRAP(data);
	}

	public OperationState getOperationState() {
		return operationState;
	}

	/**
	 * 获取错误获取异常原因
	 * 
	 * @return
	 */
	public String getReason() {
		return getData().getString("reason");
	}

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return getOperationState() == OperationState.SUCCESS;
	}

	public NOPData setData(Map data) {
		this.data = NutMap.WRAP(data);
		return this;
	}

	public NOPData setOperationState(OperationState operationState) {
		this.operationState = operationState;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Json.toJson(this, JsonFormat.forLook());
	}
}
