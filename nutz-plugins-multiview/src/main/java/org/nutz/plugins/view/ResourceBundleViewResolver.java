package org.nutz.plugins.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker2;
import org.nutz.mvc.view.AbstractPathView;

/**
 * 接口 ViewMaker2的实现，用于从 IOC 容器配置文件中查找视图。
 * 
 * @author 邓华锋(http://dhf.ink)
 *
 */
public class ResourceBundleViewResolver implements ViewMaker2 {
	private static final String INNER_VIEW_TYPE = "json|raw|re|void|http|redirect|forward|>>|->";
	private static final String CONFIG = "conf";
	private static final String MULTI_VIEW_RESOVER = "multiViewResover";
	private LinkedHashMap<String, AbstractTemplateViewResolver> resolvers = new LinkedHashMap<String, AbstractTemplateViewResolver>();
	private MultiViewResover multiViewResover;
	private PropertiesProxy config;
	private String appRoot;
	private boolean inited;

	@Override
	public View make(Ioc ioc, final String type, final String value) {
		if (!inited) {
			synchronized (resolvers) {
				if (!inited) {
					if (ioc != null) {
						try {
							config = ioc.get(PropertiesProxy.class, CONFIG);
						} catch (IocException e) {
							throw e;
						}
						multiViewResover = ioc.get(MultiViewResover.class, MULTI_VIEW_RESOVER);
					}

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
		String reqPath = value;
		boolean containsInnerType = INNER_VIEW_TYPE.indexOf(type) > -1;// 有没有内置的视图参数
		boolean containsResolversKey = resolvers.containsKey(type);// 在配置的目标视图里的前缀有没有
		boolean isNoContains = !containsInnerType && !containsResolversKey;// 如果都没有
		String viewType = type;
		if (isNoContains) {
			reqPath = type;
			// 设置默认视图
			if (Strings.isNotBlank(multiViewResover.getDefaultView())) {
				viewType = multiViewResover.getDefaultView();
			}
		}
		final AbstractTemplateViewResolver vr = resolvers.get(viewType);
		if (vr == null) {
			return null;
		}
		// 设置全局配置文件
		if (vr.getConfig() == null) {
			// 优先自定义配置
			if (multiViewResover.getConfig() != null) {
				vr.setConfig(multiViewResover.getConfig());
			} else {// 约定的配置conf
				vr.setConfig(config);
			}

		}

		if (Strings.isBlank(vr.getPrefix()) || Strings.isBlank(vr.getSuffix())) {
			throw new NullPointerException(vr.getClass().getSimpleName() + " prefix or suffix is null");
		}

		if (!vr.isInited) {
			synchronized (vr) {
				if (!vr.isInited) {
					vr.init(appRoot, Mvcs.getServletContext());
					vr.setInited(true);
				}
			}
		}
		// 用于传dest 既构造函数方式传参数 路径
		if (isNoContains) {
			return new AbstractPathView(reqPath) {
				@Override
				public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
					Map<String, Object> sourceMap = new HashMap<String, Object>();
					sourceMap.put("obj", obj);
					sourceMap.put("evalPath", this.evalPath(req, obj));
					sourceMap.put("dest", type);
					vr.render(req, resp, sourceMap);
				}
			};
		} else {
			return new AbstractPathView(reqPath) {
				@Override
				public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
					Map<String, Object> sourceMap = new HashMap<String, Object>();
					sourceMap.put("obj", obj);
					sourceMap.put("evalPath", this.evalPath(req, obj));
					sourceMap.put("dest", value);
					vr.render(req, resp, sourceMap);
				}
			};
		}

	}

	@Override
	public View make(NutConfig conf, ActionInfo ai, String type, String value) {
		appRoot = conf.getAppRoot();
		return make(conf.getIoc(), type, value);
	}
}
