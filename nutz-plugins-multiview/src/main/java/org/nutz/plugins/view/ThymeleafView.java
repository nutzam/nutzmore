package org.nutz.plugins.view;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Mvcs;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import nz.net.ultraq.thymeleaf.LayoutDialect;

public class ThymeleafView extends AbstractTemplateViewResolver {
	private static final String TEMPLATE_MODE_KEY = "templateMode";
	private static final String TEMPLATE_MODE = "HTML5";
	private static final String CACHEABLE_KEY = "cacheable";
	private static final boolean CACHEABLE = true;
	private static final String CACHETTLMS_KEY = "cacheTTLMs";
	private static final Long CACHETTLMS = 3600000L;
	private static final String DIALECTS_KEY = "dialects";
	TemplateEngine templateEngine = new TemplateEngine();
	ServletContextTemplateResolver templateResolver = null;

	public ThymeleafView(String dest) {
		super(dest);
	}

	@Override
	protected void init(String appRoot, ServletContext sc) {
		this.templateResolver = new ServletContextTemplateResolver(Mvcs.getServletContext());
		templateResolver.setTemplateMode(properties.getString(TEMPLATE_MODE_KEY, TEMPLATE_MODE));
		// TODO 需要针对全局prefix 判断子类是否调用prefix来确定evpath路径
		templateResolver.setPrefix(super.getPrefix());
		super.isInitedSetPrefix = true;
		templateResolver.setSuffix(super.getSuffix());
		templateResolver.setCharacterEncoding(super.getEncoding());
		templateResolver.setCacheable(super.getProperties().getBoolean(CACHEABLE_KEY, CACHEABLE));
		templateResolver.setCacheTTLMs(super.getProperties().getLong(CACHETTLMS_KEY, CACHETTLMS));
		IDialect[] dialects = properties.getArray(DIALECTS_KEY, IDialect.class);
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new LayoutDialect());
		if (null != dialects) {
			for (IDialect dialect : dialects) {
				templateEngine.addDialect(dialect);
			}
		}
	}

	@Override
	protected void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
			Map<String, Object> sharedVars) throws Throwable {
		String path = evalPath;
		resp.setContentType(super.getContentType() + "; charset=" + super.getEncoding());
		resp.setCharacterEncoding(super.getEncoding());
		try {
			org.nutz.lang.util.Context ctx = super.createContext(req, null);
			sharedVars.putAll(ctx.getInnerMap());
			WebContext context = new WebContext(req, resp, Mvcs.getServletContext(), Locale.getDefault(), sharedVars);
			templateEngine.process(path, context, resp.getWriter());
		} catch (Exception e) {
			log.error("模板引擎错误", e);
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void setPrefix(String prefix) {
		super.setPrefix(prefix);
		if (templateResolver != null) {
			templateResolver.setPrefix(super.getPrefix());
		}
	}

	@Override
	public void setSuffix(String suffix) {
		super.setSuffix(suffix);
		if (templateResolver != null) {
			templateResolver.setSuffix(super.getSuffix());
		}
	}
}
