package org.nutz.plugins.xmlbind.bean;

import org.nutz.plugins.xmlbind.annotation.XmlAttr;
import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle("wpt")
public class GpxWpt {

    @XmlAttr
    protected String lat;
    @XmlAttr
    protected String lon;
    @XmlEle(simpleNode = true)
    protected String ele;
    @XmlEle(simpleNode=true)
    protected String name;
    @XmlEle(simpleNode=true)
    protected String type;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
