package org.nutz.plugins.view;

import java.util.LinkedHashMap;

/**
 * 用于注入的多视图
 * @author denghuafeng (it@denghuafeng.com)
 *
 */
public class MultiViewResover {
	private LinkedHashMap<String, AbstractTemplateViewResolver> resolvers;

	public LinkedHashMap<String, AbstractTemplateViewResolver> getResolvers() {
		return resolvers;
	}

	public void setResolvers(LinkedHashMap<String, AbstractTemplateViewResolver> resolvers) {
		this.resolvers = resolvers;
	}
}
