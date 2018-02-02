package org.nutz.mock;

import javax.servlet.Servlet;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockHttpServletResponse;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.NutServlet;

@Ignore
public abstract class AbstractMvcTest extends NutIocTestBase {

	protected Servlet servlet;

	protected MockHttpServletRequest request;

	protected MockHttpServletResponse response;

	protected MockHttpSession session;

	protected MockServletContext servletContext;

	protected MockServletConfig servletConfig;

	protected NutConfig nc;

	@Override
	@Before
	public void before() throws Exception {
		servletContext = Mock.servlet.context();
		servletConfig = new MockServletConfig(servletContext, "nutz");
		initServletConfig();
		servlet = new NutServlet();
		servlet.init(servletConfig);
		session = Mock.servlet.session(servletContext);
		nc = Mvcs.getNutConfig();
		ioc = nc.getIoc();
		injectSelfFields();
		newreq();
		_before();
	}

	protected void newreq() {
		request = Mock.servlet.request().setSession(session);
		request.setContextPath("");
		request.setSession(session);
		response = new MockHttpServletResponse();
	}

	protected abstract void initServletConfig();

	@Override
	@After
	public void after() throws Exception {
		_after();
		if (servlet != null)
			servlet.destroy();
	}

}
