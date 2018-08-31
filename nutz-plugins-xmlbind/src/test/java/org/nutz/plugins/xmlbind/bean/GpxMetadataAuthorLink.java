package org.nutz.plugins.xmlbind.bean;

import org.nutz.plugins.xmlbind.annotation.XmlAttr;
import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle("link")
public class GpxMetadataAuthorLink {

    @XmlAttr
    public String href;
    @XmlEle(simpleNode = true)
    protected String name;
    @XmlEle(simpleNode = true)
    protected String type;
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
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
