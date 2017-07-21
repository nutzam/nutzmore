package org.nutz.plugins.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
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

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutFilter;

/**
 * 默认的undertow服务启动器
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-24
 */
public class WebLauncher {

	private static Log log = Logs.get();

	/**
	 * 缺省的启动器
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		start(args);
	}

	/**
	* 执行启动的主函数，接受-config命令行参数指定 web服务器的配置文件路径。如果没有这个参数，默认在 classpath 下寻找 "web.properties" 文件。
	* <p>
	* 这个文件遵循 Nutz 多行属性文件规范，可设定如下的键值:
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
		start(args, getDefaultBuilder(), getDefaultServletBuilder());
	}

	public static void start(String[] args, Builder builder) {
		start(args, builder, getDefaultServletBuilder());
	}

	public static void start(String[] args, Builder builder, DeploymentInfo servletBuilder) {
		WebConfig conf = WebConfig.NewByArgs(args);
		start(conf, builder, servletBuilder);
	}

	/**
	 * 直接指定运行参数方式启动
	 * @param conf
	 */
	public static void start(WebConfig conf) {
		start(conf, getDefaultBuilder(), getDefaultServletBuilder());
	}

	public static void start(WebConfig conf, Builder builder) {
		start(conf, builder, getDefaultServletBuilder());
	}

	public static void start(WebConfig conf, Builder builder, DeploymentInfo servletBuilder) {
		String contextPath = conf.getContextPath();

		servletBuilder.setContextPath(contextPath).setDefaultSessionTimeout(conf.getSession() * 60).setDeploymentName("nutz-web");
		addDefaultFilter(servletBuilder, conf);
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
		builder.addHttpListener(conf.getPort(), conf.getIp()).setHandler(pathHandler);

		final Undertow server = builder.build();
		server.start();

		log.infof("*** WebServer start at %s:%d! ***", conf.getIp(), conf.getPort());
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.stop();
			}
		});
	}

	/**
	 * 缺省的Builder
	 * 用户可以通过它调整线程数、buffer、socket参数等，进行性能优化
	 * @return
	 */
	public static Builder getDefaultBuilder() {
		return Undertow.builder();
	}

	/**
	 * 缺省的DeploymentInfo，可加载自定义的 Filter  Servlets
	 * @param servletBuilder
	 */
	public static DeploymentInfo getDefaultServletBuilder() {
		return Servlets.deployment().setClassLoader(DeploymentInfo.class.getClassLoader()).setEagerFilterInit(true);
	}

	/**
	 * 增加缺省的 filter
	 * 
	 * <li>NutFilter.class</li>
	 * <li>ResourceManager</li>
	 * 
	 * @param servletBuilder
	 */
	protected static void addDefaultFilter(DeploymentInfo servletBuilder, WebConfig conf) {
		FilterInfo nutzFilter = new FilterInfo("mvc", NutFilter.class);
		nutzFilter.addInitParam("modules", conf.getMainModules());

		servletBuilder.addFilter(nutzFilter).addFilterUrlMapping("mvc", "/*", DispatcherType.REQUEST).addFilterUrlMapping("mvc", "/*", DispatcherType.FORWARD);
		File resRootDir = Files.findFile(conf.getRoot());
		if (resRootDir != null && resRootDir.isDirectory()) {
			servletBuilder.setResourceManager(new FileResourceManager(resRootDir, 100));
		} else {
			servletBuilder.setResourceManager(new ClassPathResourceManager(DeploymentInfo.class.getClassLoader(), "web/"));
		}
	}
}
