package org.nutz.plugins.ioc.loader;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
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
	private static final ThreadIocLoader lock_get = new ThreadIocLoader();
	public static Map<String, Ioc2> iocs = new LinkedHashMap<String, Ioc2>();// 顺序很重要 首先从mainIoc中查找
	static {
		config = new PropertiesProxy(PROPERTIES_NAME);
	}
	// 是否在线程环境下与  强制使用MvcIoc
	public static ThreadLocal<Boolean> isUsedMvcIoc = new ThreadLocal<Boolean>();
	/**
	 * 持有Ioc容器,避免被GC, 及完成测试后需要关闭ioc容器
	 */
	private static Ioc2 mainIoc;
	private static Ioc mvcIoc;

	public static ThreadIocLoader getIoc() {
		if (mainIoc == null) {
			try {
				synchronized (lock_get) {
					if (mainIoc == null) {
						mainIoc = new NutIoc(getIocLoader()); // 生成Ioc容器
						log.info("<<<--- get Not Mvc Ioc --->>>");
						if((isUsedMvcIoc.get() != null && isUsedMvcIoc.get().booleanValue())) {
							// NutFilter作用域内,通常是请求线程内
							mvcIoc =  Mvcs.getIoc();// ctx().iocs.get(getName());
							if (mvcIoc == null) {
								// 独立线程, 例如计划任务,定时任务的线程.
								mvcIoc =  Mvcs.ctx().getDefaultIoc();// iocs.values().iterator().next();
							}
							log.info("<<<--- get Mvc Ioc --->>>");
						}
						iocs.put("ioc", mainIoc);
					}
				}
				init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lock_get;
	}

	/**
	 * 用户自定义初始化过程, 在ioc容器初始化完成后及本对象的属性注入完成后执行
	 */
	private static void init() throws Exception {
		// 添加各种组合IocLoader
		String iocComboLoader = config.get(IOC_COMBO_LOADER);
		if (Strings.isNotBlank(iocComboLoader)) {
			String[] iocComboLoaders = Strings.splitIgnoreBlank(iocComboLoader, SEPARATOR_CHAR);
			for (String icl : iocComboLoaders) {
				Class<?> clazz = Class.forName(icl);
				@SuppressWarnings("deprecation")
				IocLoader loader = (IocLoader) clazz.newInstance();
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					Inject inject = field.getAnnotation(Inject.class);
					if (inject == null)
						continue;
					String val = inject.value();
					Object v = null;
					if (Strings.isBlank(val)) {
						v = mainIoc.get(field.getType(), field.getName());
					} else {
						if (val.startsWith("refer:"))
							val = val.substring("refer:".length());
						v = mainIoc.get(field.getType(), val);
					}
					field.setAccessible(true);
					field.set(loader, v);
				}
				iocs.put(icl, new NutIoc(new ComboIocLoader(loader)));

			}
		}

		// 添加执行链
		if (iocSetupCopy == null) {
			final IocSetupBuilder b = IocSetupBuilder.create();
			String iocSetupFirstStr = config.get(SETUP_FIRST);// 从comboIocLoader配置文件中加载复合配置
			if (Strings.isNotBlank(iocSetupFirstStr)) {
				String[] iocSetupFirsts = Strings.splitIgnoreBlank(iocSetupFirstStr, SEPARATOR_CHAR);
				for (String isf : iocSetupFirsts) {
					IocSetup is = (IocSetup) mainIoc.get(Class.forName(isf));
					b.addFirst(is);
				}
			}

			String iocSetupLastStr = config.get(SETUP_LAST);// 从comboIocLoader配置文件中加载复合配置
			if (Strings.isNotBlank(iocSetupLastStr)) {
				String[] iocSetupLasts = Strings.splitIgnoreBlank(iocSetupLastStr, SEPARATOR_CHAR);
				for (String isl : iocSetupLasts) {
					IocSetup is = (IocSetup) mainIoc.get(Class.forName(isl));
					b.addLast(is);
				}
			}
			iocSetupCopy = b.build();
		}
		iocSetupCopy.init(iocs);

		// 是为了不在同一包下，做的动作，进行重新加载
		List<String> loaderClasses = config.getList(LOADER_CLASSES, SEPARATOR_CHAR);
		for (String loaderClass : loaderClasses) {
			mainIoc.get(Class.forName(loaderClass));
		}
	}

	/**
	 * 用户自定义销毁过程, 在ioc容器销毁前执行
	 */
	private static void _depose() throws Exception {
		iocSetupCopy.destroy(iocs);
		mainIoc = null;
	}

	/**
	 * 获取IocLoader,默认是ComboIocLoader实例, 子类可以自定义
	 */
	private static IocLoader getIocLoader() throws Exception {
		return new ComboIocLoader(getIocConfigure());
	}

	/**
	 * 子类可覆盖本方法,以配置项目的MainModule,可选项
	 */
	private static Class<?> getMainModule() throws Exception {
		String mainModule = config.get(MAIN_MODULE);
		if (Strings.isNotBlank(mainModule)) {
			return Class.forName(config.get(MAIN_MODULE));
		}
		return null;
	}

	/**
	 * 子类可覆盖本方法,以配置项目的ioc配置,可选项
	 */
	private static String[] getIocConfigure() throws Exception {
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

	public void depose() throws Exception {
		try {
			_depose();
		} finally {
			Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Ioc2> entry = it.next();
				entry.getValue().depose();
			}
		}

	}

	public <T> T get(Class<T> type, String name) throws IocException {
		IocException ex = null;
		Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Ioc2> entry = it.next();
			try {
				return entry.getValue().get(type, name);
			} catch (IocException e) {// 这里异常不做处理，等实在找不着时，再抛出异常
				ex = e;
			}
		}
		throw ex;
	}

	public <T> T get(Class<T> type) throws IocException {
		IocException ex = null;
		Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Ioc2> entry = it.next();
			try {
				return entry.getValue().get(type);
			} catch (IocException e) {// 这里异常不做处理，等实在找不着时，再抛出异常
				ex = e;
			}
		}
		try {
			if(mvcIoc!=null) {
				return mvcIoc.get(type);
			}
		} catch (Exception e) {
			
		}
		throw ex;
	}

	public boolean has(String name) throws IocException {
		IocException ex = null;
		Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Ioc2> entry = it.next();
			try {
				boolean bl = entry.getValue().has(name);
				if (bl) {
					return true;
				}
			} catch (IocException e) {// 这里异常不做处理，等实在找不着时，再抛出异常
				ex = e;
			}
		}
		if (ex != null) {
			if(mvcIoc!=null) {
				return mvcIoc.has(name);
			}
			throw ex;
		} else {
			return false;
		}
	}

	public String[] getNames() {
		List<String[]> nameList = new LinkedList<String[]>();
		int arrLen = 0;
		Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Ioc2> entry = it.next();
			String[] names = entry.getValue().getNames();
			nameList.add(names);
			arrLen += names.length;
		}
		int destPos = 0;
		String[] nameArr = new String[arrLen];
		for (String[] names : nameList) {
			destPos += names.length;
			System.arraycopy(names, 0, nameArr, destPos, names.length);
		}
		return nameArr;
	}

	public void reset() {
		Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Ioc2> entry = it.next();
			entry.getValue().reset();
		}
	}

	public String[] getNamesByType(Class<?> klass) {
		Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Ioc2> entry = it.next();
			String[] t = entry.getValue().getNamesByType(klass);
			if (t != null) {
				return t;
			}
		}
		if(mvcIoc!=null) {
			return mvcIoc.getNamesByType(klass);
		}
		return null;
	}

	public <K> K getByType(Class<K> klass) {
		Iterator<Entry<String, Ioc2>> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Ioc2> entry = it.next();
			K k = entry.getValue().getByType(klass);
			if (k != null) {
				return k;
			}
		}
		if(mvcIoc!=null) {
			return mvcIoc.getByType(klass);
		}
		return null;
	}
}
