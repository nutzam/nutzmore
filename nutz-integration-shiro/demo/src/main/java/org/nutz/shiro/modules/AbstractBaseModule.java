package org.nutz.shiro.modules;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.view.ForwardView;
import org.nutz.mvc.view.JspView;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.mvc.view.UTF8JsonView;

/**
 * 
 * 提供controller的数据封装统标准操作
 * 
 * @author Kerbores <br>
 *         每个模块只需要继承此模块，配置@At和@Fail即可
 * 
 * @Fail 建议直接放到统一的处理view进行处理 @Fail("jsp:jsp.exception.exception")
 */
@Encoding(input = "UTF-8", output = "UTF-8")
@IocBean
public class AbstractBaseModule {
	protected static final Log log = Logs.get();

	public AbstractBaseModule() {
	}

	/**
	 * 添加cookie信息
	 * 
	 * @param name
	 *            键
	 * @param value
	 *            值
	 * @param age
	 *            存活时间(单位 秒)
	 */
	protected void _addCookie(String name, String value, int age) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(age);
		Mvcs.getResp().addCookie(cookie);
	}

	public String _base() {
		return Mvcs.getReq().getContextPath();
	}

	/**
	 * 修正页码
	 * 
	 * @param page
	 *            参数页码
	 * @return
	 */
	public int _fixPage(int page) {
		return page <= 0 ? 1 : page;
	}

	/**
	 * 修正检索key
	 * 
	 * @param key
	 * @return
	 */
	public String _fixSearchKey(String key) {
		HttpServletRequest request = Mvcs.getReq();
		if (Strings.equalsIgnoreCase("get", request.getMethod()) && Lang.isWin()) {
			key = Strings.isBlank(key) ? "" : key;
			try {
				return new String(key.getBytes("iso-8859-1"), request.getCharacterEncoding());
			} catch (UnsupportedEncodingException e) {
				log.debug(e);
				return "";
			}
		}
		return Strings.isBlank(key) ? "" : key;
	}

	/**
	 * 获取指定的 cookie
	 * 
	 * @param name
	 *            cookie 名
	 * @return cookie 值,如果没有返回 null
	 */
	protected String _getCookie(String name) {
		Cookie[] cookies = Mvcs.getActionContext().getRequest().getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Strings.equals(cookie.getName(), name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 指定命名空间为页面一级导航高亮提供支持 hack
	 * 
	 * @return nameSpace
	 */
	public String _getNameSpace() {
		return null;
	};

	public String _ip() {
		return Lang.getIP(Mvcs.getReq());
	}

	/**
	 * 添加session信息
	 * 
	 * @param key
	 *            session key
	 * @param value
	 *            value
	 */
	protected void _putSession(String key, Object value) {
		Mvcs.getReq().getSession().setAttribute(key, value);
	}

	/**
	 * 转发
	 * 
	 * @param path
	 * @param objs
	 *            数据默认绑在 objs上
	 * @return
	 */
	public View _renderForward(String path, Object... objs) {
		Mvcs.getActionContext().getRequest().setAttribute("objs", objs);
		return new ForwardView(path);
	}

	/**
	 * json形式响应
	 * 
	 * @param objs
	 *            数据
	 * @return
	 */
	public View _renderJson(Object... objs) {
		UTF8JsonView view = (UTF8JsonView) UTF8JsonView.NICE;
		view.setData(objs);
		return view;
	}

	/**
	 * 转发至jsp
	 * 
	 * @param path
	 *            路径
	 * @param objs
	 *            数据
	 * @return
	 */
	public View _renderJsp(String path, Object... objs) {
		Mvcs.getActionContext().getRequest().setAttribute("objs", objs);// 数据绑定
		return new JspView(path);
	}

	/**
	 * 重定向
	 * 
	 * @param path
	 * @return
	 */
	public View _renderRedirct(String path) {
		return new ServerRedirectView(path);
	}

}