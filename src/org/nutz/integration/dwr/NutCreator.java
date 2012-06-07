package org.nutz.integration.dwr;

import org.directwebremoting.create.AbstractCreator;
import org.directwebremoting.util.LocalUtil;
import org.directwebremoting.util.Messages;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * 使用NutIoc来作为DWR的Creator<p/>
 * 用法,在dwr.xml中添加:<p/>
 * {@code<init><creator id="nutz" class="org.nutz.integration.dwr.NutCreator"/></init>}
 * <p/>
 * 在需要使用NutIoc的Bean的地方:</p>
 * {@code
 * <create creator="nutz" javascript="Demo">
 *     <param name="class" value="net.wendal.nutz.dwr.DwrMe" />
 *     <param name="beanName" value="dwrMe" />
 * </create>
 * }
 * @author wendal
 *
 */
@SuppressWarnings("rawtypes")
public class NutCreator extends AbstractCreator {

	private Class clazz;
	private String beanName;
	private static Ioc overrideIoc;

	public Class getType() {
		if (clazz != null)
			return clazz;
		try {
			return getInstance().getClass();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Object getInstance() throws InstantiationException {
		Ioc ioc = NutCreator.overrideIoc;
		if (ioc == null)
			ioc = Mvcs.getIoc();
		if (beanName != null)
			return ioc.get(clazz, beanName);
		return ioc.get(clazz);
	}

	public void setClass(String classname) {
		try {
			this.clazz = LocalUtil.classForName(classname);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException(Messages.getString( "Creator.ClassNotFound", classname));
		}
	}
	
	/**
	 * 直接设置Ioc实例
	 */
	public static void setOverrideIoc(Ioc overrideIoc) {
		NutCreator.overrideIoc = overrideIoc;
	}
	
	/**
	 * 传入初始化参数
	 * @see org.nutz.mvc.annotation.IocBy
	 */
	public static void init(String ...confs) {
		NutCreator.overrideIoc = new ComboIocProvider().create(null, confs);
	}
}
