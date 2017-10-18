package org.nutz.plugins.zdoc;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 描述了一个文档集合
 * <p>
 * 这实际上是一个树形结构，类似:
 * 
 * <pre>
 * NutDSet
 *   - getChildren():List[NutD]
 *      + NutDoc
 *      + NutDSet ...
 *      + NutDoc
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutDSet extends NutD {

    /**
     * 所有的子文档列表，按照文档名称散列
     */
    private Map<String, NutD> map;

    /**
     * 缓存所有子文档
     */
    private List<NutDSet> subs;

    public NutDSet(String name) {
        super(name);
        this.map = new LinkedHashMap<>();
        this.subs = new LinkedList<>();
    }

    @Override
    public boolean isDoc() {
        return false;
    }

    @Override
    public boolean isSet() {
        return true;
    }

    public NutD get(String path) {
        String[] nms = Strings.splitIgnoreBlank(path, "[/\\\\]");
        return __get(nms, 0);
    }

    public Collection<NutD> getChildren() {
        return map.values();
    }

    public NutD removeChild(String name) {
        NutD d = map.remove(name);
        if (null != d && d instanceof NutDSet) {
            subs.remove(d);
        }
        return d;
    }

    private NutD __get(String[] nms, int off) {
        // 防止越界
        if (off < 0 || off >= nms.length)
            return null;

        // 开始分析吧
        String nm = nms[off];
        NutD d = map.get(nm);
        if (null != d) {
            // 还要继续拿
            if (off < (nms.length - 1)) {
                return __get(nms, off + 1);
            }
            // 嗯就是自己了
            return this;
        }
        return null;
    }

    public NutDoc createDoc(String name) {
        if (map.containsKey(name))
            throw Lang.makeThrow("e.zdoc.exists : %s/%s", this.getPath(), name);
        NutDoc d = new NutDoc(name);
        d.setParent(this);
        map.put(name, d);
        return d;
    }

    public NutDoc createDocIfNoExists(String name) {
        NutD d = map.get(name);
        // 不存在，创建
        if (null == d) {
            d = new NutDoc(name);
            d.setParent(this);
            map.put(name, d);
            return (NutDoc) d;
        }
        // 文档，返回
        if (d instanceof NutDoc) {
            return (NutDoc) d;
        }
        // 搞什么鬼
        throw Lang.makeThrow("e.zdoc.nodoc : %s/%s", this.getPath(), name);
    }

    public NutDoc createDocByPath(String path, boolean quiet) {
        String[] nms = Strings.splitIgnoreBlank(path, "[/\\\\]");
        NutDSet ds = this;
        int lastIndex = nms.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            ds = ds.createSetIfNoExists(nms[i]);
        }
        if (quiet)
            return ds.createDocIfNoExists(nms[lastIndex]);
        return ds.createDoc(nms[lastIndex]);
    }

    public NutDSet createSet(String name) {
        if (map.containsKey(name))
            throw Lang.makeThrow("e.zdoc.exists : '%s' already in '%s'", name, this.getPath());
        NutDSet d = new NutDSet(name);
        d.setParent(this);
        map.put(name, d);
        subs.add(d);
        return d;
    }

    public NutDSet createSetIfNoExists(String name) {
        NutD d = map.get(name);
        // 不存在，创建
        if (null == d) {
            NutDSet ds = new NutDSet(name);
            ds.setParent(this);
            map.put(name, ds);
            subs.add(ds);
            return ds;
        }
        // 文档，返回
        if (d instanceof NutDSet) {
            return (NutDSet) d;
        }
        // 搞什么鬼
        throw Lang.makeThrow("e.zdoc.noset : %s/%s", this.getPath(), name);
    }

    public NutDSet createSetByPath(String path, boolean quiet) {
        String[] nms = Strings.splitIgnoreBlank(path, "[/\\\\]");
        NutDSet ds = this;
        int lastIndex = nms.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            ds = ds.createSetIfNoExists(nms[i]);
        }
        if (quiet)
            return ds.createSetIfNoExists(nms[lastIndex]);
        return ds.createSet(nms[lastIndex]);
    }

}
