package org.nutz.plugins.ngrok.server.auth;

import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.server.AbstractNgrokServer;

public interface NgrokAuthProvider {

    boolean check(AbstractNgrokServer srv, NgrokMsg auth);
    
    String[] mapping(AbstractNgrokServer srv, String id, NgrokMsg authMsg, NgrokMsg req);
    
    void record(String host, long in, long out);
}
