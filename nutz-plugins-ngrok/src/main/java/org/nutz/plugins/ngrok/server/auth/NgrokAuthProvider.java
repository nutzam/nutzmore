package org.nutz.plugins.ngrok.server.auth;

import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.server.NgrokServer;

public interface NgrokAuthProvider {

    boolean check(NgrokServer srv, NgrokMsg auth);
    
    String[] mapping(NgrokServer srv, NgrokServer.NgrokServerClient client, NgrokMsg req);
}
