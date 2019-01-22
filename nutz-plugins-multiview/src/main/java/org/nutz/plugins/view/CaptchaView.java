package org.nutz.plugins.view;

import java.io.OutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.plugins.view.captcha.CaptchaGener;
import org.nutz.plugins.view.captcha.ImageVerification;

public class CaptchaView extends AbstractTemplateViewResolver {
	/**
	 * Session 中用于存放 captcha 字符串的 key
	 */
	public static final String CAPTCHA = "KERBORES_NUTZ_CAPTCHA";

	private int length;
	private CaptchaGener captchaGener;

	public CaptchaView(String dest) {
		super(dest);
	}

	@Override
	protected void init(String appRoot, ServletContext sc) {
		if (super.getContentType() == "text/html") {// 如果为默认值，则设为
			super.setContentType("image/jpeg");
		}
		length = properties.getInt("length", 4);
		captchaGener = properties.getAs("captchaGener", CaptchaGener.class);
	}

	@Override
	protected void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
			Map<String, Object> sharedVars) throws Throwable {
		resp.setContentType(super.getContentType());
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
