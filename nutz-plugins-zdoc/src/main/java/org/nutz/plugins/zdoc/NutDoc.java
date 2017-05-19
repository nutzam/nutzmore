package org.nutz.plugins.zdoc;

import java.util.Date;

import org.nutz.lang.Times;
import org.nutz.lang.util.Tag;

public class NutDoc extends NutD {

    /**
     * 文档的原始字符串内容
     */
    private String primerContent;

    /**
     * 文档内容的根节点(解析过的)
     */
    private Tag rootTag;

    public NutDoc(String name) {
        super(name);
    }

    @Override
    public boolean isDoc() {
        return true;
    }

    @Override
    public boolean isSet() {
        return false;
    }

    public String getPrimerContent() {
        return primerContent;
    }

    public void setPrimerContent(String primerContent) {
        this.primerContent = primerContent;
    }

    public Tag getRootTag() {
        return rootTag;
    }

    public void setRootTag(Tag root) {
        this.rootTag = root;
    }

    public Tag setRootTagIfNull(String tagName) {
        if (null == this.rootTag)
            this.rootTag = Tag.tag(tagName);
        return this.rootTag;
    }

    public boolean hasDateTime() {
        return meta.has("date");
    }

    public void setDateTime(long ams) {
        Date d = Times.D(ams);
        String ds = Times.sD(d);
        meta.put("date", ds);
    }

    public String getDateTime() {
        return meta.getString("date");
    }
}
