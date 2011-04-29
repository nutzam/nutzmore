package org.nutz.validation;

import static org.junit.Assert.assertEquals;

import javax.servlet.Servlet;

import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockHttpServletResponse;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.NutServlet;

/**
 * Mvc 中使用 AOP 对入口参数进行验证，并将验证结果返回给 Error 参数中
 * 
 * @author QinerG(QinerG@gmail.com)
 */
public class MvcValidationTest {

	protected Servlet servlet;

	protected MockHttpServletRequest request;

	protected MockHttpServletResponse response;

	protected MockHttpSession session;

	protected MockServletContext servletContext;

	protected MockServletConfig servletConfig;

	@Before
	public void init() throws Throwable {
		servletContext = new MockServletContext();
		servletConfig = new MockServletConfig(servletContext, "nutz");
		servletConfig.addInitParameter("modules",
				"org.nutz.validation.meta.Mvc");

		servlet = new NutServlet();
		servlet.init(servletConfig);

		session = Mock.servlet.session(servletContext);
		request = Mock.servlet.request().setSession(session);
		request.setContextPath("");
		request.setSession(session);
		response = new MockHttpServletResponse();
	}

	private int doreq(String path, String json) throws Exception {
		request.setPathInfo(path);
		request.setInputStream(Mock.servlet.ins(new StringInputStream(json,
				"UTF-8")));
		servlet.service(request, response);
		return response.getAsInt();
	}

	@Test
	public void test_a() throws Exception {
		String path = "/test";
		String json = "{bean : {name:'韦小宝', address:'长安街'} }";
		assertEquals(8, doreq(path, json));
	}

	@Test
	public void test_b() throws Exception {
		String path = "/test";
		String json = "{bean : {name:'韦小宝', address:'长安街', age:18} }";
		assertEquals(7, doreq(path, json));
	}
	
	@Test
	public void test_c() throws Exception {
		String path = "/test";
		String json = "{bean : {name:'韦小宝', address:'长安街', age:18, mobile:'13699999999'} }";
		assertEquals(6, doreq(path, json));
	}

}
