package org.nutz.integration.dwr;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.WebContextFactory.WebContextBuilder;
import org.directwebremoting.impl.ContainerUtil;
import org.directwebremoting.impl.StartupUtil;
import org.directwebremoting.servlet.UrlProcessor;
import org.nutz.lang.Lang;

@SuppressWarnings("serial")
public class NutDwrServlet extends HttpServlet {

	NutIocContainer container;

	WebContextBuilder webContextBuilder;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		ServletContext servletContext = servletConfig.getServletContext();
		try {
			ContainerUtil.setupDefaults(container, servletConfig);
			ContainerUtil.setupFromServletConfig(container, servletConfig);

			webContextBuilder = StartupUtil.initWebContext(servletConfig,
					servletContext, container);
			StartupUtil.initServerContext(servletConfig, servletContext,
					container);

			ContainerUtil.prepareForWebContextFilter(servletContext,
					servletConfig, container, webContextBuilder, this);
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			webContextBuilder.set(req, resp, getServletConfig(), getServletContext(), container);
			UrlProcessor processor = (UrlProcessor) container.getBean(UrlProcessor.class.getName());
			processor.handle(req, resp);
		} finally {
			webContextBuilder.unset();
		}
	}
}
