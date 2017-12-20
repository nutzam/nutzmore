package org.nutz.mongo;

import java.util.Set;

import org.nutz.lang.Lang;

import com.mongodb.DB;

/**
 * 对于 DB 对象的薄封装
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoDB {

    private DB db;

    public ZMoDB(DB db) {
        this.db = db;
    }

    /**
     * 获取集合，如果集合不存在，则抛错
     * 
     * @param name
     *            集合名称
     * @return 集合薄封装
     */
    public ZMoCo c(String name) {
        if (!db.collectionExists(name))
            throw Lang.makeThrow("Colection noexitst: %s.%s", db.getName(), name);
        return new ZMoCo(db.getCollection(name));
    }

    /**
     * 获取一个集合，如果集合不存在，就创建它
     * 
     * @param name
     *            集合名
     * @param dropIfExists
     *            true 如果存在就清除
     * @return 集合薄封装
     */
    public ZMoCo cc(String name, boolean dropIfExists) {
        // 不存在则创建
        if (!db.collectionExists(name)) {
            return createCollection(name, null);
        }
        // 固定清除
        else if (dropIfExists) {
            db.getCollection(name).drop();
            return createCollection(name, null);
        }
        // 已经存在
        return new ZMoCo(db.getCollection(name));
    }

    /**
     * 是否存在某个集合
     * 
     * @param name
     *            集合名
     * @return 是否存在
     */
    public boolean cExists(String name) {
        return db.collectionExists(name);
    }

    /**
     * 创建一个集合
     * 
     * @param name
     *            集合名
     * @param options
     *            集合配置信息
     * @return 集合薄封装
     */
    public ZMoCo createCollection(String name, ZMoDoc options) {
        if (db.collectionExists(name)) {
            throw Lang.makeThrow("Colection exitst: %s.%s", db.getName(), name);
        }

        // 创建默认配置信息
        if (null == options) {
            options = ZMoDoc.NEW("capped:false");
        }

        return new ZMoCo(db.createCollection(name, options));
    }

    /**
     * 清除数据库的游标
     * 
     * @param force
     *            是否强制
     */
    @Deprecated
    public void cleanCursors(boolean force) {
        //db.cleanCursors(force);
    }

    /**
     * @return 当前数据库所有可用集合名称
     */
    public Set<String> cNames() {
        return db.getCollectionNames();
    }

    public DB getNativeDB() {
        return this.db;
    }
}
