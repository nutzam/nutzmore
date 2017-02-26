package org.nutz.plugins.nop.core;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * 携带数据的操作结果描述<br>
 * operationState 为操作状态<br>
 * data为携带的数据,用map来保存key-value形式的各种数据便于根据key进行获取<br>
 * 
 * @author Ixion
 *
 *         create at 2014年8月22日
 */
public class NOPData<T> {

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
		return NOPData.exception().setMsg( msg);
	}

	/**
	 * 创建一个带失败信息的NOPData
	 * 
	 * @param reason
	 *            失败原因
	 * @return NOPData实例
	 */
	public static NOPData fail(String reason) {
		return NOPData.me().setOperationState(OperationState.FAIL).setMsg(reason);
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
	
	public NOPData<T> success(T t) {
		return success().setData(t);
	}


	/**
	 * 操作结果数据 假设一个操作要返回很多的数据 一个用户名 一个产品 一个相关产品列表 一个产品的评论信息列表 我们以key
	 * value形式进行保存，页面获取data对象读取其对于的value即可
	 */
	private T data;
	
	private String msg;
	
	

	public T getData() {
		return data;
	}

	public NOPData<T> setData(T data) {
		this.data = data;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public NOPData setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	/**
	 * 带状态的操作 比如登录有成功和失败
	 */
	private OperationState operationState = OperationState.DEFAULT;

	public NOPData() {
		super();
	}




	public OperationState getOperationState() {
		return operationState;
	}


	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return getOperationState() == OperationState.SUCCESS;
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
