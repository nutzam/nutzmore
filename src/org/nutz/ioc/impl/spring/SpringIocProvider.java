package org.nutz.ioc.impl.spring;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocException;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.lang.Strings;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.init.NutConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * 简单实现Nutz.IoC-Spring桥
 * <p/>
 * Need Spring 2.0 or later
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class SpringIocProvider implements IocProvider, Ioc {

	protected ApplicationContext applicationContext;

	public Ioc create(NutConfig config, String[] args) {
		if (config == null)
			applicationContext = new ClassPathXmlApplicationContext(args);
		else
			applicationContext = (ApplicationContext) config.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);;
		return this;
	}

	public void depose() {
		applicationContext.publishEvent(new ContextClosedEvent(
				applicationContext));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, String name) {
		return (T) applicationContext.getBean(name, type);
	}

	public String[] getNames() {
		return applicationContext.getBeanDefinitionNames();
	}

	public boolean has(String name) {
		return applicationContext.containsBean(name);
	}

	public void reset() {
		applicationContext.publishEvent(new ContextRefreshedEvent(
				applicationContext));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> classZ) throws IocException {
		InjectName injectName = classZ.getAnnotation(InjectName.class);
		if (injectName != null && !Strings.isBlank(injectName.value()))
			return (T) applicationContext.getBean(injectName.value());
		return (T) applicationContext.getBean(applicationContext.getBeanNamesForType(classZ)[0]);
	}
}
