package org.nutz.plugins.nop.core.sign;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.nutz.http.Http;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPRequest;

/**
 * 抽象一下
 * 
 * @author kerbores
 *
 */
public abstract class AbstractSinger implements Signer {

	Log log = Logs.get();

	/**
	 * 根据appSecret获取器签名
	 * 
	 * @param fetcher
	 *            appSecret获取器
	 * @param appKey
	 *            应用key
	 * @param timestamp
	 *            时间戳
	 * @param gateway
	 *            方法/路由
	 * @param nonce
	 *            随机串
	 * @param dataMate
	 *            数据元数据
	 * @return
	 */
	public String sign(AppsecretFetcher fetcher, String appKey, String timestamp, String gateway, String nonce, String dataMate) {
		return sign(fetcher.fetch(appKey), timestamp, gateway, nonce, dataMate);
	}


	/**
	 * 服务器端签名
	 * 
	 * @param request
	 *            请求
	 * @param fetcher
	 *            appSecret获取器
	 * @return
	 * @throws IOException
	 */
	public String sign(HttpServletRequest request, AppsecretFetcher fetcher) {
		return sign(fetcher, request.getHeader(NOPConfig.appkeyKey), request.getHeader(NOPConfig.tsKey), request.getHeader(NOPConfig.methodKey),
				request.getHeader(NOPConfig.nonceKey), getDataMate(request));
	}

	protected String getDataMate(HttpServletRequest request) {
		if (Strings.equalsIgnoreCase(request.getMethod(), "GET")) {// GET请求需要处理一下
			return Lang.md5(request.getQueryString());
		}
		try {
			return Lang.md5(request.getInputStream());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 客户端签名
	 * 
	 * @param appSecret
	 *            应用密钥
	 * @param timestamp
	 *            时间戳
	 * @param gateway
	 *            方法/路由
	 * @param nonce
	 *            随机串
	 * @param request
	 *            请求
	 * @return
	 */
	public String sign(String appSecret, String timestamp, String gateway, String nonce, NOPRequest request) {
		return sign(appSecret, timestamp, gateway, nonce, getDataMate(request));
	}

	protected String getDataMate(NOPRequest request) {
		if (request.isGet()) {
			String query = request.getURLEncodedParams();
			String method = Strings.isBlank(query) ? request.getGateway() : request.getGateway() + "?" + query;
			query = method.indexOf("?") > 0 ? method.substring(method.indexOf("?") + 1) : "";
			return Lang.md5(query);
		}

		return Lang.md5(request.getInputStream());
	}

	protected String getURLEncodedParams(Map<String, String[]> params, final String enc) {
		final StringBuilder sb = new StringBuilder();
		if (params != null) {
			for (Entry<String, String[]> en : params.entrySet()) {
				final String key = en.getKey();
				Object val = en.getValue();
				if (val == null)
					val = "";
				Lang.each(val, new Each<Object>() {
					@Override
					public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						sb.append(Http.encode(key, enc))
								.append('=')
								.append(Http.encode(ele, enc))
								.append('&');
					}
				});
			}
			if (sb.length() > 0)
				sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	public boolean check(HttpServletRequest request, AppsecretFetcher fetcher) {
		if (Strings.isBlank(request.getHeader(NOPConfig.appkeyKey)) ||
				Strings.isBlank(request.getHeader(NOPConfig.tsKey)) ||
				Strings.isBlank(request.getHeader(NOPConfig.methodKey)) ||
				Strings.isBlank(request.getHeader(NOPConfig.nonceKey)) ||
				Strings.isBlank(request.getHeader(NOPConfig.signKey))) {
			return false;
		}
		return Strings.equalsIgnoreCase(sign(request, fetcher), request.getHeader(NOPConfig.signKey));
	}

}
