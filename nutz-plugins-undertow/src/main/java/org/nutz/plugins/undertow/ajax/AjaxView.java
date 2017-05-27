package org.nutz.plugins.undertow.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

/**
 * 将返回渲染成标准 AjaxReturn
 * 
 * 可以在配置文件中设置js的格式，适用开发与生产环境
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author pw
 */
public class AjaxView implements View {

	private boolean uc;

	public AjaxView() {
		uc = false;
	}

	public AjaxView(boolean uc) {
		this.uc = uc;
	}

	public AjaxView(String useCompact) {
		uc = Strings.isBlank(useCompact) ? false : Boolean.parseBoolean(useCompact);
	}

	public JsonFormat getJsonFormat() {
		if (uc) {
			// 紧凑模式(生产)
			return JsonFormat.compact();
		} else {
			// 一般模式(开发)
			return JsonFormat.nice();
		}
	}

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws IOException {
		AjaxReturn re;
		// 空
		if (null == obj) {
			re = Ajax.ok();
		}
		// 异常
		else if (obj instanceof Throwable) {
			re = Ajax.fail().setErrCode("500").setMsg("err").setData(((Throwable) obj).getMessage());
		}
		// AjaxReturn
		else if (obj instanceof AjaxReturn) {
			re = (AjaxReturn) obj;
		}
		// 数据对象
		else {
			re = Ajax.ok().setData(obj);
		}

		// 写入返回
		Mvcs.write(resp, re, getJsonFormat());
	}

}
