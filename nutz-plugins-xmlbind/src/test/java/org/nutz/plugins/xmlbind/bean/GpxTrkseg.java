package org.nutz.plugins.xmlbind.bean;

import java.util.List;

import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle("trkseg")
public class GpxTrkseg {

    @XmlEle("trkpt")
    public List<GpxTrkpt> trkpts;

    public List<GpxTrkpt> getTrkpts() {
        return trkpts;
    }

    public void setTrkpts(List<GpxTrkpt> trkpts) {
        this.trkpts = trkpts;
    }
    
    
}
