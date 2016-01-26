package org.nutz.plugins.view.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 
 * @author ixion
 *
 * @email kerbores@kerbores.com
 *
 * @description 验证码绘制
 * 
 * @copyright free use
 *
 *
 * @time 2016年1月26日 上午11:18:42
 */
public class ImageVerification {

	public void setIMAGE_VERIFICATION_LENGTH(int iMAGE_VERIFICATION_LENGTH) {
		IMAGE_VERIFICATION_LENGTH = iMAGE_VERIFICATION_LENGTH;
	}

	private int IMAGE_VERIFICATION_LENGTH = 4;

	private String verifyCode = "";

	private CaptchaGener captchaGener = new DefaultCaptchaGener();

	public BufferedImage creatImage() {

		// 在内存中创建图象
		int width = 20 * IMAGE_VERIFICATION_LENGTH + 20, height = 35;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// 获取图形上下文
		Graphics g = image.getGraphics();

		// 生成随机类
		Random random = new Random();

		// 设定背景色
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);

		// 设定字体
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 25));

		// 随机产生255条干扰线，使图象中的认证码不易被其它程序探测到
		for (int i = 0; i < 255; i++) {
			g.setColor(getRandColor(140, 255));
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(100);
			int yl = random.nextInt(100);
			g.drawLine(x, y, x + xl, y + yl);
		}

		verifyCode = captchaGener.gen(IMAGE_VERIFICATION_LENGTH);
		for (int i = 0; i < IMAGE_VERIFICATION_LENGTH; i++) {
			String rand = verifyCode.charAt(i) + "";
			// 将认证码显示到图象中
			g.setColor(getRandColor(20, 130));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(rand, 20 * i + 10, 25 + getRandInt(-5, 5));
		}
		// 图象生效
		g.dispose();
		return image;
	}

	public Color getRandColor(int b, int e) {// 给定范围获得随机颜色
		if (b > 255)
			b = 255;
		if (e > 255)
			e = 255;
		int rc = getRandInt(b, e);
		int gc = getRandInt(b, e);
		int bc = getRandInt(b, e);
		return new Color(rc, gc, bc);
	}

	public int getRandInt(int b, int e) {
		if (b > e) {
			int temp = e;
			e = b;
			b = temp;
		}
		Random random = new Random();
		return b + random.nextInt(e - b);
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setCaptchaGener(CaptchaGener captchaGener) {
		this.captchaGener = captchaGener;
	}
}
