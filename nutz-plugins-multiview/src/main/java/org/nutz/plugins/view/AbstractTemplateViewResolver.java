package org.nutz.plugins.view;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.AbstractPathView;

/**
 * 视图模板类，其他模板视图需继承此抽象类
 * 
 * @author 邓华锋(http://dhf.ink)
 *
 */
public abstract class AbstractTemplateViewResolver extends AbstractPathView {
	protected static final Log log = Logs.get();
	private PropertiesProxy config;
	private String prefix;
	private String suffix;
	private String contentType;
	private String encoding;
	private String configPath;
	// 扩展属性
	protected NutMap properties = new NutMap();
	protected boolean isInited;
	// 是否在init方法里掉用了getPrefix()来设置了前缀，如果有则在拼接路径时不需要加上前缀
	protected boolean isInitedSetPrefix;

	public AbstractTemplateViewResolver(String dest) {
		super(dest);
	}

	protected abstract void init(String appRoot, ServletContext sc);

	protected abstract void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
			Map<String, Object> sharedVars) throws Throwable;

	@SuppressWarnings("unchecked")
	public final void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		Map<String, Object> sourceMap = null;
		try {
			if (obj != null && obj instanceof Map) {
				sourceMap = org.nutz.castor.Castors.me().castTo(obj, Map.class);
				if (!sourceMap.containsKey(MultiView.EVAL_PATH) || !sourceMap.containsKey(MultiView.DEST)) {// 验证必要的key值
					sourceMap = null;
				}
			}
		} catch (Exception e) {
			throw e;
		}

		Map<String, Object> sv = new HashMap<String, Object>();
		Object objOld = sourceMap == null ? obj : sourceMap.get("obj");
		sv.put(MultiView.OBJ, objOld);
		sv.put(MultiView.REQUEST, req);
		sv.put(MultiView.RESPONSE, resp);
		HttpSession session = Mvcs.getHttpSession();
		sv.put(MultiView.SESSION, session);
		ServletContext application = Mvcs.getServletContext();
		sv.put(MultiView.APPLICATION, application);
		sv.put(MultiView.VIEW_NAME, this.getClass().getSimpleName());
		sv.put(MultiView.PROPS, System.getProperties());
		sv.put(MultiView.MSGS, Mvcs.getMessages(req));
		sv.put(MultiView.CFG, config);

		if (resp != null && Strings.isBlank(resp.getContentType()) && !Strings.isBlank(this.getContentType())) {// resp的contentType优先级高
			resp.setContentType(this.getContentType() + "; charset=" + this.getEncoding());// 配置文件设置的contentType
			resp.setCharacterEncoding(this.getEncoding());
		}
		String evalPath = null;
		if (sourceMap != null && sourceMap.get(MultiView.DEST) != null && Strings.isBlank(evalPath)) {
			sv.put(MultiView.DEST, sourceMap.get(MultiView.EVAL_PATH).toString());
			evalPath = sourceMap.get(MultiView.EVAL_PATH).toString();
		} else {
			evalPath = evalPath(req, obj);
		}
		String tplDir = this.getPrefix();// 模板路径
		// application级别 动态切换模板路径
		Object viewPrefix = application.getAttribute(MultiView.VIEW_PREFIX);
		if (viewPrefix != null) {
			this.setPrefix(viewPrefix.toString());
			tplDir = viewPrefix.toString();
		}
		// session级别 动态切换模板路径
		viewPrefix = session.getAttribute(MultiView.VIEW_PREFIX);
		if (viewPrefix != null) {
			this.setPrefix(viewPrefix.toString());
			tplDir = viewPrefix.toString();
		}
		String ext = this.getSuffix();// 模板文件扩展名
		// application级别 动态切换模板路径
		Object viewSuffix = application.getAttribute(MultiView.VIEW_SUFFIX);
		if (viewSuffix != null) {
			this.setSuffix(viewSuffix.toString());
			ext = viewSuffix.toString();
		}
		// session级别 动态切换模板后缀
		viewSuffix = session.getAttribute(MultiView.VIEW_SUFFIX);
		if (viewSuffix != null) {
			this.setSuffix(viewSuffix.toString());
			ext = viewSuffix.toString();
		}

		if (Strings.isBlank(tplDir)) {
			tplDir = "";
		}

		if (evalPath != null && evalPath.contains("?")) { // 将参数部分分解出来
			evalPath = evalPath.substring(0, evalPath.indexOf('?'));
		}

		if (Strings.isBlank(evalPath)) {
			evalPath = Mvcs.getRequestPath(req);
			evalPath = tplDir + (evalPath.startsWith("/") ? "" : "/") + Files.renameSuffix(evalPath, ext);
		} else if (evalPath.charAt(0) == '/') {// 绝对路径 : 以 '/' 开头的路径不增加视图配置的模板路径
			if (!evalPath.toLowerCase().endsWith(ext))
				evalPath += ext;
		} else {// 包名形式的路径
			if (isInitedSetPrefix) {// TODO 考虑Thymeleaf的视图切换问题
				tplDir = "";
			}
			evalPath = tplDir + "/" + evalPath.replace('.', '/') + ext;
		}

		String resDir = "";
		if (config != null) {
			resDir = config.get(MultiView.RESOURCE_DIR);
			// 始终保持空字符串，以防后面出现空指针异常
			if (Strings.isBlank(resDir)) {
				resDir = "";
			}
		}

		String path = req.getContextPath();
		try {
			int serverPort = req.getServerPort();
			String basePath = req.getScheme() + "://" + req.getServerName() + (serverPort != 80 ? ":" + serverPort : "")
					+ path + "/";
			sv.put(MultiView.BASE_PATH, basePath);
		} catch (Exception e) {// 为了测试，而try的，Mock没有ServerPort Scheme ServerName
			if (Strings.equals("Not implement yet!", e.getMessage())) {
			}
		}

		sv.put(MultiView.PATH, path);

		String servletExtension = "";
		if (config != null) {
			servletExtension = config.get(MultiView.SERVLET_EXTENSION_KEY);
		}
		sv.put(MultiView.SERVLET_EXTENSION, servletExtension);
		sv.put(MultiView.TPL_DIR, tplDir);
		if (!resDir.startsWith("http")) {// 如果是http开头，说明是CDN静态地址
			resDir = path + "/" + resDir;
		}
		sv.put(MultiView.RES_PATH, resDir);// 资源路径
		sv.put(MultiView.TPL_RES_PATH, resDir + tplDir.replace(MultiView.WEB_INF, "") + "/");// 模板对应的资源路径
		this.render(req, resp, evalPath, sv);
	}

	public String getPrefix() {
		if (Strings.isBlank(prefix)) {
			return MultiView.DEFAULT_PREFIX;
		}
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		if (Strings.isBlank(suffix)) {
			return MultiView.DEFAULT_SUFFIX;
		}
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getContentType() {
		if (Strings.isBlank(contentType)) {
			return MultiView.DEFAULT_CONTENT_TYPE;
		}
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public PropertiesProxy getConfig() {
		return config;
	}

	public void setConfig(PropertiesProxy config) {
		this.config = config;
	}

	protected boolean isInited() {
		return isInited;
	}

	protected void setInited(boolean isInited) {
		this.isInited = isInited;
	}

	public NutMap getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = new NutMap(properties);
	}

	public String getEncoding() {
		if (Strings.isBlank(this.encoding)) {
			return MultiView.DEFAULT_ENCODING;
		}
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
