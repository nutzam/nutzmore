package org.nutz.plugins.undertow;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.util.CmdParams;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 封装WEB启动参数配置
 * @author qinerg@gmail.com
 * @varsion 2017-6-22
 */
public class WebConfig {

	private static Log log = Logs.get();

	/** "web.ip" -  绑定请求的IP，默认 0.0.0.0  */
	private String ip = "0.0.0.0";
	/** "web.port" - 应用监听的端口，默认 8080 */
	private int port = 8080;
	/** "web.path" - 应用ContextPath，默认 / */
	private String contextPath = "/";
	/** "web.root" - 应用的资源文件路径，默认 src/main/webapp/ */
	private String root = "src/main/webapp/";
	/** "web.modules" - 应用的主模块，默认 org.nutz.plugins.undertow.welcome.DefaultModule , 用于演示. 请替换 */
	private String mainModules = "org.nutz.plugins.undertow.welcome.DefaultModule";
	/** "web.session" - session缓存时长，默认 20 */
	private int session = 20;
	/** "web.runmode" - 应用的运行模式，默认 dev */
	private String runmode = "dev";
	/** "web.thread.io" - 应用 io 线程数 */
	private Integer threadIo;
	/** "web.thread.worker" - 应用工作线程数 */
	private Integer threadWorker;

	/**
	 * 从指定的配置文件参数初始化
	 * @param configName
	 */
	public static WebConfig NewByProperties(String configName) {
		NutMap m = new NutMap();
		try {
			m.putAll(new PropertiesProxy(configName));
		} catch (Exception e) {
			log.warnf("undertow plugins config file [%s] not find! use default config.", configName);
		}
		return NewByMap(m);
	}

	/**
	 * 通过命令行参数初始化
	 * @param args
	 */
	public static WebConfig NewByArgs(String[] args) {
		if (args != null && args.length > 0 && args[0].startsWith("-")) {
			CmdParams pp = CmdParams.parse(args, "debug");
			// 指定了配置文件名，优先加载配置文件中参数
			if (pp.has("config")) {
				return NewByProperties(pp.get("config"));
			} else {
				return NewByMap(pp.map());
			}
		} else {
			return NewByProperties("web.properties");
		}
	}

	/**
	 * 通过 map 参数初始化
	 * @param map
	 */
	public static WebConfig NewByMap(NutMap map) {
		WebConfig conf = new WebConfig();
		if (map.containsKey("web.ip"))
			conf.ip = map.getString("web.ip");
		if (map.containsKey("web.port"))
			conf.port = map.getInt("web.port");
		if (map.containsKey("web.path"))
			conf.contextPath = map.getString("web.path");
		if (map.containsKey("web.root"))
			conf.root = map.getString("web.root");
		if (map.containsKey("web.modules"))
			conf.mainModules = map.getString("web.modules");
		if (map.containsKey("web.session"))
			conf.session = map.getInt("web.session");
		if (map.containsKey("web.runmode"))
			conf.runmode = map.getString("web.runmode");

		// 线程数配置
		if (map.containsKey("web.thread.io"))
			conf.threadIo = map.getInt("web.thread.io");
		if (map.containsKey("web.thread.worker"))
			conf.threadWorker = map.getInt("web.thread.worker");

		return conf;
	}

	public String getIp() {
		return ip;
	}

	public WebConfig setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public int getPort() {
		return port;
	}

	public WebConfig setPort(int port) {
		this.port = port;
		return this;
	}

	public String getContextPath() {
		return contextPath;
	}

	public WebConfig setContextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	public String getRoot() {
		return root;
	}

	public WebConfig setRoot(String root) {
		this.root = root;
		return this;
	}

	public String getMainModules() {
		return mainModules;
	}

	public WebConfig setMainModules(String mainModules) {
		this.mainModules = mainModules;
		return this;
	}

	public int getSession() {
		return session;
	}

	public WebConfig setSession(int session) {
		this.session = session;
		return this;
	}

	public String getRunmode() {
		return runmode;
	}

	public WebConfig setRunmode(String runmode) {
		this.runmode = runmode;
		return this;
	}

	public Integer getThreadIo() {
		return threadIo;
	}

	public WebConfig setThreadIo(Integer threadIo) {
		this.threadIo = threadIo;
		return this;
	}

	public Integer getThreadWorker() {
		return threadWorker;
	}

	public WebConfig setThreadWorker(Integer threadWorker) {
		this.threadWorker = threadWorker;
		return this;
	}

}
