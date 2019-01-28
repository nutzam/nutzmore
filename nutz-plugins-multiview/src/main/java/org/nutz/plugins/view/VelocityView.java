package org.nutz.plugins.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.ServletUtils;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.config.FilterNutConfig;
import org.nutz.mvc.config.ServletNutConfig;

public class VelocityView extends AbstractTemplateViewResolver {
	private org.apache.velocity.tools.view.VelocityView view;
	private String defaultLayout;
	private String layoutDir;
	private String vmPath;
	private boolean bufferOutput = false;
	private String errorTemplate;
	
	public VelocityView(String dest) {
		super(dest);
	}
	
	@Override
	protected void init(String appRoot, ServletContext sc) {
			//优先级：1.ini-param  2.template setting config 3.default properties
			String properties_path = Mvcs.getNutConfig().getInitParameter("org.apache.velocity.properties");
			if(Strings.isNotBlank(properties_path)) {
				super.setConfig(new PropertiesProxy(properties_path));
			}else if(super.getConfig()==null) {
				properties_path ="org.apache.velocity.properties";
				super.setConfig(new PropertiesProxy(properties_path));
			}
		  
			PropertiesProxy propertiesProxy =super.getConfig();
			this.errorTemplate = propertiesProxy.get("tools.view.servlet.error.template", "Error.vm").trim();
			this.layoutDir = propertiesProxy.get("tools.view.servlet.layout.directory", "layout/").trim();
			this.defaultLayout = propertiesProxy.get("tools.view.servlet.layout.default.template", "Default.vm").trim();
			if (!this.layoutDir.endsWith("/")) {
				this.layoutDir += '/';
			}
			this.defaultLayout = (this.layoutDir + this.defaultLayout);
	}

