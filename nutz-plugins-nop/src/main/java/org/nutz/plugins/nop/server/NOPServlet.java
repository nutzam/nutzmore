package org.nutz.plugins.nop.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPData;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOPServlet.java
 *
 * @description NOPServlet
 *
 * @time 2016年8月31日 下午3:53:13
 *
 */
public class NOPServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getHeader(NOPConfig.methodKey());
		if (Strings.isBlank(method)) {// 空方法
			response.getWriter().write(Json.toJson(NOPData.exception("null method")));
			return;
		}
		String info = Lang.readAll(request.getReader());
		request.setAttribute(NOPConfig.parasKey(), Lang.map(info));
		request.getRequestDispatcher(method).forward(request, response);// 将请求转发给真实的函数入口
	}

}
