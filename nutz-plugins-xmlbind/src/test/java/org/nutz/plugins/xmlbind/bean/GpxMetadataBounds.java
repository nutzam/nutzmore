package org.nutz.plugins.xmlbind.bean;

import org.nutz.plugins.xmlbind.annotation.XmlAttr;
import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle("bounds")
public class GpxMetadataBounds {

    @XmlAttr
    public String minlat;
    @XmlAttr
    public String minlon;
    @XmlAttr
    public String maxlat;
    @XmlAttr
    public String maxlon;
    public String getMinlat() {
        return minlat;
    }
    public void setMinlat(String minlat) {
        this.minlat = minlat;
    }
    public String getMinlon() {
        return minlon;
    }
    public void setMinlon(String minlon) {
        this.minlon = minlon;
    }
    public String getMaxlat() {
        return maxlat;
    }
    public void setMaxlat(String maxlat) {
        this.maxlat = maxlat;
    }
    public String getMaxlon() {
        return maxlon;
    }
    public void setMaxlon(String maxlon) {
        this.maxlon = maxlon;
    }

}