	@Override
	protected void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
			Map<String, Object> sharedVars) throws Throwable {
		vmPath = evalPath;
		Context context = null;
		try {
			context = createContext(req, resp);
			fillContext(context, req);
			setContentType(req, resp);
			Template template = handleRequest(req, resp, context);
			mergeTemplate(template, context, resp);
		} catch (IOException e) {
			throw e;
		} catch (ResourceNotFoundException e) {
			manageResourceNotFound(req, resp, e);
		} catch (RuntimeException e) {
			throw e;
		} finally {
			requestCleanup(req, resp, context);
		}
	}
	
	/**
	 * @param request
	 * @param response
	 * @param context
	 */
	private void requestCleanup(HttpServletRequest request, HttpServletResponse response, Context context) {
	}

	protected void manageResourceNotFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException e) throws IOException {
		String path = ServletUtils.getPath(request);
		log.debug("Resource not found for path '" + path + "'", e);
		String message = e.getMessage();
		if ((!response.isCommitted()) && (path != null) && (message != null) && (message.contains("'" + path + "'"))) {
			response.sendError(404, path);
		} else {
			error(request, response, e);
			throw e;
		}
	}

	/**
	 * @param request
	 * @param response
	 * @param e
	 */
	private void error(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException ex) {
		try {
			Context ctx = createContext(request, response);
			Throwable cause = ex;
			if ((cause instanceof MethodInvocationException)) {
				ctx.put("invocation_exception", ex);
				cause = ex.getWrappedThrowable();
			}
			ctx.put("error_cause", cause);
			StringWriter sw = new StringWriter();
			cause.printStackTrace(new PrintWriter(sw));
			ctx.put("stack_trace", sw.toString());
			Template et = getTemplate(this.errorTemplate);
			mergeTemplate(et, ctx, response);
		} catch (Exception e) {
			log.error("Error during error template rendering", e);
			if (!response.isCommitted()) {
				return;
			}
			try {
				String path = ServletUtils.getPath(request);
				log.error("Error processing a template for path '" + path + "'", e);
				StringBuilder html = new StringBuilder();
				html.append("<html>\n");
				html.append("<head><title>Error</title></head>\n");
				html.append("<body>\n");
				html.append("<h2>VelocityView : Error processing a template for path '");
				html.append(path);
				html.append("'</h2>\n");
				Throwable cause = e;
				String why = cause.getMessage();
				if ((why != null) && (why.length() > 0)) {
					html.append(StringEscapeUtils.escapeHtml(why));
					html.append("\n<br>\n");
				}
				if ((cause instanceof MethodInvocationException)) {
					cause = ((MethodInvocationException) cause).getWrappedThrowable();
				}
				StringWriter sw = new StringWriter();
				cause.printStackTrace(new PrintWriter(sw));
				html.append("<pre>\n");
				html.append(StringEscapeUtils.escapeHtml(sw.toString()));
				html.append("</pre>\n");
				html.append("</body>\n");
				html.append("</html>");
				response.getWriter().write(html.toString());
			} catch (Exception e2) {
				String msg = "Exception while printing error screen";
				log.error(msg, e2);
				throw new RuntimeException(msg, e);
			}
		}
	}

	protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws IOException {
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		context.put("screen_content", sw.toString());
		Object obj = context.get("layout");
		String layout = obj == null ? null : obj.toString();
		if (layout == null) {
			layout = this.defaultLayout;
		} else {
			layout = this.layoutDir + layout;
		}
		try {
			template = getTemplate(layout);
		} catch (Exception e) {
			log.error("Can't load layout \"" + layout + "\"", e);
			if (!layout.equals(this.defaultLayout)) {
				template = getTemplate(this.defaultLayout);
			}
		}
		Writer writer;
		if (this.bufferOutput) {
			writer = new StringWriter();
		} else {
			writer = response.getWriter();
		}
		getVelocityView().merge(template, context, writer);
		if (this.bufferOutput) {
			response.getWriter().write(writer.toString());
		}
	}

	protected Template getTemplate(String name) {
		return getVelocityView().getTemplate(name);
	}

	/**
	 * 真实的处理请求的
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @return
	 */
	private Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) {
		Enumeration<String> attrs = request.getAttributeNames();
		while (attrs.hasMoreElements()) {
			String attr = attrs.nextElement();
			context.put(attr, request.getAttribute(attr));
		}
		return getVelocityView().getTemplate(vmPath);
	}

	protected void setContentType(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(getVelocityView().getDefaultContentType());
	}

	protected void fillContext(Context ctx, HttpServletRequest request) {
		String layout = findLayout(request);
		if (layout != null) {

			ctx.put("layout", layout);
		}
	}

	protected String findLayout(HttpServletRequest request) {
		String layout = request.getParameter("layout");
		if (layout == null) {
			layout = (String) request.getAttribute("layout");
		}
		return layout;
	}

	/**
	 * 创建模板上下文
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
		return getVelocityView().createContext(request, response);
	}

	/**
	 * 获取view
	 * 
	 * @return
	 */
	protected org.apache.velocity.tools.view.VelocityView getVelocityView() {
		if (this.view == null) {
			try {
				Object config_ = getNutConfig();
				if (config_ instanceof FilterConfig) {
					setVelocityView(ServletUtils.getVelocityView((FilterConfig) config_));
				} else {
					setVelocityView(ServletUtils.getVelocityView((ServletConfig) config_));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				log.error(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				log.error(e);
			}
			assert (this.view != null);
		}
		return this.view;
	}

	protected Object getNutConfig() throws IllegalArgumentException, IllegalAccessException {
		NutConfig config = Mvcs.getNutConfig();
		if (config instanceof FilterNutConfig) {
			FilterNutConfig con = ((FilterNutConfig) Mvcs.getNutConfig());
			for (Field f : con.getClass().getDeclaredFields()) {
				if (Strings.equalsIgnoreCase("config", f.getName())) {
					f.setAccessible(true);
					return f.get(con);
				}
			}
		} else {
			ServletNutConfig con = ((ServletNutConfig) Mvcs.getNutConfig());
			for (Field f : con.getClass().getDeclaredFields()) {
				if (Strings.equalsIgnoreCase("config", f.getName())) {
					f.setAccessible(true);
					return f.get(con);
				}
			}
		}
		return null;
	}

	/**
	 * @param velocityView
	 */
	private void setVelocityView(org.apache.velocity.tools.view.VelocityView velocityView) {
		this.view = velocityView;
	}
}
