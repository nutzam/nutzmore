package org.nutz.plugins.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视图接口
 * @author denghuafeng(it@denghuafeng.com)
 *
 */
public interface ViewResolver {

	void render(HttpServletRequest req, HttpServletResponse resp,
			String evalPath, Map<String, Object> sharedVars) throws Throwable;

}
