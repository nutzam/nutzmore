package org.nutz.plugins.view.freemarker;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class FreeMarkerConfigurer {
    
	private Configuration configuration;
	private String prefix;
	private String suffix;
	private FreemarkerDirectiveFactory freemarkerDirectiveFactory;
	
	/**
	 * 默认配置   前缀 /WEB-INF 也就是模板基路径
	 *          后缀 .ftl
	 *          
	 */
	public FreeMarkerConfigurer() {
	    Configuration configuration = new Configuration();
	    this.initp(configuration, Mvcs.getServletContext(), "WEB-INF", ".ftl", null);
	}

	public FreeMarkerConfigurer(Configuration configuration, ServletContext sc, String prefix, String suffix, FreemarkerDirectiveFactory freemarkerDirectiveFactory) {
		this.initp(configuration, sc, prefix, suffix, freemarkerDirectiveFactory);
	}
	
	protected void initp(Configuration configuration, ServletContext sc, String prefix, String suffix, FreemarkerDirectiveFactory freemarkerDirectiveFactory) {
	    this.configuration = configuration;
        this.prefix = sc.getRealPath(prefix);
        this.suffix = suffix;
        this.freemarkerDirectiveFactory = freemarkerDirectiveFactory;
        if (this.prefix == null)
            this.prefix = sc.getRealPath("/") + prefix;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void init() {
		try {
			initFreeMarkerConfigurer();
		} catch (IOException e) {
			Lang.wrapThrow(e);
		} catch (TemplateException e) {
			Lang.wrapThrow(e);
		}
        if (freemarkerDirectiveFactory == null)
            return;
		for (FreemarkerDirective freemarkerDirective : freemarkerDirectiveFactory.getList()) {
			configuration.setSharedVariable(freemarkerDirective.getName(), freemarkerDirective.getTemplateDirectiveModel());
		}
	}

	public String getSuffix() {
		return suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	protected void initFreeMarkerConfigurer() throws IOException, TemplateException {
//	    Properties p = new Properties();
//	    p.load(getClass().getResourceAsStream("freemarker.properties"));
//	    configuration.setSettings(p);
//		configuration.setDirectoryForTemplateLoading(Files.findFile(prefix));
	}
}