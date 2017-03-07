package org.nutz.plugins.hotplug;

import java.io.ByteArrayInputStream;

import org.nutz.lang.Lang;

public class HotplugAsset {

    public byte[] buf;
    public String etag;
    
    public HotplugAsset() {}
    
    public HotplugAsset(byte[] buf) {
        this.buf = buf;
        this.etag = Lang.digest("SHA1", new ByteArrayInputStream(buf)).substring(12);
    }
}
