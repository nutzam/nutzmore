package org.nutz.hessian.mvc.adaptor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.hessian.mvc.adaptor.HessianAdaptor;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.ServiceContext;
import com.shanggame.service.HessianClient;
import com.shanggame.service.SayHelloWorld;

/**
 * @author koukou890@qq.com
 * 
 * demo:
 * server<br /> 
 * @AdaptBy(type = HessianAdaptor.class, args = { "com.shanggame.module.SayHelloWorld", "com.shanggame.module.SayHelloWorldImpl" })
	@At
	@Ok("void")
	public void t() {

	}
 * 
 * client<br /> 
 * public static void main(String[] args) {
		HessianClient c = new HessianClient();
		try {
			Object obj = c.factory.create(SayHelloWorld.class, "http://localhost:8080/t");
			System.out.println(((SayHelloWorld)obj).hi("abc"));
		} catch (MalformedURLException e) {
			log.error(e);
		}
	}
 *
 */
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
