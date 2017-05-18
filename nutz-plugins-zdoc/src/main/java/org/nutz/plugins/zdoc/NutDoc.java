package org.nutz.plugins.zdoc;

import org.nutz.lang.util.Tag;

public class NutDoc extends NutD {

    /**
     * 文档的原始字符串内容
     */
    private String primerContent;

    /**
     * 文档内容的根节点(解析过的)
     */
    private Tag root;

    public NutDoc(String path) {
        super(path);
    }

    public String getPrimerContent() {
        return primerContent;
    }

    public void setPrimerContent(String primerContent) {
        this.primerContent = primerContent;
    }

    public Tag getRoot() {
        return root;
    }

    public void setRoot(Tag root) {
        this.root = root;
    }

    public Tag setRootIfNull(String tagName) {
        if (null == this.root)
            this.root = Tag.tag(tagName);
        return this.root;
    }

}
