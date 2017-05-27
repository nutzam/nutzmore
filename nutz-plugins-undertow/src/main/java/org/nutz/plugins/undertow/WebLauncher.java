package org.nutz.plugins.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;

import java.io.File;

import javax.servlet.DispatcherType;

import org.jboss.logging.Logger.Level;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.CmdParams;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutFilter;

/**
 * 默认的undertow服务的启动器
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-24
 */
public class WebLauncher {

	private static Log log = Logs.get();
	// 设置启动的参数
	private static NutMap config = new NutMap();
	
	/**
	* 执行启动的主函数，接受-config命令行参数，为 web 服务器的配置文件路径。如果没有这个参数，默认在 classpath 下寻找 "web.properties" 文件。
	* <p>
	* 这个文件遵循 Nutz 多行属性文件规范，同时必须具备如下的键:
	* <ul>
	* <li>"web.ip" - 绑定请求的IP，默认 0.0.0.0  
	* <li>"web.port" - 应用监听的端口，默认 8080
	* <li>"web.path" - 应用ContextPath，默认 /
	* <li>"web.root" - 应用的资源文件路径，默认 src/main/webapp/
	* <li>"web.modules" - 应用的主模块，默认 org.nutz.plugins.undertow.welcome.DefaultModule , 用于演示. 请替换
	* <li> "web.session" - session缓存时长，默认 20
	* <li> "web.runmode" - 应用的运行模式，默认 dev
	* <li> "web.thread.io" - 应用 io 线程数
	* <li> "web.thread.worker" - 应用工作线程数
	* </ul>
	* 
	* @param args
	*            接收命令行参数，可替代properties设置 
	 * @throws Exception 
	*/
	public static void start(String[] args) {
		if (args != null && args.length > 0 && args[0].startsWith("-")) {
			configByArgs(args);
		} else {
			configByProperties("web.properties");
		}

		UndertowLogger.ROOT_LOGGER.isEnabled(Level.DEBUG);

		String contextPath = config.getString("web.path");

		DeploymentInfo servletBuilder = Servlets.deployment().setClassLoader(DeploymentInfo.class.getClassLoader()).setContextPath(contextPath)
				.setDefaultSessionTimeout(config.getInt("web.session")).setDeploymentName("nutz-web");
		addCustomFilterServlets(servletBuilder); // 加载自定义的 filter 及 servlet
		addDefaultFilter(servletBuilder);
		servletBuilder.addWelcomePages("index.html", "index.htm", "index.do");

		DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
		manager.deploy();

		HttpHandler servletHandler;
		try {
			servletHandler = manager.start();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		PathHandler pathHandler;
		if ("/".equals(contextPath)) {
			pathHandler = Handlers.path(servletHandler);
		} else {
			pathHandler = Handlers.path(Handlers.redirect(contextPath)).addPrefixPath(contextPath, servletHandler);
		}

		Builder builder = Undertow.builder().addHttpListener(config.getInt("web.port"), config.getString("web.ip")).setHandler(pathHandler);
		optimizeParam(builder); // 自定义优化

		final Undertow server = builder.build();
		server.start();

		log.infof("*** WebServer start at %s:%d! ***", config.getString("web.ip"), config.getInt("web.port"));
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.stop();
			}
		});
	}

	/**
	 * 缺省的启动器
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		start(args);
	}

	/**
	 * 子类可覆盖本方法，加载自定义的 Filter  Servlets
	 * @param servletBuilder
	 */
	protected static void addCustomFilterServlets(DeploymentInfo servletBuilder) {

	}

	/**
	 * 子类可覆盖此方法，进行线程数、网络参数等更精细化优化
	 * @param builder
	 */
	protected static void optimizeParam(Builder builder) {

	}

	/**
	 * 增加缺省的 filter
	 * 
	 * <li>NutFilter.class</li>
	 * <li>ResourceManager</li>
	 * 
	 * @param servletBuilder
	 */
	protected static void addDefaultFilter(DeploymentInfo servletBuilder) {
		FilterInfo nutzFilter = new FilterInfo("mvc", NutFilter.class);
		nutzFilter.addInitParam("modules", config.getString("web.modules"));

		servletBuilder.addFilter(nutzFilter).addFilterUrlMapping("mvc", "/*", DispatcherType.REQUEST).addFilterUrlMapping("mvc", "/*", DispatcherType.FORWARD);
		File resRootDir = Files.findFile(config.getString("web.root"));
		if (resRootDir != null && resRootDir.isDirectory()) {
			servletBuilder.setResourceManager(new FileResourceManager(resRootDir, 100));
		} else {
			servletBuilder.setResourceManager(new ClassPathResourceManager(DeploymentInfo.class.getClassLoader(), "web/"));
		}
	}

	// 通过命令行参数初始化
	private static void configByArgs(String[] args) {
		CmdParams pp = CmdParams.parse(args, "debug");
		// 指定了配置文件名，优先加载配置文件中参数
		if (pp.has("config")) {
			configByProperties(pp.get("config"));
		} else {
			initCommonParam(pp.map());
		}
	}

	// 通过 web.properties 文件初始化
	private static void configByProperties(String configFile) {
		if (Files.findFile(configFile) != null) {
			NutMap m = new NutMap();
			m.putAll(new PropertiesProxy(configFile));
			initCommonParam(m);
		} else {
			log.warnf("web config file[%s] not find, use default config.", configFile);
			initCommonParam(NutMap.NEW());
		}
	}

	private static void initCommonParam(NutMap map) {
		config.put("web.ip", map.get("web.ip", "0.0.0.0"));
		config.put("web.port", map.getInt("web.port", 8080));
		config.put("web.path", map.get("web.path", "/"));
		config.put("web.root", map.get("web.root", "src/main/webapp/"));
		config.put("web.modules", map.get("web.modules", "org.nutz.plugins.undertow.welcome.DefaultModule"));
		config.put("web.session", map.getInt("web.session", 20));
		config.put("web.runmode", map.get("web.runmode", "dev"));

		// 线程数配置
		if (map.containsKey("web.thread.io"))
			config.put("web.thread.io", map.getInt("web.thread.io"));
		if (map.containsKey("web.thread.worker"))
			config.put("web.thread.worker", map.getInt("web.thread.worker"));
	}
}
