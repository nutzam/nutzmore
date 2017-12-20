package org.nutz.mongo.fieldfilter;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.util.Closer;
import org.nutz.mongo.ZMo;
import org.nutz.mongo.ZMoDoc;
import org.nutz.mongo.entity.ZMoField;

/**
 * 字段过滤器，在 toDoc 的时候生效，通过 ZMo.setFilterFilter 设置
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class ZMoFF {

    private static ThreadLocal<ZMoFF> _field_filters_ = new ThreadLocal<ZMoFF>();

    public static void set(ZMoFF ff) {
        if (null != ff) {
            _field_filters_.set(ff);
        }
    }

    public static ZMoFF get() {
        return _field_filters_.get();
    }

    public static void remove() {
        _field_filters_.remove();
    }

    public <T> T run(Closer<T> closer) {
        set(this);
        try {
            return closer.invoke();
        }
        finally {
            remove();
        }
    }

    public ZMoDoc toDoc(final Object obj) {
        return run(new Closer<ZMoDoc>() {
            public ZMoDoc invoke() {
                return ZMo.me().toDoc(obj);
            }
        });
    }

    /**
     * true 表示匹配上的字段忽略，false 表示匹配上的字段不忽略
     */
    private boolean asIgnore;

    /**
     * true 表示匹配Java字段，false 表示匹配Mongo字段
     */
    private boolean byJava;

    /**
     * 是否忽略 null 值
     */
    private boolean ignoreNull;

    /**
     * 对于特殊字段，指定为特殊值的时候忽略
     */
    private Map<String, Number> ignoreNumber;

    protected ZMoFF() {
        asIgnore = false;
        byJava = true;
        ignoreNull = false;
        ignoreNumber = new HashMap<String, Number>();
    }

    /**
     * @param fld
     *            当前字段
     * @return 是否忽略这个字段
     */
    public boolean isIgnore(ZMoField fld, Object v) {
        if (null == v && ignoreNull)
            return true;

        // 得到名称
        String key = byJava ? fld.getJavaName() : fld.getMongoName();

        // 如果是数字，那么看看是否需要忽略
        if (null != v && !ignoreNumber.isEmpty() && v instanceof Number) {
            Number n = ignoreNumber.get(key);
            if (null != n && n.doubleValue() == ((Number) v).doubleValue())
                return true;
        }

        // 如果匹配上了
        if (match(key)) {
            return asIgnore;
        }
        return !asIgnore;
    }

    public ZMoFF asIgnore(boolean asActive) {
        this.asIgnore = asActive;
        return this;
    }

    public ZMoFF byJava(boolean byJava) {
        this.byJava = byJava;
        return this;
    }

    public ZMoFF ignoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    /**
     * @param key
     *            根据 byJava 的设定
     * @param n
     *            值
     * @return 自身
     */
    public ZMoFF ignoreNumber(String key, Number n) {
        ignoreNumber.put(key, n);
        return this;
    }

    /**
     * 子类的抽象实现
     * 
     * @param fld
     *            字段名
     * @return 是否匹配上
     */
    protected abstract boolean match(String fld);

}
