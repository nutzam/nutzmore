package org.nutz.plugins.nop.core.sign;

import java.util.Arrays;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

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

	/**
	 * 
	 */
	public DigestSigner() {
		this.name = "MD5";
	}

	public DigestSigner(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.plugins.nop.core.sign.Signer#sign(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String sign(String appSecret, String timestamp, String gateway, String nonce, String dataMate) {
		String[] temp = Lang.array(appSecret, timestamp, gateway, nonce, dataMate);
		Arrays.sort(temp);
		log.debugf("sign with %s args %s", name(), temp);
		return Lang.digest(name(), Strings.join("", temp));
	}

}
