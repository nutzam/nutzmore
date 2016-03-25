package org.nutz.plugins.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker2;
import org.nutz.mvc.view.AbstractPathView;

/**
 * 接口 ViewMaker2的实现，用于从 IOC 容器配置文件中查找视图。
 * @author denghuafeng(it@denghuafeng.com)
 *
 */
public class ResourceBundleViewResolver implements ViewMaker2 {
	private static final String OBJ = "obj";
	private static final String REQUEST = "request";
	private static final String RESPONSE = "response";
	private static final String SESSION = "session";
	private static final String APPLICATION = "application";
	private static final String CONFIG = "config";
	private static final String MULTI_VIEW_RESOVER = "multiViewResover";
	private static final String VIEW_NAME = "viewName";
	private static final String PATH = "path";
	private static final String BASE_PATH = "basePath";
	private static final String SERVLET_EXTENSION = "servletExtension";
	private static final String SERVLET_EXTENSION_KEY="servlet.extension";
	private static final String TPL_DIR = "tplDir";
	private static final String RESOURCE_DIR="resource.dir";
	private static final String RES_PATH = "resPath";
	private static final String TPL_RES_PATH = "tplResPath";
	private LinkedHashMap<String, AbstractTemplateViewResolver> resolvers = new LinkedHashMap<String, AbstractTemplateViewResolver>();
	private MultiViewResover multiViewResover;
	private PropertiesProxy config;
	private String appRoot;
	private boolean inited;

	@Override
	public View make(Ioc ioc, String type, String value) {
		if (!inited) {
			synchronized (resolvers) {
				if (!inited) {
					config = ioc.get(PropertiesProxy.class, CONFIG);
					multiViewResover = ioc.get(MultiViewResover.class,
							MULTI_VIEW_RESOVER);
					if (multiViewResover != null) {
						resolvers = multiViewResover.getResolvers();
					}
					if (resolvers == null || resolvers.size() == 0) {
						return null;
					}
					inited = true;
				}
			}
		}

		final AbstractTemplateViewResolver vr = resolvers.get(type);
		
		if (vr == null)
			return null;
		
		if (!vr.isInited) {
			synchronized (vr) {
				if (!vr.isInited) {
					vr.init(appRoot, Mvcs.getServletContext());
					vr.setInited(true);
				}
			}
		}
		
		return new AbstractPathView(value) {
			public void render(HttpServletRequest req,
					HttpServletResponse resp, Object obj) throws Throwable {
				Map<String, Object> sv = new HashMap<String, Object>();
				sv.put(OBJ, obj);
				sv.put(REQUEST, req);
				sv.put(RESPONSE, resp);
				sv.put(SESSION, Mvcs.getHttpSession());
				sv.put(APPLICATION, Mvcs.getServletContext());
				sv.put(VIEW_NAME, vr.getName());
				if (vr.getContentType() != null) {
					resp.setContentType(vr.getContentType());
				}
				String evalPath = evalPath(req, obj);
				String tplDir = vr.getPrefix();// 模板路径
				String ext = vr.getSuffix();// 模板文件扩展名

				if (Strings.isBlank(tplDir)) {
					tplDir = "";
				}

				if (evalPath != null && evalPath.contains("?")) { // 将参数部分分解出来
					evalPath = evalPath.substring(0, evalPath.indexOf('?'));
				}

				if (Strings.isBlank(evalPath)) {
					evalPath = Mvcs.getRequestPath(req);
					evalPath = tplDir
							+ (evalPath.startsWith("/") ? "" : "/")
							+ Files.renameSuffix(evalPath, ext);
				}
				// 绝对路径 : 以 '/' 开头的路径不增加视图配置的模板路径
				else if (evalPath.charAt(0) == '/') {
					if (!evalPath.toLowerCase().endsWith(ext))
						evalPath += ext;
				}
				// 包名形式的路径
				else {
					evalPath = tplDir + "/" + evalPath.replace('.', '/')
							+ ext;
				}

				String resDir = config.get(RESOURCE_DIR);
				if (Strings.isBlank(resDir)) {
					resDir = "";
				}
				String path = req.getContextPath();
				int serverPort = req.getServerPort();
				String basePath = req.getScheme() + "://" + req.getServerName()
						+ (serverPort != 80 ? ":" + serverPort : "")
						+ path + "/";
				sv.put(PATH, path);
				sv.put(BASE_PATH, basePath);
				sv.put(SERVLET_EXTENSION, config.get(SERVLET_EXTENSION_KEY));
				sv.put(TPL_DIR, tplDir);
				sv.put(RES_PATH, path + "/" + resDir);//资源路径
				sv.put(TPL_RES_PATH, path + "/" + resDir
						+ tplDir + "/");//模板对应的资源路径
				vr.render(req, resp, evalPath, sv);
			}
		};
	}

	@Override
	public View make(NutConfig conf, ActionInfo ai, String type, String value) {
		appRoot = conf.getAppRoot();
		return make(conf.getIoc(), type, value);
	}
}
