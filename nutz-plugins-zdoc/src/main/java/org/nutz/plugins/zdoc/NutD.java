package org.nutz.plugins.zdoc;

import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

public abstract class NutD {

    /**
     * 指向自己所属集合
     */
    private NutDSet parent;

    /**
     * 缓存文档路径中的名称部分
     */
    private String name;

    /**
     * 文档的一些元数据，固定的元数据有
     * 
     * <ul>
     * <li>title : 文档标题
     * <li>author : 作者
     * <li>tags : 标签（是一个数组）
     * </ul>
     */
    protected NutMap meta;

    public NutD(String name) {
        this.name = name;
        this.meta = new NutMap();
    }

    public boolean isRoot() {
        return null == parent;
    }

    public NutDSet getHome() {
        if (null == parent) {
            if (this instanceof NutDSet)
                return (NutDSet) this;
            else
                return null;
        }
        return parent.getHome();
    }

    public String getPath() {
        LinkedList<String> nms = new LinkedList<>();
        this.__join_path(nms);
        return Strings.join("/", nms);
    }

    protected void __join_path(LinkedList<String> nms) {
        if (null != parent) {
            nms.addFirst(name);
            this.parent.__join_path(nms);
        }
    }

    public boolean hasParent() {
        return null != parent;
    }

    public NutDSet getParent() {
        return parent;
    }

    public void setParent(NutDSet parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NutMap getMeta() {
        return meta;
    }

    public String getTitle() {
        return meta.getString("title", name);
    }

    public String getTitle(String dft) {
        return meta.getString("title", dft);
    }

    public boolean hasAuthor() {
        return meta.has("author");
    }

    public String getAuthor() {
        return meta.getString("author");
    }

    public boolean hasTags() {
        return meta.has("tags");
    }

    public List<String> getTags() {
        return meta.getList("tags", String.class);
    }
}
