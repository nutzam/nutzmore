package org.nutz.plugins.view.captcha;

/**
 * 验证码生成接口
 * 
 * @author Kerbores
 *
 */
public interface CaptchaGener {
	/**
	 * 生成指定长度验证码
	 * 
	 * @param length
	 *            长度
	 * @return 验证码
	 */
	String gen(int length);
}
