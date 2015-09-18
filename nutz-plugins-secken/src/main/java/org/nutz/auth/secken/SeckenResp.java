package org.nutz.auth.secken;

import org.nutz.lang.util.NutMap;

@SuppressWarnings("serial")
public class SeckenResp extends NutMap {

    public int status() {
        return getInt("status", -1);
    }
    
    public String description() {
        return getString("description");
    }
    
    public String event_id() {
        return getString("event_id");
    }
    
    public String uid() {
        return getString("uid");
    }
    
    public String signature() {
        return getString("signature");
    }
    
    public String qrcode_url() {
        return getString("qrcode_url");
    }
    
    public String qrcode_data() {
        return getString("qrcode_data");
    }
    
    // -----------------------
    
    public boolean ok() {
        return status() == 200;
    }
    
    public SeckenResp check() {
        if (!ok())
            throw new RuntimeException("bad status");
        return this;
    }
}
