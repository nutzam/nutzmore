package org.nutz.plugins.ioc.loader.chain;

import java.util.Map;

import org.nutz.ioc.Ioc;

/**
 * Ioc启动接口
 * 
 * @author 邓华锋 http://dhf.ink
 *
 */
public interface IocSetup {
	/**
	 * 启动时，额外逻辑
	 * 
	 * @param nc
	 *            配置对象,包含Ioc等你需要的一切资源
	 */
	void init(Map<String, Ioc> iocs);

	/**
	 * 关闭时，额外逻辑
	 * 
	 * @param nc
	 *            配置对象,包含Ioc等你需要的一切资源
	 */
	void destroy(Map<String, Ioc> iocs);
}
