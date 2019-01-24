package org.nutz.plugins.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;
import org.nutz.mvc.view.AbstractPathView;

/**
 * 接口 ViewMaker2的实现，用于从 IOC 容器配置文件中查找视图。
 * 
 * @author 邓华锋(http://dhf.ink)
 *
 */
public class ResourceBundleViewResolver implements MultiView {
	private LinkedHashMap<String, AbstractTemplateViewResolver> resolvers = new LinkedHashMap<String, AbstractTemplateViewResolver>();
	private PropertiesProxy config = new PropertiesProxy();
	private String defaultView;
	private String appRoot;
	private boolean inited;

	@Override
	public View make(Ioc ioc, final String type, final String value) {
		if (!inited) {
			synchronized (resolvers) {
				if (!inited) {
					if (ioc != null) {
						// 查找MultiViewResover类型的定义名称，进行映射配置
						String[] names = ioc.getNamesByType(MultiViewResover.class);
						MultiViewResover defaultMultiViewResover = null;
						for (int i = 0; i < names.length; i++) {
							String name = names[i];
							MultiViewResover multiViewResover = ioc.get(MultiViewResover.class, name);
							defaultMultiViewResover = multiViewResover;
							if (multiViewResover != null) {
								LinkedHashMap<String, AbstractTemplateViewResolver> rs = multiViewResover
										.getResolvers();
								if (rs == null || rs.size() == 0) {
									continue;
								}
								resolvers.putAll(rs);// 叠加配置视图
							}
							config.putAll(multiViewResover.getConfig());// 叠加配置文件
							// 设置默认视图
							if (Strings.isNotBlank(multiViewResover.getDefaultView())) {
								defaultView = multiViewResover.getDefaultView();
							}
						}
						//如果默认视图没有设置，则默认为Map列表第一个视图，跟顺序有关
						if(Strings.isBlank(defaultView)&&defaultMultiViewResover!=null) {
							Iterator<Entry<String,AbstractTemplateViewResolver>> it=defaultMultiViewResover.getResolvers().entrySet().iterator();
							if(it.hasNext()) {
								defaultView=it.next().getKey();
							}
						}
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
			if (Strings.isNotBlank(defaultView)) {
				viewType = defaultView;
			}
		}
		final AbstractTemplateViewResolver vr = resolvers.get(viewType);
		if (vr == null) {
			return null;
		}

		if (Strings.isBlank(vr.getPrefix()) || Strings.isBlank(vr.getSuffix())) {
			throw new NullPointerException(vr.getClass().getSimpleName() + " prefix or suffix is null");
		}

		if (!vr.isInited) {
			synchronized (vr) {
				if (!vr.isInited) {
					vr.init(appRoot, Mvcs.getServletContext());
					// 合并到全局配置文件中
					if (vr.getConfig() != null) {
						if (config != null) {
							config.putAll(vr.getConfig().toMap());
						}
					}
					vr.setInited(true);
				}
			}
		}
		// 用于传dest 既构造函数方式传参数 路径
		if (isNoContains) {
			return new AbstractPathView(reqPath) {
				@Override
				public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
					renderView(type, vr, req, resp, obj, this.evalPath(req, obj));
				}
			};
		} else {
			return new AbstractPathView(reqPath) {
				@Override
				public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
					renderView(value, vr, req, resp, obj, this.evalPath(req, obj));
				}
			};
		}
	}

	@Override
	public View make(NutConfig conf, ActionInfo ai, String type, String value) {
		appRoot = conf.getAppRoot();
		return make(conf.getIoc(), type, value);
	}

	private void setDefaultView(String defaultView) {
		this.defaultView = defaultView;
	}

	private void renderView(final String dest, final AbstractTemplateViewResolver vr, HttpServletRequest req,
			HttpServletResponse resp, Object obj, String evalPath) throws Throwable {
		ServletContext application = Mvcs.getServletContext();
		// application级别 动态切换模板引擎
		if (application.getAttribute(DEFAULT_VIEW) != null) {
			setDefaultView(application.getAttribute(DEFAULT_VIEW).toString());
		}
		HttpSession session = Mvcs.getHttpSession();
		// session级别 动态切换模板引擎
		if (session.getAttribute(DEFAULT_VIEW) != null) {
			setDefaultView(session.getAttribute(DEFAULT_VIEW).toString());
		}
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(OBJ, obj);
		sourceMap.put(EVAL_PATH, evalPath);
		sourceMap.put(DEST, dest);
		vr.render(req, resp, sourceMap);
	}

	public LinkedHashMap<String, AbstractTemplateViewResolver> getResolvers() {
		return resolvers;
	}

	public void setResolvers(LinkedHashMap<String, AbstractTemplateViewResolver> resolvers) {
		this.resolvers = resolvers;
	}
}
