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
 * @author denghuafeng(it@denghuafeng.com)
 *
 */
public class ResourceBundleViewResolver implements ViewMaker2 {
	private static final String CONFIG = "conf";
	private static final String MULTI_VIEW_RESOVER = "multiViewResover";
	private LinkedHashMap<String, AbstractTemplateViewResolver> resolvers = new LinkedHashMap<String, AbstractTemplateViewResolver>();
	private MultiViewResover multiViewResover;
	private PropertiesProxy config;
	private String appRoot;
	private boolean inited;

	@Override
	public View make(Ioc ioc, String type, final String value) {
		if (!inited) {
			synchronized (resolvers) {
				if (!inited) {
					if(ioc!=null){
						try {
							config = ioc.get(PropertiesProxy.class, CONFIG);
						} catch (IocException e) {
							//org.nutz.ioc.IocException: [conf] # For object [conf] - type:[class org.nutz.ioc.impl.PropertiesProxy]
						}
						multiViewResover = ioc.get(MultiViewResover.class,
								MULTI_VIEW_RESOVER);
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
		//TODO 定义为Object 类型，通过反射去调用公用的方法，为了方便子类扩展属性
		final AbstractTemplateViewResolver vr = resolvers.get(type);
		if(vr==null){
			return null;
		}
		
		if(vr.getConfig()==null){
			vr.setConfig(config);
		}
		
		if (Strings.isBlank(vr.getPrefix()) || Strings.isBlank(vr.getSuffix())) {
			throw new NullPointerException(vr.getClass().getSimpleName()
					+ " prefix or suffix is null");
		}

		if (!vr.isInited) {
			synchronized (vr) {
				if (!vr.isInited) {
					vr.init(appRoot, Mvcs.getServletContext());
					vr.setInited(true);
				}
			}
		}

		return new AbstractPathView(value){
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

	@Override
	public View make(NutConfig conf, ActionInfo ai, String type, String value) {
		appRoot = conf.getAppRoot();
		return make(conf.getIoc(), type, value);
	}
}
