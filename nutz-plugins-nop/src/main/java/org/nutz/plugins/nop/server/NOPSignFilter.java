package org.nutz.plugins.nop.server;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.nutz.http.Header;
import org.nutz.http.Request.METHOD;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPData;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.sign.AppsecretFetcher;
import org.nutz.plugins.nop.core.sign.DigestSigner;
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
		HttpServletRequest request = ac.getRequest();
		String digestName = request.getAttribute("digestName") == null ? "MD5" : request.getAttribute("digestName") .toString();
		String fetcherName = request.getAttribute("fetcherName") == null ? "default" : request.getAttribute("fetcherName") .toString();
		AppsecretFetcher fetcher = Strings.equals("default", fetcherName) ? AppsecretFetcher.defaultFetcher : ac.getIoc().get(AppsecretFetcher.class, fetcherName);
		Signer signer = new DigestSigner(digestName, fetcher);
		Header header = Header.create();
		Enumeration<String> headers =	request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String key = headers.nextElement();
			header.set(key, request.getHeader(key));
		}
		String method = request.getHeader(NOPConfig.methodKey);
		NOPRequest req = NOPRequest.create(method, METHOD.valueOf(request.getMethod()), "", header);
		if (signer.check(req)) {
			return null;
		}else {
			return new UTF8JsonView().setData(NOPData.exception("checkSign failed"));
		}
	}


}
