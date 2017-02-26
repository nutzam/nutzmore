package org.nutz.plugins.nop.core.sign;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.nop.core.NOPRequest;

/**
 * 签名器
 * 
 * @author kerbores
 *
 */
public interface Signer {
	
	public Log log = Logs.get();

	/**
	 * 名称
	 * 
	 * @return
	 */
	public String name();

	/**
	 * 签名生成
	 * 
	 * @param request
	 *            请求
	 * @return
	 */
	public String sign(NOPRequest request);

	/**
	 * 签名检查
	 * 
	 * @param request
	 *            请求
	 * @return
	 */
	public boolean check(NOPRequest request);

}
