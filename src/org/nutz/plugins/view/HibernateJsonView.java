package org.nutz.plugins.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.proxy.HibernateProxy;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.view.UTF8JsonView;

/**
 * 支持Hibernate LazyLoad的bean, 但只支持单层,不递归
 * @author wendal
 *
 */
public class HibernateJsonView extends UTF8JsonView {

	
	public HibernateJsonView(JsonFormat format) {
		super(format);
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws IOException {
		if (obj != null && obj instanceof HibernateProxy) {
			obj = ((HibernateProxy)obj).getHibernateLazyInitializer().getImplementation();
		}
		super.render(req, resp, obj);
	}
}
