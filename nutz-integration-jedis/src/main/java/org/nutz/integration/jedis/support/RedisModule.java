package org.nutz.integration.jedis.support;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.io.InputStream;
import java.io.StringReader;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.JedisClusterWrapper;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Each;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisModule extends Endpoint {

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected JedisAgent jedisAgent;

    @At("/")
    @Ok("raw:html")
    public InputStream index() {
        InputStream ins = Mvcs.getServletContext().getResourceAsStream("/WEB-INF/pages/redis.html");
        if (ins == null)
            ins = getClass().getResourceAsStream("redis.html");
        return ins;
    }

    @At
    @Ok("json:full")
    @Aop("redis")
    public NutMap info() {
        final NutMap re = new NutMap();
        if (jedis() instanceof JedisClusterWrapper)
            re.put("db", 0);
        else
            re.put("db", jedis().getDB());
        StringReader sr = new StringReader(jedis().info());
        Streams.eachLine(sr, new Each<String>() {
            public void invoke(int index, String ele, int length) {
                if (Strings.isBlank(ele) || !ele.contains(":") || ele.startsWith("#"))
                    return;
                String[] tmp = ele.split(":", 2);
                if (tmp.length != 2)
                    return;
                re.put(tmp[0], tmp[1]);
            }
        });
        return ajaxOk(re);
    }

    @At
    @Aop("redis")
    @Ok("json:full")
    public NutMap scan(String pattern, String cursor, int count) {
        if (count < 1)
            count = 1000;
        else if (count > 10000)
            count = 10000;
        if (Strings.isBlank(cursor))
            cursor = "";
        ScanParams params = new ScanParams().count(count).match(pattern);
        ScanResult<String> re = jedis().scan(cursor, params);
        NutMap r = new NutMap();
        r.put("next", re.getStringCursor());
        r.put("keys", re.getResult());
        return ajaxOk(r);
    }
    
    @At
    @Aop("redis")
    @Ok("json:full")
    @AdaptBy(type=JsonAdaptor.class)
    public void exec(NutMap command) {
        
    }
    
    protected NutMap ajaxOk(Object data) {
        return new NutMap("ok", true).setv("data", data);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}
}
