package org.nutz.plugins.view.thymeleaf;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.AbstractPathView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import nz.net.ultraq.thymeleaf.LayoutDialect;

public class ThymeleafView extends AbstractPathView {

	private static final Log log = Logs.get();

	private TemplateEngine templateEngine = new TemplateEngine();

	private String contentType;

	private String encoding;

	public ThymeleafView(ThymeleafProperties properties, String path) {
		super(path);
		templateEngine.setTemplateResolver(initializeTemplateResolver(properties));

		templateEngine.addDialect(new LayoutDialect());
		IDialect[] dialects = properties.getDialects();
		if (null != dialects) {
			for (IDialect dialect : dialects) {
				templateEngine.addDialect(dialect);
			}
		}

		encoding = properties.getEncoding();
		contentType = properties.getContentType() + "; charset=" + encoding;
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object value) throws Exception {
		String path = evalPath(request, value);
		response.setContentType(contentType);
		response.setCharacterEncoding(encoding);
		try {
			org.nutz.lang.util.Context ctx = createContext(request, value);
			WebContext context = new WebContext(request,
					response,
					Mvcs.getServletContext(),
					Locale.getDefault(),
					ctx.getInnerMap());
			templateEngine.process(path, context, response.getWriter());
		} catch (Exception e) {
			log.error("模板引擎错误", e);
			throw e;
		}
	}

	private ITemplateResolver initializeTemplateResolver(ThymeleafProperties properties) {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(Mvcs.getServletContext());

		templateResolver.setTemplateMode(properties.getMode());
		templateResolver.setPrefix(properties.getPrefix());
		templateResolver.setSuffix(properties.getSuffix());
		templateResolver.setCharacterEncoding(properties.getEncoding());
		templateResolver.setCacheable(properties.isCache());
		templateResolver.setCacheTTLMs(properties.getCacheTTLMs());

		return templateResolver;
	}
}
