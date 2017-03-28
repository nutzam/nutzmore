package org.nutz.integration.jedis;

import java.util.HashSet;

import org.nutz.lang.Strings;

import redis.clients.jedis.HostAndPort;

public class JedisClusterNodeSet extends HashSet<HostAndPort> {

    private static final long serialVersionUID = 6028293027128774439L;

    private String nodes;
    private int port = 6379;
    private String host = "localhost";

    public void init() {
        if (!Strings.isBlank(nodes)) {
            String[] tmp = Strings.splitIgnoreBlank(nodes);
            for (String node : tmp) {
                HostAndPort hp;
                if (node.contains(":")) {
                    String[] tmp2 = node.split("[\\:]");
                    hp = new HostAndPort(tmp2[0], Integer.parseInt(tmp2[1]));
                } else {
                    hp = new HostAndPort(node, port);
                }
                this.add(hp);
            }
        } else {
            this.add(new HostAndPort(host, port));
        }
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
