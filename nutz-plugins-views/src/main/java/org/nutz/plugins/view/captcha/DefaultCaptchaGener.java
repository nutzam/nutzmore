package org.nutz.plugins.view.captcha;

import org.nutz.lang.random.R;

public class DefaultCaptchaGener implements CaptchaGener {

	private String pool = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public DefaultCaptchaGener() {
		super();
	}

	public DefaultCaptchaGener(String pool) {
		super();
		this.pool = pool;
	}

	@Override
	public String gen(int length) {
		if (length <= 0) {
			return "";
		}
		char[] pools = pool.toCharArray();
		String target = "";
		while (target.length() < length) {
			target += pools[R.random(0, pools.length - 1)];
		}
		return target;
	}

}
