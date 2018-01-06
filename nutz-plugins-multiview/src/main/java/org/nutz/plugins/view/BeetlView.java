package org.nutz.plugins.view;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;
import org.beetl.ext.web.WebRender;
import org.nutz.lang.Strings;

/**
 * Beetl视图。
 * 
 * @author 邓华锋(http://dhf.ink)
 *
 */
public class BeetlView extends AbstractTemplateViewResolver {

	public BeetlView(String dest) {
		super(dest);
	}

	public GroupTemplate groupTemplate;

	@Override
	protected void init(String appRoot, ServletContext sc) {
		Configuration cfg = null;
		try {
			cfg = Configuration.defaultConfiguration();
			// 针对beetl放在公共的lib目录获取不到beetl.properties的补救方案
			if (!Strings.isBlank(appRoot) && !Strings.isBlank(getConfigPath())) {
				cfg.add(new File(appRoot + "/" + getConfigPath() + "/beetl.properties"));
			}
		} catch (IOException e) {
			throw new RuntimeException("加载GroupTemplate失败", e);
		}
		WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
		if (!Strings.isBlank(appRoot)) {
			resourceLoader.setRoot(appRoot);
		}
		groupTemplate = new GroupTemplate(resourceLoader, cfg);
		// 3.0以上用sc.getClassLoader()
		// 2.5以下用Thread.currentThread().getContextClassLoader()
		groupTemplate.setClassLoader(Thread.currentThread().getContextClassLoader());
	}

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
			Map<String, Object> sharedVars) throws Throwable {
		groupTemplate.setSharedVars(sharedVars);
		WebRender render = new WebRender(groupTemplate);
		render.render(evalPath, req, resp);
	}

}
