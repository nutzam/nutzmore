package org.nutz.plugins.view;

import java.util.LinkedHashMap;

import org.nutz.ioc.impl.PropertiesProxy;

/**
 * 用于注入的多视图
 * 
 * @author 邓华锋(http://dhf.ink)
 *
 */
public class MultiViewResover {
	private PropertiesProxy config;
	private String defaultView;
	private LinkedHashMap<String, AbstractTemplateViewResolver> resolvers;

	public String getDefaultView() {
		return defaultView;
	}

	public void setDefaultView(String defaultView) {
		this.defaultView = defaultView;
	}

	public LinkedHashMap<String, AbstractTemplateViewResolver> getResolvers() {
		return resolvers;
	}

	public void setResolvers(LinkedHashMap<String, AbstractTemplateViewResolver> resolvers) {
		this.resolvers = resolvers;
	}

	public PropertiesProxy getConfig() {
		return config;
	}

}
