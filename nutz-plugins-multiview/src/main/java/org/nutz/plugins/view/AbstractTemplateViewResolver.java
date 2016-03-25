package org.nutz.plugins.view;

import javax.servlet.ServletContext;

/**
 * 视图模板类，其他模板视图需继承此抽象类
 * @author denghuafeng(it@denghuafeng.com)
 *
 */
public abstract class AbstractTemplateViewResolver extends AbstractUrlBasedView {
	
	protected boolean isInited;
	
	protected abstract void init(String appRoot,ServletContext sc);

	public boolean isInited() {
		return isInited;
	}

	public void setInited(boolean isInited) {
		this.isInited = isInited;
	}
	
}
