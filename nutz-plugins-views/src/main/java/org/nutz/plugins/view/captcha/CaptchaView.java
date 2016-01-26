package org.nutz.plugins.view.captcha;

import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;

/**
 * 
 * @author ixion
 *
 * @email kerbores@kerbores.com
 *
 * @description 验证码视图
 * 
 * @copyright free use
 *
 *
 * @time 2016年1月26日 上午11:18:06
 */
public class CaptchaView implements View {
	/**
	 * Session 中用于存放 captcha 字符串的 key
	 */
	public static final String CAPTCHA = "KERBORES_NUTZ_CAPTCHA";
	private static final Log log = Logs.getLog(CaptchaView.class);

	private String contentType;
	private int length;
	private CaptchaGener captchaGener;

	public CaptchaView(String contentType) {
		if (contentType == null) {
			this.contentType = "image/jpeg";
		} else {
			this.contentType = contentType;
		}
	}

	public CaptchaView(String contentType, int length) {
		if (contentType == null) {
			this.contentType = "image/jpeg";
		} else {
			this.contentType = contentType;
		}
		this.length = length;
	}

	public CaptchaView(String contentType, CaptchaGener captchaGener) {
		if (contentType == null) {
			this.contentType = "image/jpeg";
		} else {
			this.contentType = contentType;
		}
		this.captchaGener = captchaGener;
	}

	public CaptchaView(String contentType, CaptchaGener captchaGener, int length) {
		if (contentType == null) {
			this.contentType = "image/jpeg";
		} else {
			this.contentType = contentType;
		}
		this.captchaGener = captchaGener;
		this.length = length;
	}

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		resp.setContentType(this.contentType);
		resp.setHeader("Pragma", "No-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		HttpSession session = req.getSession();

		OutputStream out = resp.getOutputStream();
		// 输出图象到页面
		ImageVerification iv = new ImageVerification();

		if (length != 0) {
			iv.setIMAGE_VERIFICATION_LENGTH(length);
		}
		if (captchaGener != null) {
			iv.setCaptchaGener(captchaGener);
		}

		if (ImageIO.write(iv.creatImage(), "JPEG", out)) {
			log.debugf("写入输出流成功:%s.", iv.getVerifyCode());
		} else {
			log.debugf("写入输出流失败:%s.", iv.getVerifyCode());
		}

		session.setAttribute(CAPTCHA, iv.getVerifyCode());

		// 以下关闭输入流！
		out.flush();
		out.close();

	}
}
