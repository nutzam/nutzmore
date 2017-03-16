package org.apache.zookeeper;

import java.io.IOException;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;

public class ZooKeeperFactory {
    
    protected PropertiesProxy conf;
    
    protected Ioc ioc;

    public ZooKeeper get() throws IOException {
        String connectString = conf.get("zookeeper.connectString");
        int sessionTimeout = conf.getInt("zookeeper.sessionTimeout");
        String _watcher = conf.get("zookeeper.watcher");
        Watcher watcher = null;
        if (!Strings.isBlank(_watcher)) {
            watcher = ioc.get(Watcher.class, _watcher);
        }
        long sessionId = conf.getLong("zookeeper.sessionId", 0);
        byte[] sessionPasswd = conf.has("zookeeper.sessionPasswd") ? conf.get("zookeeper.sessionPasswd").getBytes() : null;
        boolean canBeReadOnly = conf.getBoolean("zookeeper.canBeReadOnly", true);
        return new ZooKeeper(connectString, sessionTimeout, watcher, sessionId, sessionPasswd, canBeReadOnly);
    }
}
