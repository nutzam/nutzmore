package org.nutz.plugins.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;

import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import jetbrick.template.web.JetWebContext;
import jetbrick.template.web.JetWebEngine;

/**
 * JetTemplate视图。
 * 
 * @author 邓华锋(http://dhf.ink)
 *
 */
public class JetTemplateView extends AbstractTemplateViewResolver {
	private JetEngine engine;

	public JetTemplateView(String dest) {
		super(dest);
	}

	public void init(String appRoot, ServletContext sc) {
		if (super.getConfig() != null) {// 如果配置了配置文件
			engine = JetWebEngine.create(sc, super.getConfig().toProperties(), null);
		} else {// 没有，则自动查找
			engine = JetWebEngine.create(sc);
		}
	}

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
			Map<String, Object> sharedVars) throws Throwable {
		String charsetEncoding = engine.getConfig().getOutputEncoding().name();
		resp.setCharacterEncoding(charsetEncoding);
		if (resp.getContentType() == null) {
			resp.setContentType("text/html; charset=" + charsetEncoding);
		}
		try {
			JetTemplate template = engine.getTemplate(evalPath);
			Iterator<Entry<String, Object>> iter = sharedVars.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Object> entry = iter.next();
				String key = entry.getKey().toString();
				Object val = entry.getValue();
				req.setAttribute(key, val);
			}
			JetWebContext context = new JetWebContext(req, resp);
			template.render(context, resp.getOutputStream());
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
