package org.nutz.plugins.view.freemarker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.ClassTools;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

public class FreeMarkerConfigurer {

	private final static Log log = Logs.get();

	private Configuration configuration;
	private String prefix;
	private String suffix;
	private FreemarkerDirectiveFactory freemarkerDirectiveFactory;
	private Map<String, Object> tags = new HashMap<String, Object>();
	private TemplateLoader templateLoader;

	public FreeMarkerConfigurer() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
		this.initp(configuration, Mvcs.getServletContext(), "/WEB-INF", ".ftl", new FreemarkerDirectiveFactory());
	}

	public FreeMarkerConfigurer(Configuration configuration, ServletContext sc, String prefix, String suffix, FreemarkerDirectiveFactory freemarkerDirectiveFactory) {
		this.initp(configuration, sc, prefix, suffix, freemarkerDirectiveFactory);
	}

	protected void initp(Configuration configuration, ServletContext sc, String prefix, String suffix, FreemarkerDirectiveFactory freemarkerDirectiveFactory) {
        this.configuration = configuration;
        URL url = ClassTools.getClassLoader().getResource(prefix);
        String path = url == null ? null : url.getPath();
        if (path != null) {
            if (path.contains("jar!")) {
                this.prefix = prefix;
                log.info("using classload for TemplateLoading : " + prefix);
                templateLoader = new ClassTemplateLoader(getClass().getClassLoader(), prefix);
            }
            else {
                this.prefix = path;
            }
        }
        this.suffix = suffix;
        this.freemarkerDirectiveFactory = freemarkerDirectiveFactory;
        if (this.prefix == null) {
            log.info("using ServletContext path mode " + prefix);
            this.prefix = sc.getRealPath("/") + prefix;
        }

        this.configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        this.configuration.setTemplateUpdateDelayMilliseconds(-1000);
        this.configuration.setDefaultEncoding("UTF-8");
        this.configuration.setURLEscapingCharset("UTF-8");
        this.configuration.setLocale(Locale.CHINA);
        this.configuration.setBooleanFormat("true,false");
        this.configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
        this.configuration.setDateFormat("yyyy-MM-dd");
        this.configuration.setTimeFormat("HH:mm:ss");
        this.configuration.setNumberFormat("0.######");
        this.configuration.setWhitespaceStripping(true);
        log.info("prefix = " + prefix);
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
			Iterator<Entry<String, Object>> iterator = tags.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				configuration.setSharedVariable(entry.getKey(), entry.getValue());
			}
			if (freemarkerDirectiveFactory == null)
				return;
			for (FreemarkerDirective freemarkerDirective : freemarkerDirectiveFactory.getList()) {
				configuration.setSharedVariable(freemarkerDirective.getName(), freemarkerDirective.getTemplateDirectiveModel());
			}
		} catch (IOException e) {
			log.error(e);
		} catch (TemplateException e) {
			log.error(e);
		}
	}

	public String getSuffix() {
		return Strings.isBlank(freemarkerDirectiveFactory.getSuffix()) ? this.suffix : freemarkerDirectiveFactory.getSuffix();
	}

	public String getPrefix() {
		return prefix;
	}

	protected void initFreeMarkerConfigurer() throws IOException, TemplateException {
		String path = freemarkerDirectiveFactory.getFreemarker();
        if (templateLoader == null) {
            File file = Files.findFile(path);
            if (!Lang.isEmpty(file)) {
                Properties p = new Properties();
                p.load(Streams.fileIn(file));
                configuration.setSettings(p);
            }
            File f = Files.findFile(prefix);
            if (f == null || f.getPath().contains("jar!")) {
                log.info("using classload for TemplateLoading : " + prefix);
                configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), prefix);
            }
            else {
                log.info("using Directory for TemplateLoading : " + prefix);
                configuration.setDirectoryForTemplateLoading(f);
            }
        }
        else {
            configuration.setTemplateLoader(templateLoader);
        }
	}

	public void setTags(Map<String, Object> map) {
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			String key = entry.getKey();
			Object obj = entry.getValue();
			tags.put(key, obj);
		}
	}

	public FreeMarkerConfigurer setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public FreeMarkerConfigurer setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	/**
	 *
	 * @param map
	 * @return
	 *
	 *         mapTags : { factory : "$freeMarkerConfigurer#addTags", args : [ {
	 *         'abc' : 1, 'def' : 2 } ] }
	 */
	public FreeMarkerConfigurer addTags(Map<String, Object> map) {
		if (map != null) {
			try {
				configuration.setAllSharedVariables(new SimpleHash(map, new DefaultObjectWrapper(Configuration.VERSION_2_3_28)));
			} catch (TemplateModelException e) {
				log.error(e);
			}
		}
		return this;
	}
	
	public void setTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }
	
	
}
