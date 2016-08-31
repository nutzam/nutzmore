package org.nutz.plugins.nop.core.sign;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.nop.core.NOPRequest;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file Signer.java
 *
 * @description 签名产生器
 *
 * @time 2016年8月31日 下午2:17:14
 *
 */
public interface Signer {

	public Log log = Logs.get();

	public String name();

	public String sign(String appKey, String appSecret, NOPRequest request);

}
