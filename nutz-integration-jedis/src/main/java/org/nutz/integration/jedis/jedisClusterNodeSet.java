package org.nutz.integration.jedis;

import java.util.HashSet;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

import redis.clients.jedis.HostAndPort;

public class jedisClusterNodeSet extends HashSet<HostAndPort> {

    private static final long serialVersionUID = 6028293027128774439L;
    
    protected PropertiesProxy conf;
    
    public void init() {
        String nodes = conf.get("redis.nodes");
        if (!Strings.isBlank(nodes)) {
            String[] tmp = Strings.splitIgnoreBlank(nodes);
            for (String node : tmp) {
                HostAndPort hp;
                if (node.contains(":")) {
                    String[] tmp2 = node.split("[\\:]");
                    hp = new HostAndPort(tmp2[0], Integer.parseInt(tmp2[1]));
                } else {
                    hp = new HostAndPort(node, conf.getInt("redis.port", 6937));
                }
                this.add(hp);
            }
        } else {
            this.add(new HostAndPort(conf.get("redis.host", "127.0.0.1"), conf.getInt("redis.port", 6937)));
        }
    }
}
