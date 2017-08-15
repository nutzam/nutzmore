package org.nutz.integration.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.resource.NutResource;
import org.nutz.resource.impl.ResourceLocation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * 用法 Scans.me().add(new SpringResourceLoaction(ctx));
 * 
 * @author wendal
 *
 */
public class SpringResourceLoaction extends ResourceLocation implements ApplicationContextAware {

	protected ApplicationContext applicationContext;

	@Override
	public String id() {
		return "spring";
	}

	@Override
	public void scan(String base, Pattern pattern, List<NutResource> list) {
		base = pattern.matcher(base).find() ? "classpath*:" + base : "classpath*:" + base + "/**";
		try {
			Resource[] tmp = applicationContext.getResources(base);
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
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public class SpringResource extends NutResource {

		protected Resource resource;

		@Override
		public InputStream getInputStream() throws IOException {
			return resource.getInputStream();
		}

	}
}
