package org.nutz.plugins.view;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mvc.Mvcs;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;

public class FreemarkerView extends AbstractTemplateViewResolver {
	private static final String CONFIG_SERVLET_CONTEXT_KEY = "freemarker.Configuration";
	private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
	private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
	private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
	private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
	private static final String KEY_APPLICATION = "Application";
	private static final String KEY_REQUEST_MODEL = "Request";
	private static final String KEY_SESSION_MODEL = "Session";
	private static final String KEY_REQUEST_PARAMETER_MODEL = "Parameters";
	private static final String KEY_EXCEPTION = "exception";
	private static final String KEY_JSP_TAGLIBS = "JspTaglibs";
	private Configuration cfg;

	public FreemarkerView(String dest) {
		super(dest);
	}

	@Override
	protected void init(String appRoot, ServletContext sc) {
		cfg = (Configuration) sc.getAttribute(CONFIG_SERVLET_CONTEXT_KEY);
		if (cfg == null) {
			cfg = new Configuration(Configuration.VERSION_2_3_28);
			cfg.setServletContextForTemplateLoading(sc, "/");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
			// 读取freemarker配置文件
			loadSettings(cfg);
			sc.setAttribute(CONFIG_SERVLET_CONTEXT_KEY, cfg);
		}
		cfg.setWhitespaceStripping(true);
	}

	// @SuppressWarnings("unchecked")
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, String evalPath,
			Map<String, Object> vars) throws Throwable {
		// 添加数据模型
		// root.put(BASE, request.getContextPath());
		Enumeration<String> reqs = request.getAttributeNames();
		while (reqs.hasMoreElements()) {
			String strKey = (String) reqs.nextElement();
			vars.put(strKey, request.getAttribute(strKey));
		}
		// 让freemarker支持jsp 标签
		jspTaglibs(Mvcs.getServletContext(), request, response, vars, cfg.getObjectWrapper());
		// 模版路径
		Template t = cfg.getTemplate(evalPath);
		response.setContentType("text/html; charset=" + t.getEncoding());
		t.process(vars, response.getWriter());
	}

	protected void loadSettings(Configuration cfg) {
		try {
			if (super.getConfig() == null) {
				super.setConfig(new PropertiesProxy("freemarker.properties"));
			}
			cfg.setSettings(super.getConfig().toProperties());
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	protected void jspTaglibs(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model, ObjectWrapper wrapper) {
		synchronized (servletContext) {
			ServletContextHashModel servletContextModel = (ServletContextHashModel) servletContext
					.getAttribute(ATTR_APPLICATION_MODEL);

			if (servletContextModel == null) {

				GenericServlet servlet = JspSupportServlet.jspSupportServlet;
				// TODO if the jsp support servlet isn't load-on-startup then it
				// won't exist
				// if it hasn't been accessed, and a JSP page is accessed
				if (servlet != null) {
					servletContextModel = new ServletContextHashModel(servlet, wrapper);
					servletContext.setAttribute(ATTR_APPLICATION_MODEL, servletContextModel);
					TaglibFactory taglibs = new TaglibFactory(servletContext);
					servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, taglibs);
				}

			}

			model.put(KEY_APPLICATION, servletContextModel);
			model.put(KEY_JSP_TAGLIBS, (TemplateModel) servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL));
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			model.put(KEY_SESSION_MODEL, new HttpSessionHashModel(session, wrapper));
		}

		HttpRequestHashModel requestModel = (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);

		if ((requestModel == null) || (requestModel.getRequest() != request)) {
			requestModel = new HttpRequestHashModel(request, response, wrapper);
			request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
		}
		model.put(KEY_REQUEST_MODEL, requestModel);

		HttpRequestParametersHashModel reqParametersModel = (HttpRequestParametersHashModel) request
				.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
		if (reqParametersModel == null || requestModel.getRequest() != request) {
			reqParametersModel = new HttpRequestParametersHashModel(request);
			request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL, reqParametersModel);
		}
		model.put(KEY_REQUEST_PARAMETER_MODEL, reqParametersModel);

		Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");

		if (exception == null) {
			exception = (Throwable) request.getAttribute("javax.servlet.error.JspException");
		}

		if (exception != null) {
			model.put(KEY_EXCEPTION, exception);
		}
	}
}
