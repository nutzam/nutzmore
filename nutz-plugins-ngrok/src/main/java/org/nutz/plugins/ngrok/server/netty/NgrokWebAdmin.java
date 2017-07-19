package org.nutz.plugins.ngrok.server.netty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SetupBy;
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
public class NgrokWebAdmin implements Setup, ActionFilter {

    private static final Log log = Logs.get();

    @Inject
    protected NgrokNettyServer server;
    
    // ---------------------------------------------------
    //                   WebAdmin相关的代码
    // ---------------------------------------------------
    
    
    @Ok("json:full")
    @At("/client/list")
    public Object clientList() {
        List<NutMap> clients = new ArrayList<NutMap>();
        for (NgrokContrlHandler handler : server.clientHanlders.values()) {
            try {
                clients.add(handler.asMap());
            } catch (Throwable e) {
                log.debug("fail at NgrokContrlHandler.asMap", e);
            }
        }
        Collections.sort(clients, new Comparator<NutMap>() {
            public int compare(NutMap prev, NutMap next) {
                return prev.getString("id").compareTo(next.getString("id"));
            }
        });
        return clients;
    }
    
    public void init(NutConfig nc) {
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
    }
    
    public void destroy(NutConfig nc) {
    }

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
