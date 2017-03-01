package org.nutz.plugins.ngrok.common;

import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

@SuppressWarnings("serial")
public class NgrokMsg extends NutMap {

    /**
     * // When a client opens a new control channel to the server // it must
     * start by sending an Auth message. type Auth struct { Version string //
     * protocol version MmVersion string // major/minor software version
     * (informational only) User string Password string OS string Arch string
     * ClientId string // empty for new sessions } //----------------
     * {"Type":"Auth","Payload":{"Version":"2","MmVersion":"1.7","User":"XXX","Password":"","OS":"windows","Arch":"386","ClientId":""}}
     */
    public static NgrokMsg auth(String user,
                                String password,
                                String os,
                                String arch,
                                String clientId) {
        NgrokMsg msg = new NgrokMsg();
        msg.setv("Type", "Auth").setv("Version", "2").setv("MmVersion", "1.7");
        msg.setv("User", user).setv("Password", password).setv("OS", os).setv("Arch", arch);
        msg.setv("ClientId", Strings.sBlank(clientId));
        return msg;
    }

    /**
     * type ReqTunnel struct { ReqId string Protocol string
     * 
     * // http only Hostname string Subdomain string HttpAuth string
     * 
     * // tcp only RemotePort uint16 }
     */
    public static NgrokMsg reqTunnel(String reqId,
                                         String hostname,
                                         String protocol,
                                         String subdomain,
                                         String httpAuth,
                                         int remotePort) {
        NgrokMsg msg = new NgrokMsg();
        msg.put("Type", "ReqTunnel");
        msg.setv("ReqId", reqId)
           .setv("Protocol", protocol)
           .setv("Hostname", hostname)
           .setv("Subdomain", subdomain);
        msg.setv("HttpAuth", Strings.sBlank(httpAuth)).setv("RemotePort", remotePort);
        return msg;
    }

    /**
     * // After a client receives a ReqProxy message, it opens a new //
     * connection to the server and sends a RegProxy message. type RegProxy
     * struct { ClientId string }
     */
    public static NgrokMsg regProxy(String clientId) {
        NgrokMsg msg = new NgrokMsg();
        msg.put("Type", "RegProxy");
        msg.setv("ClientId", clientId);
        return msg;
    }

    public static NgrokMsg ping() {
        NgrokMsg msg = new NgrokMsg();
        msg.put("Type", "Ping");
        return msg;
    }

    public static NgrokMsg pong() {
        NgrokMsg msg = new NgrokMsg();
        msg.put("Type", "Pong");
        return msg;
    }
}
