package org.nutz.plugin.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nutz.dao.runtime")
public class NutzDaoRuntimeProperties {

	private boolean create;

	private boolean migration;

	private String[] basepackage;

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public boolean isMigration() {
		return migration;
	}

	public void setMigration(boolean migration) {
		this.migration = migration;
	}

	public String[] getBasepackage() {
		return basepackage;
	}

	public void setBasepackage(String[] basepackage) {
		this.basepackage = basepackage;
	}

}
