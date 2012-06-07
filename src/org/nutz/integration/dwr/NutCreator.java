package org.nutz.integration.dwr;

import java.util.Map;

import org.directwebremoting.create.AbstractCreator;
import org.directwebremoting.util.LocalUtil;
import org.directwebremoting.util.Messages;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;

@SuppressWarnings("rawtypes")
public class NutCreator extends AbstractCreator {
	
	private Class clazz;
	private String beanName;
	private Ioc ioc;

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
		Ioc ioc = this.ioc;
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
	
	@Override
	public void setProperties(Map params) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		super.setProperties(params);
	}
}
