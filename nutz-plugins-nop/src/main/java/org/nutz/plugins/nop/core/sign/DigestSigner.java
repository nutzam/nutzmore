package org.nutz.plugins.nop.core.sign;

import java.util.Arrays;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPRequest;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOPSigner.java
 *
 * @description 默认的签名实现
 *
 * @time 2016年8月31日 下午2:36:05
 *
 */
public class DigestSigner extends AbstractSinger {

	private String name;

	public DigestSigner(String name, AppsecretFetcher fetcher) {
		super(fetcher);
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String sign(NOPRequest request) {
		String appKey = request.getHeader().get(NOPConfig.appkeyKey);
		String method = request.getHeader().get(NOPConfig.methodKey);
		String timestamp = request.getHeader().get(NOPConfig.tsKey);
		String once = request.getHeader().get("once");
		String appSecret = Strings.isBlank(request.getAppSecret()) ?   getFetcher().fetch(appKey) : request.getAppSecret();//能取到就取出来
		String[] temp = Lang.array(method, timestamp, once, appSecret);
		Arrays.sort(temp);//字典序 method timestamp once appSecret
		return Lang.digest(name(), Strings.join("", temp));//用摘要名取摘要
	}

}
