package org.nutz.plugins.view.captcha;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * @author idor(sjbwylbs@gmail.com)
 */
public class CaptchaViewMaker implements ViewMaker {

	@Override
	public View make(Ioc ioc, String type, String value) {
		if ("captche".equalsIgnoreCase(type)) {
			return new CaptchaView("image/jpeg", 4);
		}
		return null;
	}

}
