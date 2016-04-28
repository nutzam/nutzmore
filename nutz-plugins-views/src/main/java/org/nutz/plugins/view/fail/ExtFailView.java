package org.nutz.plugins.view.fail;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Strings;
import org.nutz.mvc.View;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-views
 *
 * @file ExtFailView.java
 *
 * @description 错误视图代理
 *
 * @time 2016年3月8日 上午10:51:26
 *
 */
public class ExtFailView implements View {

	private View normalFailView;

	private View ajaxFailView;

	/**
	 * @param normalFailView
	 * @param ajaxFailView
	 */
	public ExtFailView(View normalFailView, View ajaxFailView) {
		super();
		this.normalFailView = normalFailView;
		this.ajaxFailView = ajaxFailView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.mvc.View#render(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object obj) throws Throwable {

		// 看看是不是ajax请求
		String requestType = request.getHeader("X-Requested-With");
		if (Strings.isBlank(requestType)) {
			normalFailView.render(request, response, obj);
		} else {
			ajaxFailView.render(request, response, obj);
		}

	}

}
