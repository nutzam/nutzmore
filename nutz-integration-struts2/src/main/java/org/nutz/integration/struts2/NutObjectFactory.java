package org.nutz.integration.struts2;

import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutMvcListener;

import com.opensymphony.xwork2.ObjectFactory;

/**
 * 需要2个配置, 首先在struts.properties加入
 * struts.objectFactory=org.nutz.integration.struts2.NutObjectFactory
 * <p/>
 * 第二步有2种选择, 推荐在web.xml声明org.nutz.mvc.NutMvcListener, 或者直接
 * struts.objectFactory.iocArgs=*js,ioc/,*anno,net.wendal.nutzbook
 * 
 * @author wendal(wendal1985@gmail.com)
 * @see https://struts.apache.org/docs/objectfactory.html
 */
public class NutObjectFactory extends ObjectFactory {

	private static final long serialVersionUID = 8509262184575823946L;

	public NutObjectFactory() {
	}

	protected Ioc ioc;

	protected boolean selfIoc;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
		if (clazz.getAnnotation(IocBean.class) != null) {
			if (ioc == null) {
				ioc = Mvcs.getIoc();
				if (ioc == null)
					ioc = Mvcs.ctx().getDefaultIoc();
				if (ioc == null)
					ioc = NutMvcListener.ioc();
			}
			return ioc.get(clazz);
		}
		return super.buildBean(clazz, extraContext);
	}

	public void setIoc(Ioc ioc) {
		this.ioc = ioc;
	}

	public void setIocArgs(String args) throws ClassNotFoundException {
		this.ioc = new NutIoc(new ComboIocLoader(args.trim().split(",")));
		selfIoc = true;
	}

	@Override
	public boolean isNoArgConstructorRequired() {
		return false;
	}

}
