package org.nutz.integration.dubbo;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Strings;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class DubboConfigIocLoader extends DubboIocLoader {

	protected Ioc ioc;
	protected PropertiesProxy conf;

	public DubboConfigIocLoader(Ioc ioc, PropertiesProxy conf) {
		this.ioc = ioc;
		this.conf = conf;
		init();
	}

	protected void init() {
		// 添加最基础的配置项
		conf.putIfAbsent("dubbo.application.name", conf.get("nutz.application.name", "demo"));
		conf.putIfAbsent("dubbo.registry.address", "multicast://224.5.6.7:1234");
		conf.putIfAbsent("dubbo.protocol.name", "dubbo");
		conf.putIfAbsent("dubbo.protocol.port", "20880");
		// 传递给Dubbo配置类
		ConfigUtils.addProperties(conf.toProperties());

		iobjs.put("dubbo_iobjs", Iocs.wrap(iobjs));
		wrap("dubboManager", new DubboManager());

		prepareApplicationConfig();
		prepareProtocolConfig();
		prepareRegistryConfig();
		prepareAnnotationBean();
	}

	protected void prepareApplicationConfig() {
		ApplicationConfig applicationConfig = conf.make(ApplicationConfig.class, "dubbo.application.");
		wrap("dubboApplicationConfig", applicationConfig);
	}

	protected void prepareRegistryConfig() {
		RegistryConfig registryConfig = conf.make(RegistryConfig.class, "dubbo.registry.");
		wrap("dubboRegistryConfig", registryConfig);
	}

	protected void prepareProtocolConfig() {
		ProtocolConfig protocolConfig = conf.make(ProtocolConfig.class, "dubbo.protocol.");
		wrap("dubboPrototeConfig", protocolConfig);
	}

	protected void prepareAnnotationBean() {
		String propName = "dubbo.scan.basePackages";
		String beanName = "dubboAnnotationBean";
		AnnotationBean annos = new AnnotationBean();
		annos.setPackages(Strings.splitIgnoreBlank(conf.get(propName)));
		wrap(beanName, annos);
	}

	protected void wrap(String beanName, Object iocBean) {
		IocObject iobj = Iocs.wrap(iocBean);
		DubboAgent.checkIocObject(beanName, iobj);
		iobjs.put(beanName, iobj);
	}
}
