package org.nutz.plugins.ioc.loader;

import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.plugins.ioc.loader.chain.IocSetup;
import org.nutz.plugins.ioc.loader.chain.IocSetupBuilder;

/**
 * 定时任务、线程环境下ioc加载
 * 
 * @author 邓华锋 http://dhf.ink
 *
 */
public final class ThreadIocLoader {
	/**
	 * 持有Ioc容器,避免被GC, 及完成测试后需要关闭ioc容器
	 */
	private static Ioc ioc;
	private static final Log log = Logs.get();
	private static final String MAIN_MODULE = "ioc.main.module";
	private static final String IOC_BY = "ioc.by";
	private static final String LOADER_CLASSES = "ioc.loader.classes";
	private static final String SETUP_FIRST = "ioc.setup.first";
	private static final String SETUP_LAST = "ioc.setup.last";
	private static final String IOC_COMBO_LOADER = "ioc.combo.loader";
	private static final String PROPERTIES_NAME = "comboIocLoader.properties";
	private static final String SEPARATOR_CHAR = ",";
	private static PropertiesProxy config;
	private static IocSetup iocSetupCopy;
	public static ComboIocLoader comboIocLoader;

	public static Ioc getMvcFilterIoc() {
		if (ioc == null) {
			// NutFilter作用域内,通常是请求线程内
			ioc = Mvcs.getIoc();
			log.info("<<<--- get Mvc Ioc --->>>");
			getNotMvcIoc();
		}
		return ioc;
	}

	public static Ioc getIoc() {
		if (ioc == null) {
			// 独立线程, 例如计划任务,定时任务的线程.
			ioc = Mvcs.ctx().getDefaultIoc();
			log.info("<<<--- get Mvc Ioc --->>>");
			getNotMvcIoc();
		}
		return ioc;
	}

	private static Ioc getNotMvcIoc() {
		synchronized (log) {
			if (ioc == null) {
				config = new PropertiesProxy(PROPERTIES_NAME);
				try {
					init();
					// 是为了不在同一包下，做的动作，进行重新加载
					List<String> loaderClasses = config.getList(LOADER_CLASSES, SEPARATOR_CHAR);
					for (String loaderClass : loaderClasses) {
						ioc.get(Class.forName(loaderClass));
					}
					log.info("<<<--- get Not Mvc Ioc --->>>");
				} catch (Exception e) {
					log.error("ioc实例失败", e);
				}

			}
		}
		return ioc;
	}

	/**
	 * 初始化Ioc容器
	 * 
	 * @throws Exception
	 *             初始化过程出错的话抛错
	 */
	public static void init() throws Exception {
		ioc = new NutIoc(getIocLoader()); // 生成Ioc容器
		_init(); // 执行用户自定义初始化过程
	}

	/**
	 * 用户自定义初始化过程, 在ioc容器初始化完成后及本对象的属性注入完成后执行
	 */
	public static void _init() throws Exception {
		// 添加各种组合IocLoader
		String iocComboLoader = config.get(IOC_COMBO_LOADER);
		if (Strings.isNotBlank(iocComboLoader)) {
			String[] iocComboLoaders = Strings.splitIgnoreBlank(iocComboLoader, SEPARATOR_CHAR);
			for (String icl : iocComboLoaders) {
				IocLoader loader = (IocLoader) Class.forName(icl).newInstance();
				comboIocLoader.addLoader(loader);
			}
		}

		// 添加执行链
		if (iocSetupCopy == null) {
			final IocSetupBuilder b = IocSetupBuilder.create();
			String iocSetupFirstStr = config.get(SETUP_FIRST);// 从comboIocLoader配置文件中加载复合配置
			if (Strings.isNotBlank(iocSetupFirstStr)) {
				String[] iocSetupFirsts = Strings.splitIgnoreBlank(iocSetupFirstStr, SEPARATOR_CHAR);
				for (String isf : iocSetupFirsts) {
					IocSetup is = (IocSetup) ioc.get(Class.forName(isf));
					b.addFirst(is);
				}
			}

			String iocSetupLastStr = config.get(SETUP_LAST);// 从comboIocLoader配置文件中加载复合配置
			if (Strings.isNotBlank(iocSetupLastStr)) {
				String[] iocSetupLasts = Strings.splitIgnoreBlank(iocSetupLastStr, SEPARATOR_CHAR);
				for (String isl : iocSetupLasts) {
					IocSetup is = (IocSetup) ioc.get(Class.forName(isl));
					b.addLast(is);
				}
			}
			iocSetupCopy = b.build();
		}
		iocSetupCopy.init(ioc);
	}

	/**
	 * 用户自定义销毁过程, 在ioc容器销毁前执行
	 */
	public static void _depose() throws Exception {
		iocSetupCopy.destroy(ioc);
	}

	/**
	 * 获取IocLoader,默认是ComboIocLoader实例, 子类可以自定义
	 */
	public static IocLoader getIocLoader() throws Exception {
		comboIocLoader = new ComboIocLoader(getIocConfigure());
		return comboIocLoader;
	}

	/**
	 * 子类可覆盖本方法,以配置项目的MainModule,可选项
	 */
	public static Class<?> getMainModule() throws Exception {
		String mainModule = config.get(MAIN_MODULE);
		if (Strings.isNotBlank(mainModule)) {
			return Class.forName(config.get(MAIN_MODULE));
		}
		return null;
	}

	/**
	 * 子类可覆盖本方法,以配置项目的ioc配置,可选项
	 */
	public static String[] getIocConfigure() throws Exception {
		Class<?> klass = getMainModule();
		String iocByStr = config.get(IOC_BY);// 从comboIocLoader配置文件中加载复合配置
		String[] iocBys = null;
		if (Strings.isNotBlank(iocByStr)) {
			iocBys = Strings.splitIgnoreBlank(iocByStr, SEPARATOR_CHAR);
		}
		if (klass == null)
			return iocBys;
		IocBy iocBy = klass.getAnnotation(IocBy.class);
		if (iocBy == null)
			return iocBys;
		return iocBy.args();
	}

	public static void depose() throws Exception {
		try {
			_depose();
		} finally {
			if (ioc != null)
				ioc.depose();
		}
	}
}
