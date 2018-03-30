package org.nutz.plugins.nop.server;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
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
@WebServlet(name = "nop", urlPatterns = { "/nop.endpoint" }, initParams = { @WebInitParam(name = "config", value = "nop.properties") })
public class NOPServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String configPath;

	private String digestName = "MD5";

	private String fetcherName = "default";

	Log log = Logs.get();

	/**
	 * 默认5秒超时,如果实现了对时,那么5秒足以,如果不对时请酌情调整此时间
	 */
	private int timeout = 5;

	@Override
	public void init() throws ServletException {
		super.init();
		configPath = getServletConfig().getInitParameter("config");
		if (configPath == null) {
			configPath = "nop.properties";
		}
		PropertiesProxy proxy = new PropertiesProxy(configPath);
		timeout = proxy.getInt("timeout", timeout);
	}

	public Date addSeconds(Date base, long seconds) {
		return Times.D(base.getTime() + seconds * 1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1. 时间戳校验 2.签名校验转移到nutz的filter中去处理,这样便于获取ioc中的对象 3.方法校验
		 * 
		 */
		Enumeration<String> headerNames = request.getHeaderNames();
		Lang.each(headerNames, new Each<String>() {

			@Override
			public void invoke(int index, String key, int length) throws ExitLoop, ContinueLoop, LoopException {
				log.debugf("header name : %s and header value is : %s", key, request.getHeader(key)); // 先全部打出来
			}
		});
		try {
			String method = request.getHeader(NOPConfig.methodKey);
			if (Strings.isBlank(method)) {// 空方法
				response.getWriter().write(Json.toJson(NOPData.exception("null method")));
				return;
			}
			String timeStemp = request.getHeader(NOPConfig.tsKey);
			if (Strings.isBlank(timeStemp)) {
				response.getWriter().write(Json.toJson(NOPData.exception("no timeStemp")));
				return;
			}
			long time = Long.parseLong(timeStemp);
			if (addSeconds(Times.D(time), timeout).before(Times.now())) {
				response.getWriter().write(Json.toJson(NOPData.exception("request timeout")));
				return;
			}
			// 转发之前把签名相关的信息带过去
			request.setAttribute("digestName", digestName);
			request.setAttribute("fetcherName", fetcherName);
			request.getRequestDispatcher(method).forward(request, response);// 将请求转发给真实的函数入口
		} catch (Exception e) {
			response.getWriter().write(Json.toJson(NOPData.exception(e)));
		}
	}

}
