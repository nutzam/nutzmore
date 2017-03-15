package org.nutz.plugins.ngrok.server.auth;

import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.server.NgrokServer;
import org.nutz.plugins.ngrok.server.NgrokServer.NgrokServerClient;

public class DefaultNgrokAuthProvider implements NgrokAuthProvider {

    public boolean check(NgrokServer srv, NgrokMsg auth) {
        return true;
    }

    public String[] mapping(NgrokServer srv, NgrokServerClient client, NgrokMsg req) {
        return new String[]{client.id.substring(0, 6) + "." + srv.srv_host};
    }

    public void record(String host, long in, long out) {}

}
