package org.nutz.hessian.mvc.adaptor;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.PairAdaptor;

import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.ServiceContext;

public class HessianAdaptor extends PairAdaptor {

	private HessianSkeleton _homeSkeleton;

	private SerializerFactory _serializerFactory = new SerializerFactory();

	public HessianAdaptor(String _homeAPI, String _homeImpl) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Lang.loadClass(_homeAPI);
		Object obj = Lang.loadClass(_homeImpl).newInstance();
		_homeSkeleton = new HessianSkeleton(obj, clazz);
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
}
