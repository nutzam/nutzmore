package org.nutz.plugins.xmlbind.bean;

import java.util.List;

import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle("gax")
public class GpxFile {

    @XmlEle
    public GpxMetadata metadata;

    @XmlEle
    public GpxRte rte;

    @XmlEle("rtept")
    public List<GpxRtept> rtepts;

    @XmlEle("wpt")
    public List<GpxWpt> wpts;

    @XmlEle("trk")
    public GpxTrk trk;

    public GpxMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(GpxMetadata metadata) {
        this.metadata = metadata;
    }

    public GpxRte getRte() {
        return rte;
    }

    public void setRte(GpxRte rte) {
        this.rte = rte;
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

    public GpxTrk getTrk() {
        return trk;
    }

    public void setTrk(GpxTrk trk) {
        this.trk = trk;
    }
}
