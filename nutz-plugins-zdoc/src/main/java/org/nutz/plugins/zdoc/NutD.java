package org.nutz.plugins.zdoc;

import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

public abstract class NutD {

    /**
     * 指向自己所属集合
     */
    private transient NutDSet parent;

    /**
     * 缓存文档路径中的名称部分
     */
    private String name;

    /**
     * 存储对象的原始内容，比如，本地文件或者目录对象，或者对应的某条数据库记录
     */
    protected transient Object primerObj;

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

    public Object getPrimerObj() {
        return primerObj;
    }

    public void setPrimerObj(Object obj) {
        this.primerObj = obj;
    }

    public boolean isRoot() {
        return null == parent;
    }

    public abstract boolean isDoc();

    public abstract boolean isSet();

    public void remove() {
        if (null != parent) {
            parent.removeChild(this.name);
        }
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

    /**
     * 缓存 getPath() 的结果
     */
    private String _path;

    public String getPath() {
        if (null == _path) {
            LinkedList<String> nms = new LinkedList<>();
            this.__join_path(nms);
            _path = Strings.join("/", nms);
        }
        return _path;
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

    public void setTitle(String title) {
        meta.put("title", title);
    }

    public boolean hasAuthor() {
        return meta.has("authors");
    }

    public List<String> getAuthors() {
        return meta.getList("authors", String.class);
    }

    public void addAuthors(String... authors) {
        meta.pushTo("authors", authors);
    }

    public void addAuthors(List<String> authors) {
        meta.pushTo("authors", authors);
    }

    public boolean hasTags() {
        return meta.has("tags");
    }

    public List<String> getTags() {
        return meta.getList("tags", String.class);
    }

    public void addTags(String... tags) {
        meta.pushTo("tags", tags);
    }

    public void addTags(List<String> tags) {
        meta.pushTo("tags", tags);
    }
}
