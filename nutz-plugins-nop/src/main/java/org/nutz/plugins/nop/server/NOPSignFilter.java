package org.nutz.plugins.nop.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.nutz.http.Header;
import org.nutz.http.Request.METHOD;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPData;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.sign.NOPSigner;
import org.nutz.plugins.nop.core.sign.Signer;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOPSignFilter.java
 *
 * @description NOP 签名检查拦截器
 *
 * @time 2016年8月31日 下午4:01:18
 *
 */
public class NOPSignFilter implements ActionFilter {

	Log log = Logs.get();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.mvc.ActionFilter#match(org.nutz.mvc.ActionContext)
	 */
	@Override
	public View match(ActionContext ac) {
		Map<String, String> header = new HashMap<String, String>();

		Enumeration<String> headerKeys = ac.getRequest().getHeaderNames();
		while (headerKeys.hasMoreElements()) {
			String key = headerKeys.nextElement();
			header.put(key, ac.getRequest().getHeader(key));
		}
		String sign = header.remove(NOPConfig.signkey());
		String signerName = header.remove(NOPConfig.signerKey());
		String appKey = header.remove(NOPConfig.appkeyKey());
		String appSecret = header.remove(NOPConfig.appSecretKey());

		if (!checkAppKey(appKey, appSecret)) {// appKey 验证失败
			return new UTF8JsonView().setData(NOPData.exception("checkAppKey failed"));
		} else if (!checkSign(sign, signerName, appKey, appSecret, ac)) {// 签名验证失败
			return new UTF8JsonView().setData(NOPData.exception("checkSign failed"));
		}
		return null;
	}

	/**
	 * @param sign
	 * @param signerName
	 * @param appKey
	 * @param appSecret
	 * @param header
	 * @param ac
	 * @return
	 */
	private boolean checkSign(String sign, String signerName, String appKey, String appSecret, ActionContext ac) {
		Signer signer = getSignerByName(signerName);
		NOPRequest request = NOPRequest.create("", Strings.equalsIgnoreCase(ac.getRequest().getMethod(), "get") ? METHOD.GET : METHOD.POST,
				(NutMap) ac.getRequest().getAttribute(NOPConfig.parasKey()), Header.create());
		return Strings.equals(signer.sign(appKey, appSecret, request), sign);
	}

	public boolean checkAppKey(String appKey, String appSecret) {
		return Strings.isNotBlank(appKey) && Strings.isNotBlank(appSecret);// 非空即可,自行覆盖实现业务
	}

	/**
	 * 根据名称找实例
	 * 
	 * @param name
	 * @return
	 */
	public Signer getSignerByName(String name) {

		return new NOPSigner();
	}

}
