package org.nutz.plugins.view;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;

public class JspView extends AbstractTemplateViewResolver {

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp,
			String evalPath, Map<String, Object> sharedVars) throws Throwable {
		Iterator<Entry<String, Object>> iter = sharedVars.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object> entry = iter.next();
			String key = entry.getKey().toString();
			Object val = entry.getValue();
			req.setAttribute(key, val);
		}
		RequestDispatcher rd = req.getRequestDispatcher(evalPath);
        if (rd == null)
            throw Lang.makeThrow("Fail to find Forward '%s'", evalPath);
        // Do rendering
        rd.forward(req, resp);
	}

	@Override
	protected void init(String appRoot, ServletContext sc) {
		
	}

}
