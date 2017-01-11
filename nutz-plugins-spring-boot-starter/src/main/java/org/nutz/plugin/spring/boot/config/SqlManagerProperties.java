package org.nutz.plugin.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nutz.dao.sqlmanager")
public class SqlManagerProperties {

	private String[] paths;

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

}
