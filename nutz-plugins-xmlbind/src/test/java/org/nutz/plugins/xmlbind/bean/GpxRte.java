package org.nutz.plugins.xmlbind.bean;

import java.util.List;

import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle
public class GpxRte {

    @XmlEle
    public GpxRteExtension extensions;

    @XmlEle("rtept")
    public List<GpxRtept> rtepts;

    @XmlEle("wpt")
    public List<GpxWpt> wpts;

    public GpxRteExtension getExtensions() {
        return extensions;
    }

    public void setExtensions(GpxRteExtension extensions) {
        this.extensions = extensions;
    }

    public List<GpxRtept> getRtepts() {
        return rtepts;
    }

    public void setRtepts(List<GpxRtept> rtepts) {
        this.rtepts = rtepts;
    }

    public List<GpxWpt> getWpts() {
        return wpts;
    }

    public void setWpts(List<GpxWpt> wpts) {
        this.wpts = wpts;
    }
    
}
