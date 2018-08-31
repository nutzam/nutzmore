package org.nutz.plugins.xmlbind.bean;

import org.nutz.plugins.xmlbind.annotation.XmlAttr;
import org.nutz.plugins.xmlbind.annotation.XmlEle;

@XmlEle("metadata")
public class GpxMetadata {

    @XmlEle(simpleNode=true)
    protected String name;
    @XmlEle
    protected GpxMetadataAuthor author;
    @XmlEle
    protected GpxMetadataBounds bounds;
    @XmlAttr
    protected String time;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public GpxMetadataAuthor getAuthor() {
        return author;
    }
    public void setAuthor(GpxMetadataAuthor author) {
        this.author = author;
    }
    public GpxMetadataBounds getBounds() {
        return bounds;
    }
    public void setBounds(GpxMetadataBounds bounds) {
        this.bounds = bounds;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
