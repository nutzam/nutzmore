package org.nutz.plugins.view;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * @author denghuafeng(it@denghuafeng.com)
 *
 */
public abstract class AbstractTemplateViewResolver extends AbstractPathView {
	protected  final Log log = Logs.get();
	private static final String OBJ = "obj";
	private static final String REQUEST = "request";
	private static final String RESPONSE = "response";
	private static final String SESSION = "session";
	private static final String APPLICATION = "application";
	private static final String VIEW_NAME = "viewName";
	private static final String PATH = "path";
	private static final String BASE_PATH = "basePath";
	private static final String SERVLET_EXTENSION = "servletExtension";
	private static final String SERVLET_EXTENSION_KEY = "servlet.extension";
	private static final String TPL_DIR = "tplDir";
	private static final String RESOURCE_DIR = "resource.dir";
	private static final String RES_PATH = "resPath";
	private static final String TPL_RES_PATH = "tplResPath";
	private static final String WEB_INF = "WEB-INF/";
	private static final String PROPS = "props";
	private static final String MVCS = "mvcs";
	private static final String CFG="cfg";
	private static final String EVAL_PATH="evalPath";
	private static final String DEST="dest";
	private PropertiesProxy config;
	private String prefix = "";
	private String suffix = "";
	private String contentType;
	private String configPath;
	// 扩展属性
	private NutMap extAttrs = new NutMap();
	protected boolean isInited;

	public AbstractTemplateViewResolver(String dest) {
		super(dest);
	}

	protected abstract void init(String appRoot, ServletContext sc);

	protected abstract void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
			Map<String, Object> sharedVars) throws Throwable;

	@SuppressWarnings("unchecked")
	public final void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		Map<String, Object> sourceMap=null;
		try {
			if(obj!=null&&obj instanceof Map){
				sourceMap = org.nutz.castor.Castors.me().castTo(obj, Map.class);
				if(!sourceMap.containsKey(EVAL_PATH)||!sourceMap.containsKey(DEST)){//验证必要的key值
					sourceMap=null;
				}
			}
		} catch (Exception e1) {
			//e1.printStackTrace();
		}
		Map<String, Object> sv = new HashMap<String, Object>();
		Object objOld=sourceMap==null?obj:sourceMap.get("obj");
		sv.put(OBJ, objOld);
		sv.put(REQUEST, req);
		sv.put(RESPONSE, resp);
		sv.put(SESSION, Mvcs.getHttpSession());
		sv.put(APPLICATION, Mvcs.getServletContext());
		sv.put(VIEW_NAME, this.getClass().getSimpleName());
		sv.put(PROPS, System.getProperties());// .get("java.version")
		Map<String, String> msgs = Mvcs.getMessages(req);
		sv.put(MVCS, msgs);
		sv.put(CFG, config);
		if (resp != null && Strings.isBlank(resp.getContentType()) && !Strings.isBlank(this.getContentType())) {// resp的contentType优先级高
			resp.setContentType(this.getContentType());// 配置文件设置的contentType
		}

		String evalPath = null;
		if(sourceMap!=null&&sourceMap.get(DEST)!=null&&Strings.isBlank(evalPath)){
			evalPath=sourceMap.get(EVAL_PATH).toString();
		}else{
			evalPath=evalPath(req, obj);
		}
		String tplDir = this.getPrefix();// 模板路径
		String ext = this.getSuffix();// 模板文件扩展名

		if (Strings.isBlank(tplDir)) {
			tplDir = "";
		}

		if (evalPath != null && evalPath.contains("?")) { // 将参数部分分解出来
			evalPath = evalPath.substring(0, evalPath.indexOf('?'));
		}

		if (Strings.isBlank(evalPath)) {
			evalPath = Mvcs.getRequestPath(req);
			evalPath = tplDir + (evalPath.startsWith("/") ? "" : "/") + Files.renameSuffix(evalPath, ext);
		}else if (evalPath.charAt(0) == '/') {// 绝对路径 : 以 '/' 开头的路径不增加视图配置的模板路径
			if (!evalPath.toLowerCase().endsWith(ext))
				evalPath += ext;
		}else {// 包名形式的路径
			evalPath = tplDir + "/" + evalPath.replace('.', '/') + ext;
		}

		String resDir = "";
		if (config != null) {
			resDir = config.get(RESOURCE_DIR);
		}

		String path = req.getContextPath();
		try {
			int serverPort = req.getServerPort();
			String basePath = req.getScheme() + "://" + req.getServerName() + (serverPort != 80 ? ":" + serverPort : "")
					+ path + "/";
			sv.put(BASE_PATH, basePath);
		} catch (Exception e) {// 为了测试，而try的，Mock没有ServerPort Scheme ServerName
			if (Strings.equals("Not implement yet!", e.getMessage())) {
			}
		}

		sv.put(PATH, path);

		String servletExtension = "";
		if (config != null) {
			servletExtension = config.get(SERVLET_EXTENSION_KEY);
		}
		sv.put(SERVLET_EXTENSION, servletExtension);
		sv.put(TPL_DIR, tplDir);
		if (!resDir.startsWith("http")) {// 如果是http开头，说明是CDN静态地址
			resDir = path + "/" + resDir;
		}
		sv.put(RES_PATH, resDir);// 资源路径
		sv.put(TPL_RES_PATH, resDir + tplDir.replace(WEB_INF, "") + "/");// 模板对应的资源路径
		this.render(req, resp, evalPath, sv);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getContentType() {
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

	public NutMap getExtAttrs() {
		return extAttrs;
	}

	public void setExtAttrs(NutMap extAttrs) {
		this.extAttrs = extAttrs;
	}

	protected boolean isInited() {
		return isInited;
	}

	protected void setInited(boolean isInited) {
		this.isInited = isInited;
	}
}
