package org.nutz.plugins.ngrok.server;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.nutz.plugins.ngrok.common.NgrokMsg;

public class NgrokServerClient {

    public String id;
    public Socket ctlSocket;
    public Set<Socket> proxySockets = new HashSet<Socket>();
    public NgrokMsg authMsg;
}
