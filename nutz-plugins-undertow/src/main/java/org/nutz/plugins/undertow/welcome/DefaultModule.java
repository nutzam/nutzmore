package org.nutz.plugins.undertow.welcome;

import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Times;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

/**
 * 缺省的web主模块，用于示例
 * 正式使用应替换为自定义的主模块 
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-25
 */
@Modules(scanPackage = false)
@IocBy(type = ComboIocProvider.class, args = { "*anno", "com" })
public class DefaultModule {

	@At({ "/", "/index" })
	@Ok("void")
	public void index(HttpServletResponse resp) throws IOException {
		Segment cs = readTmpl("index.html.tmpl", Lang.map("time", Times.sDTms(new Date())));
		resp.getWriter().write(cs.toString());
	}

	// 使用nutz内置的代码片段工具来变通实现的视图模板
	private Segment readTmpl(String tnm, NutMap param) {
		String tmpl = Streams.readAndClose(Streams.utf8r(DefaultModule.class.getResourceAsStream(tnm)));
		CharSegment cs = new CharSegment(tmpl);
		for (Entry<String, Object> entry : param.entrySet()) {
			cs.set(entry.getKey(), entry.getValue());
		}
		return cs;
	}

}
