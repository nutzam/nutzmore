package org.nutz.plugins.ngrok.server.netty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.plugins.ngrok.server.netty.NgrokNettyServer.NgrokContrlHandler;
import org.nutz.repo.Base64;

@Modules
@IocBy(args={"*anno", "org.nutz.plugins.ngrok.server.netty"})
@Ok("json")
@Fail("http:500")
@SetupBy(value=Setup.class, args="ioc:ngrokWebAdmin")
@Filters(@By(type=ActionFilter.class, args="ioc:ngrokWebAdmin"))
@IocBean
@Views(BeetlViewMaker.class)
public class NgrokWebAdmin implements Setup, ActionFilter {

    private static final Log log = Logs.get();

    @Inject
    protected NgrokNettyServer server;
    
    // ---------------------------------------------------
    //                   WebAdmin相关的代码
    // ---------------------------------------------------
    
    
    @Ok("json:full")
    @At("/ngrokd/client/query")
    public Object query(@Param("..")Pager pager) {
        List<NutMap> clients = new ArrayList<NutMap>();
        List<String> ids = new ArrayList<String>(server.clientHanlders.keySet());
        Collections.sort(ids);
        if (pager.getPageNumber() < 1)
            pager.setPageNumber(1);
        if (pager.getPageSize() < 1)
            pager.setPageNumber(20);
        pager.setRecordCount(server.clientHanlders.size());
        int start = pager.getOffset();
        int end = start + pager.getPageSize();
        if (start >= ids.size()) {
            return new NutMap("ok", true).setv("data", new QueryResult(clients, pager));
        }
        if (end >= ids.size()) {
            end = ids.size();
        }
        for (String id : ids.subList(start, end)) {
            try {
                clients.add(server.clientHanlders.get(id).asMap());
            } catch (Throwable e) {
                log.debug("fail at NgrokContrlHandler.asMap", e);
            }
        }
        return new NutMap("ok", true).setv("data", new QueryResult(clients, pager));
    }
    
    @Ok("json:full")
    @At("/ngrokd/client/kill")
    public Object kill(String id) {
        NgrokContrlHandler handler = server.clientHanlders.get(id);
        if (handler == null) {
            return new NutMap("ok", false).setv("msg", "该客户端已经消失?");
        }
        handler.shutdown(true);;
        return new NutMap("ok", true);
    }
    
    //--------------------------------------------------
    //                  AdminLTE 界面中转
    //--------------------------------------------------
    @At("/adminlte/page/?/?")
    @Ok("beetl:/adminlte/${pathargs[0]}/${pathargs[1]}.html")
    public void page() {}
    
    @Ok("beetl:/adminlte/index.html")
    @At({"/", "/index"})
    public void index(HttpServletRequest req){
        req.setAttribute("profile", new NutMap());
    }
    
    @At("/admin/hotplug/list")
    public Object hotplugList() {
        List<NutMap> list = new ArrayList<NutMap>();
        NutMap map = new NutMap();
        map.put("name", "ngrokd");
        map.put("tmpls", new NutMap());
        map.put("menu", Json.fromJson("[{name:'Ngrok服务',subs:[{name:'在线客户端管理',page:'ngrokd/client.html'}, {name:'token管理', page:'ngrokd/token.html'}]}]"));
        list.add(map);
        return new NutMap("ok", true).setv("data", new NutMap("list", list));
    }
    
    //--------------------------------------------------
    //                  初始化和销毁
    //--------------------------------------------------
    
    public void init(NutConfig nc) {
        ((Ioc2)nc.getIoc()).getIocContext().save("app", "conf", new ObjectProxy(new PropertiesProxy()));
        new Thread("ngrok.server") {
            public void run() {
                try {
                    server.start();
                }
                catch (Throwable e) {
                    log.debug("something happen", e);
                }
            }
        }.start();
        for (ViewMaker vm : nc.getViewMakers()) {
            if (vm instanceof BeetlViewMaker) {
                BeetlViewMaker bvm = (BeetlViewMaker)vm;
                GroupTemplate gt = bvm.groupTemplate;
                gt.setResourceLoader(new FileResourceLoader(server.webadim_root + "/templates/"));
                gt.setSharedVars(new NutMap());
                gt.getSharedVars().put("conf", nc.getIoc().get(null, "conf"));
            }
        }
    }
    
    public void destroy(NutConfig nc) {
    }
    
    //--------------------------------------------------
    //                  用户授权,用Http基本授权吧,暂时
    //--------------------------------------------------

    public View match(ActionContext ac) {
        String auth = ac.getRequest().getHeader("Authorization");
        if (Strings.isBlank(auth) || !auth.startsWith("Basic ")) {
            ac.getResponse().setHeader("WWW-Authenticate", "Basic realm=\"Nutz Ngrok Web Admin\"");
            return new HttpStatusView(401);
        }
        auth = new String(Base64.decode(auth.substring("Basic ".length())));
        String[] tmp = auth.split(":", 2);
        if (!server.webadmin_password.equals(tmp[1])) {
            ac.getResponse().setHeader("WWW-Authenticate", "Basic realm=\"Nutz Ngrok Web Admin\"");
            return new HttpStatusView(401);
        }
        return null;
    }
}
