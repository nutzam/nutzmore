package org.nutz.plugins.ngrok.server.auth;

import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.server.AbstractNgrokServer;

public class DefaultNgrokAuthProvider implements NgrokAuthProvider {

    public boolean check(AbstractNgrokServer srv, NgrokMsg auth) {
        return true;
    }

    public String[] mapping(AbstractNgrokServer srv, String id, NgrokMsg authMsg, NgrokMsg req) {
        return new String[]{id.substring(0, 6) + "." + srv.srv_host};
    }

    public void record(String host, long in, long out) {}

}
