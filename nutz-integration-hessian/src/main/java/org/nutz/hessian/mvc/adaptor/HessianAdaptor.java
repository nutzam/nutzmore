package org.nutz.hessian.mvc.adaptor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.PairAdaptor;

import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.ServiceContext;

/**
 * @author koukou890@qq.comØß
 * 
 *         demo: server<br />
 * @AdaptBy(type = HessianAdaptor.class, args = {
 *               "com.shanggame.module.SayHelloWorld",
 *               "com.shanggame.module.SayHelloWorldImpl" })
 * @At
 * @Ok("void") public void t() {
 * 
 *             }
 * 
 *             client<br />
 *             private HessianProxyFactory factory = new HessianProxyFactory();
 *             public static void main(String[] args) { HessianClient c = new
 *             HessianClient(); try { Object obj =
 *             c.factory.create(SayHelloWorld.class, "http://localhost:8080/t");
 *             System.out.println(((SayHelloWorld)obj).hi("abc")); } catch
 *             (MalformedURLException e) { log.error(e); } }
 *
 */
public class HessianAdaptor extends PairAdaptor {

	private HessianSkeleton _homeSkeleton;

	private SerializerFactory _serializerFactory = new SerializerFactory();

	public HessianAdaptor(String _homeAPI, String _homeImpl) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Lang.loadClass(_homeAPI);
		Object obj = Lang.loadClass(_homeImpl).newInstance();
		if (clazz.isAssignableFrom(Lang.loadClass(_homeImpl))) {
			this._homeSkeleton = new HessianSkeleton(obj, clazz);
		}else{
			throw Lang.makeThrow(IllegalAccessException.class, "baseService must be BaseTreeableService subclass");
		}
	}

	public HessianAdaptor(Class<?> _homeAPIClass, Object _homeImpl) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (_homeAPIClass.isAssignableFrom(_homeImpl.getClass())) {
			this._homeSkeleton = new HessianSkeleton(_homeImpl, _homeAPIClass);
		}else{
			throw Lang.makeThrow(IllegalAccessException.class, "baseService must be BaseTreeableService subclass");
		}
	}

	public HessianAdaptor(HessianSkeleton _homeSkeleton) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this._homeSkeleton = _homeSkeleton;
	}

	public HessianAdaptor(Object _homeImpl) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<Class<?>> list = getAllInterfaces(_homeImpl.getClass());
		if (Lang.isEmpty(list)) {
			throw Lang.makeThrow("Class[%s] not have any Interface", _homeImpl.getClass());
		}
		int len = list.size();
		if (len != 1) {
			throw Lang.makeThrow("Class[%s] must have only one Interface", _homeImpl.getClass());
		}
		Class<?> _homeAPIClass = list.get(0);
		this._homeSkeleton = new HessianSkeleton(_homeImpl, _homeAPIClass);
	}

	protected void invoke(InputStream is, OutputStream os) throws Exception {
		_homeSkeleton.invoke(is, os, _serializerFactory);
	}

	@Override
	public Object[] getReferObject(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, String[] pathArgs) {
		if (!req.getMethod().equals("POST")) {
			resp.setStatus(500); // , "Hessian Requires POST");
			String str = "Not POST , Wrong HTTP method! --> " + req.getMethod();
			throw Lang.makeThrow(IllegalArgumentException.class, str);
		}
		String serviceId = req.getPathInfo();
		String objectId = req.getParameter("id");
		if (objectId == null)
			objectId = req.getParameter("ejbid");
		try {
			ServiceContext.begin(req, resp, serviceId, objectId);
			InputStream is = req.getInputStream();
			OutputStream os = resp.getOutputStream();
			resp.setContentType("x-application/hessian");
			invoke(is, os);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
		} finally {
			ServiceContext.end();
		}
		return null;
	}

	private List<Class<?>> getAllInterfaces(Class<?> cls) {
		if (cls == null) {
			return null;
		}
		LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
		getAllInterfaces(cls, interfacesFound);
		return new ArrayList<Class<?>>(interfacesFound);
	}

	private void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
		while (cls != null) {
			Class<?>[] interfaces = cls.getInterfaces();
			for (Class<?> i : interfaces) {
				if (interfacesFound.add(i)) {
					getAllInterfaces(i, interfacesFound);
				}
			}
			cls = cls.getSuperclass();
		}
	}
}
