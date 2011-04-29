package org.nutz.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

/**
 * 使用 Smarty4j 模板生成页面
 * 
 * @author QinerG(QinerG@gmail.com)
 */
public class SmartyView extends AbstractPathView implements View {

	private final String ext = ".html";
	private static Engine engine = new Engine();// 加载模板引擎

	public SmartyView(String dest) {
		super(dest);
		engine.setTemplatePath("");
	}

	/*
	 * 渲染页面
	 * 
	 * @see org.nutz.mvc.View#render(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Throwable {
		if ("".equals(engine.getTemplatePath())) {
			String realPath = req.getSession().getServletContext().getRealPath("/");
			engine.setTemplatePath(realPath);
		}

		String path = evalPath(req, obj);

		// 空路径，采用默认规则
		if (Strings.isBlank(path)) {
			path = Mvcs.getRequestPath(req);
			path = "WEB-INF" + (path.startsWith("/") ? "" : "/") + Files.renameSuffix(path, ext);
		}
		// 绝对路径 : 以 '/' 开头的路径不增加 '/WEB-INF'
		else if (path.charAt(0) == '/') {
			if (!path.toLowerCase().endsWith(ext))
				path += ext;
		}
		// 包名形式的路径
		else {
			path = "WEB-INF/" + path.replace('.', '/') + ext;
		}

		Template template = engine.getTemplate(path);
		
		Context ctx = new Context(); // 生成数据容器对象
		ctx.set("obj", obj);
		ctx.set("request", req);
		ctx.set("base", req.getAttribute("base"));
		ctx.set("session", req.getSession());

		template.merge(ctx, resp.getWriter());
	}
}