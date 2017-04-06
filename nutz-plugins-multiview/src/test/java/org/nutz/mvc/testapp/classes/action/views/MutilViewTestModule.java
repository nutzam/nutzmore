package org.nutz.mvc.testapp.classes.action.views;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@InjectName
@IocBean
@At("/mutil/view/")
public class MutilViewTestModule {
	/*@At("/jsp")
	@Ok("jsp:index")
	public void jspView() {
	}*/
	/*@At("/jsp")
    @Ok("jsp:jsp.views.jspView")
    public void jspView(){
    }
    
    @At("/jsp2")
    @Ok("jsp:jsp/views/jspView")
    public void jspView2(){
    }
    
    @At("/jsp3")
    @Ok("jsp:/WEB-INF/jsp/views/jspView")
    public void jspView3(){
    }
    
    @At("/jsp4")
    @Ok("jsp:/WEB-INF/jsp/views/jspView.jsp")
    public void jspView4(){
    }*/
	@At("/beetl")
	@Ok("btl:index")
	public void beetlView() {
	}
	
	@At("/freemarker")
	@Ok("ftl:index")
	public void freemarkerView() {
	}
	
	@At("/jetx")
	@Ok("jetx:index")
	public void jetxView() {
	}
}
