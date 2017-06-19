package org.nutz.plugin.spring.boot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugin.spring.boot.config.SqlManagerProperties;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.ResourceLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;

@Configuration
@ConditionalOnClass(SqlManager.class)
@EnableConfigurationProperties(SqlManagerProperties.class)
public class FileSqlManagerAutoConfiguration extends ApplicationObjectSupport implements ServletContextAware {

	private ServletContext servletContext;

	Log log = Logs.get();

	@Override
	public void setServletContext(ServletContext sc) {
		this.servletContext = sc;
	}

	public FileSqlManagerAutoConfiguration() {
	}

	@Autowired
	private SqlManagerProperties sqlManagerProperties;

	public class SpringResource extends NutResource {

		protected Resource resource;

		@Override
		public InputStream getInputStream() throws IOException {
			return resource.getInputStream();
		}

	}

	@PostConstruct
	public void init() {// 初始化一下nutz的扫描
		if (servletContext != null)
			Scans.me().init(servletContext).addResourceLocation(new ResourceLocation() {// spring的classpath下的也扫进来

				@Override
				public void scan(String base, Pattern pattern, List<NutResource> list) {
					base = pattern.matcher(base).find() ? "classpath*:" + base : "classpath*:" + base + "/**";
					try {
						Resource[] tmp = getApplicationContext().getResources(base);
						for (Resource resource : tmp) {
							if (resource.getFilename() == null)
								continue;
							if (pattern != null && !pattern.matcher(resource.getFilename()).find()) {
								continue;
							}
							SpringResource sr = new SpringResource();
							sr.resource = resource;
							sr.setName(resource.getFilename());
							sr.setSource("spring");
							list.add(sr);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public String id() {
					return "spring";
				}
			});
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlManager sqlManager() {
		String[] paths = sqlManagerProperties.getPaths();
		if (paths == null) {
			paths = new String[] { "sqls" };
		}
		return new FileSqlManager(paths);
	}

}
