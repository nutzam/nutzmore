package org.nutz.plugins.xmlbind.bean;

import org.nutz.plugins.xmlbind.annotation.XmlAttr;
import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle("trkpt")
public class GpxTrkpt {

    @XmlAttr
    public String lat;
    @XmlAttr
    public String lon;
    @XmlEle(simpleNode = true)
    public String ele;
    @XmlEle(simpleNode = true)
    public String time;
    
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public String getEle() {
        return ele;
    }
    public void setEle(String ele) {
        this.ele = ele;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    
    
}
