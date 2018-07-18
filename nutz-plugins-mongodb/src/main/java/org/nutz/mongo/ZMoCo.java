package org.nutz.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.nutz.dao.pager.Pager;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.Callback;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.entity.ZMoEntity;

import com.mongodb.AggregationOptions;
import com.mongodb.CommandResult;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBDecoderFactory;
import com.mongodb.DBEncoder;
import com.mongodb.DBEncoderFactory;
import com.mongodb.DBObject;
import com.mongodb.GroupCommand;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * 对于集合类的薄封装
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoCo {

    private DBCollection dbc;

    private static final Log log = Logs.get();

    public ZMoCo(DBCollection c) {
        this.dbc = c;
    }

    public WriteResult insert(ZMoDoc[] arr, WriteConcern concern) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", arr, concern));
        return dbc.insert(arr, concern);
    }

    public WriteResult insert(ZMoDoc[] arr, WriteConcern concern, DBEncoder encoder) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", arr, concern, encoder));
        return dbc.insert(arr, concern, encoder);
    }

    public WriteResult insert(ZMoDoc o, WriteConcern concern) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", o, concern));
        return dbc.insert(o, concern);
    }

    public WriteResult insert(ZMoDoc... arr) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", (Object) arr));
        return dbc.insert(arr);
    }

    public WriteResult insert(WriteConcern concern, ZMoDoc... arr) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", concern, (Object) arr));
        return dbc.insert(concern, arr);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public WriteResult insert(List<ZMoDoc> list) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", list));
        return dbc.insert((List) list);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public WriteResult insert(List<ZMoDoc> list, WriteConcern concern) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", list, concern));
        return dbc.insert((List) list, concern);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public WriteResult insert(List<ZMoDoc> list, WriteConcern concern, DBEncoder encoder) {
        if (log.isDebugEnabled())
            log.debug(log_format("insert", list, concern, encoder));
        return dbc.insert((List) list, concern, encoder);
    }

    public WriteResult update(ZMoDoc q,
                              ZMoDoc o,
                              boolean upsert,
                              boolean multi,
                              WriteConcern concern) {
        if (log.isDebugEnabled())
            log.debug(log_format("update", q, o, upsert, multi, concern));
        return dbc.update(q, o, upsert, multi, concern);
    }

    public WriteResult update(ZMoDoc q,
                              ZMoDoc o,
                              boolean upsert,
                              boolean multi,
                              WriteConcern concern,
                              DBEncoder encoder) {
        if (log.isDebugEnabled())
            log.debug(log_format("update", q, o, upsert, multi, concern, encoder));
        return dbc.update(q, o, upsert, multi, concern, encoder);
    }

    public WriteResult update(ZMoDoc q, ZMoDoc o, boolean upsert, boolean multi) {
        if (log.isDebugEnabled())
            log.debug(log_format("update", q, o, upsert, multi));
        return dbc.update(q, o, upsert, multi);
    }

    public WriteResult update(ZMoDoc q, ZMoDoc o) {
        if (log.isDebugEnabled())
            log.debug(log_format("update", q, o));
        return dbc.update(q, o);
    }

    public WriteResult updateMulti(ZMoDoc q, ZMoDoc o) {
        if (log.isDebugEnabled())
            log.debug(log_format("updateMulti", q, o));
        return dbc.updateMulti(q, o);
    }

    public WriteResult remove(ZMoDoc o, WriteConcern concern) {
        if (log.isDebugEnabled())
            log.debug(log_format("remove", o, concern));
        return dbc.remove(o, concern);
    }

    public WriteResult remove(ZMoDoc o, WriteConcern concern, DBEncoder encoder) {
        if (log.isDebugEnabled())
            log.debug(log_format("remove", o, concern, encoder));
        return dbc.remove(o, concern, encoder);
    }

    public WriteResult remove(ZMoDoc o) {
        if (log.isDebugEnabled())
            log.debug(log_format("remove", o));
        return dbc.remove(o);
    }

    public ZMoDoc findAndModify(ZMoDoc query,
                                ZMoDoc fields,
                                ZMoDoc sort,
                                boolean remove,
                                ZMoDoc update,
                                boolean returnNew,
                                boolean upsert) {
        if (log.isDebugEnabled())
            log.debug(log_format("findAndModify",
                                 query,
                                 fields,
                                 sort,
                                 remove,
                                 update,
                                 returnNew,
                                 upsert));
        return ZMoDoc.WRAP(dbc.findAndModify(query,
                                             fields,
                                             sort,
                                             remove,
                                             update,
                                             returnNew,
                                             upsert));
    }

    public ZMoDoc findAndModify(ZMoDoc query, ZMoDoc sort, ZMoDoc update) {
        if (log.isDebugEnabled())
            log.debug(log_format("findAndModify", query, sort, update));
        return ZMoDoc.WRAP(dbc.findAndModify(query, sort, update));
    }

    public ZMoDoc findAndModify(ZMoDoc query, ZMoDoc update) {
        if (log.isDebugEnabled())
            log.debug(log_format("findAndModify", query, update));
        return ZMoDoc.WRAP(dbc.findAndModify(query, update));
    }

    public ZMoDoc findAndRemove(ZMoDoc query) {
        if (log.isDebugEnabled())
            log.debug(log_format("findAndRemove", query));
        return ZMoDoc.WRAP(dbc.findAndRemove(query));
    }

    public void createIndex(ZMoDoc keys) {
        if (log.isDebugEnabled())
            log.debug(log_format("createIndex", keys));
        dbc.createIndex(keys);
    }

    public void createIndex(ZMoDoc keys, ZMoDoc options) {
        if (log.isDebugEnabled())
            log.debug(log_format("createIndex", keys, options));
        dbc.createIndex(keys, options);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setHintFields(List<ZMoDoc> lst) {
        if (log.isDebugEnabled())
            log.debug(log_format("setHintFields", lst));
        dbc.setHintFields((List) lst);
    }

    public DBCursor find(ZMoDoc ref) {
        if (log.isDebugEnabled())
            log.debug(log_format("find", ref));
        return dbc.find(ref);
    }

    public DBCursor find(ZMoDoc ref, ZMoDoc keys) {
        if (log.isDebugEnabled())
            log.debug(log_format("find", ref, keys));
        return dbc.find(ref, keys);
    }

    public DBCursor find() {
        if (log.isDebugEnabled())
            log.debug(log_format("find"));
        return dbc.find();
    }

    public ZMoDoc findOne() {
        if (log.isDebugEnabled())
            log.debug(log_format("findOne"));
        return ZMoDoc.WRAP(dbc.findOne());
    }

    public ZMoDoc findOne(ZMoDoc o) {
        if (log.isDebugEnabled())
            log.debug(log_format("findOne", o));
        return ZMoDoc.WRAP(dbc.findOne(o));
    }

    public ZMoDoc findOne(ZMoDoc o, ZMoDoc fields) {
        if (log.isDebugEnabled())
            log.debug(log_format("findOne", o, fields));
        return ZMoDoc.WRAP(dbc.findOne(o, fields));
    }

    public ZMoDoc findOne(ZMoDoc o, ZMoDoc fields, ZMoDoc orderBy) {
        if (log.isDebugEnabled())
            log.debug(log_format("findOne", o, fields, orderBy));
        return ZMoDoc.WRAP(dbc.findOne(o, fields, orderBy));
    }

    public ZMoDoc findOne(ZMoDoc o, ZMoDoc fields, ReadPreference readPref) {
        if (log.isDebugEnabled())
            log.debug(log_format("findOne", o, fields, readPref));
        return ZMoDoc.WRAP(dbc.findOne(o, fields, readPref));
    }

    public ZMoDoc findOne(ZMoDoc o, ZMoDoc fields, ZMoDoc orderBy, ReadPreference readPref) {
        if (log.isDebugEnabled())
            log.debug(log_format("findOne", o, fields, orderBy, readPref));
        return ZMoDoc.WRAP(dbc.findOne(o, fields, orderBy, readPref));
    }

    public WriteResult save(ZMoDoc jo) {
        if (log.isDebugEnabled())
            log.debug(log_format("save", jo));
        return dbc.save(jo);
    }

    public WriteResult save(ZMoDoc jo, WriteConcern concern) {
        if (log.isDebugEnabled())
            log.debug(log_format("save", jo, concern));
        return dbc.save(jo, concern);
    }

    public void dropIndexes() {
        if (log.isDebugEnabled())
            log.debug(log_format("dropIndexes"));
        dbc.dropIndexes();
    }

    public void dropIndexes(String name) {
        if (log.isDebugEnabled())
            log.debug(log_format("dropIndexes", name));
        dbc.dropIndexes(name);
    }

    public void drop() {
        if (log.isDebugEnabled())
            log.debug(log_format("drop"));
        dbc.drop();
    }

    public long count() {
        if (log.isDebugEnabled())
            log.debug(log_format("count"));
        return dbc.count();
    }

    public long count(ZMoDoc query) {
        if (log.isDebugEnabled())
            log.debug(log_format("count", query));
        return dbc.count(query);
    }

    public long count(ZMoDoc query, ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("count", query, readPrefs));
        return dbc.count(query, readPrefs);
    }

    public long getCount() {
        if (log.isDebugEnabled())
            log.debug(log_format("getCount"));
        return dbc.getCount();
    }

    public long getCount(ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("getCount", readPrefs));
        return dbc.getCount(readPrefs);
    }

    public long getCount(ZMoDoc query) {
        if (log.isDebugEnabled())
            log.debug(log_format("getCount", query));
        return dbc.getCount(query);
    }

    public long getCount(ZMoDoc query, ZMoDoc fields) {
        if (log.isDebugEnabled())
            log.debug(log_format("getCount", query, fields));
        return dbc.getCount(query, fields);
    }

    public long getCount(ZMoDoc query, ZMoDoc fields, ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("getCount", query, fields, readPrefs));
        return dbc.getCount(query, fields, readPrefs);
    }

    public long getCount(ZMoDoc query, ZMoDoc fields, long limit, long skip) {
        if (log.isDebugEnabled())
            log.debug(log_format("getCount", query, fields, limit, skip));
        return dbc.getCount(query, fields, limit, skip);
    }

    public long getCount(ZMoDoc query,
                         ZMoDoc fields,
                         long limit,
                         long skip,
                         ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("getCount", query, fields, limit, skip, readPrefs));
        return dbc.getCount(query, fields, limit, skip, readPrefs);
    }

    public DBCollection rename(String newName) {
        if (log.isDebugEnabled())
            log.debug(log_format("rename", newName));
        return dbc.rename(newName);
    }

    public DBCollection rename(String newName, boolean dropTarget) {
        if (log.isDebugEnabled())
            log.debug(log_format("rename", newName, dropTarget));
        return dbc.rename(newName, dropTarget);
    }

    public ZMoDoc group(ZMoDoc key, ZMoDoc cond, ZMoDoc initial, String reduce) {
        if (log.isDebugEnabled())
            log.debug(log_format("group", key, cond, initial, reduce));
        return ZMoDoc.WRAP(dbc.group(key, cond, initial, reduce));
    }

    public ZMoDoc group(ZMoDoc key, ZMoDoc cond, ZMoDoc initial, String reduce, String finalize) {
        if (log.isDebugEnabled())
            log.debug(log_format("group", key, cond, initial, reduce, finalize));
        return ZMoDoc.WRAP(dbc.group(key, cond, initial, reduce, finalize));
    }

    public ZMoDoc group(ZMoDoc key,
                        ZMoDoc cond,
                        ZMoDoc initial,
                        String reduce,
                        String finalize,
                        ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("group", key, cond, initial, reduce, finalize, readPrefs));
        return ZMoDoc.WRAP(dbc.group(key, cond, initial, reduce, finalize, readPrefs));
    }

    public ZMoDoc group(GroupCommand cmd) {
        if (log.isDebugEnabled())
            log.debug(log_format("group", cmd));
        return ZMoDoc.WRAP(dbc.group(cmd));
    }

    public ZMoDoc group(GroupCommand cmd, ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("group", cmd, readPrefs));
        return ZMoDoc.WRAP(dbc.group(cmd, readPrefs));
    }

    public List<?> distinct(String key) {
        if (log.isDebugEnabled())
            log.debug(log_format("distinct", key));
        return dbc.distinct(key);
    }

    public List<?> distinct(String key, ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("distinct", key, readPrefs));
        return dbc.distinct(key, readPrefs);
    }

    public List<?> distinct(String key, ZMoDoc query) {
        if (log.isDebugEnabled())
            log.debug(log_format("distinct", key, query));
        return dbc.distinct(key, query);
    }

    public List<?> distinct(String key, ZMoDoc query, ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("distinct", key, query, readPrefs));
        return dbc.distinct(key, query, readPrefs);
    }

    public MapReduceOutput mapReduce(String map, String reduce, String outputTarget, ZMoDoc query) {
        if (log.isDebugEnabled())
            log.debug(log_format("mapReduce", map, reduce, outputTarget, query));
        return dbc.mapReduce(map, reduce, outputTarget, query);
    }

    public MapReduceOutput mapReduce(String map,
                                     String reduce,
                                     String outputTarget,
                                     OutputType outputType,
                                     ZMoDoc query) {
        if (log.isDebugEnabled())
            log.debug(log_format("mapReduce", map, reduce, outputTarget, query));
        return dbc.mapReduce(map, reduce, outputTarget, outputType, query);
    }

    public MapReduceOutput mapReduce(String map,
                                     String reduce,
                                     String outputTarget,
                                     OutputType outputType,
                                     ZMoDoc query,
                                     ReadPreference readPrefs) {
        if (log.isDebugEnabled())
            log.debug(log_format("mapReduce", map, reduce, outputTarget, query, readPrefs));
        return dbc.mapReduce(map, reduce, outputTarget, outputType, query, readPrefs);
    }

    public MapReduceOutput mapReduce(MapReduceCommand command) {
        if (log.isDebugEnabled())
            log.debug(log_format("mapReduce", command));
        return dbc.mapReduce(command);
    }

    @Deprecated
    public com.mongodb.AggregationOutput aggregate(final List<DBObject> pipeline) {
        if (log.isDebugEnabled())
            log.debug(log_format("aggregate", pipeline));
        return dbc.aggregate(pipeline);
    }

    public Cursor aggregate(final List<DBObject> pipeline, final AggregationOptions options) {
        if (log.isDebugEnabled())
            log.debug(log_format("aggregate", pipeline));
        return dbc.aggregate(pipeline, options);
    }

    public void aggregate(final List<DBObject> pipeline,
                          final AggregationOptions options,
                          Callback<Cursor> callback) {
        Cursor cursor = aggregate(pipeline, options);
        try {
            callback.invoke(cursor);
        }
        finally {
            cursor.close();
        }
    }

    public List<ZMoDoc> getIndexInfo() {
        List<DBObject> dbobjs = dbc.getIndexInfo();
        List<ZMoDoc> list = new ArrayList<ZMoDoc>(dbobjs.size());
        for (DBObject dbojb : dbobjs)
            list.add(ZMoDoc.WRAP(dbojb));
        return list;
    }

    public void dropIndex(ZMoDoc keys) {
        if (log.isDebugEnabled())
            log.debug(log_format("dropIndex", keys));
        dbc.dropIndex(keys);
    }

    public void dropIndex(String name) {
        if (log.isDebugEnabled())
            log.debug(log_format("dropIndex", name));
        dbc.dropIndex(name);
    }

    public CommandResult getStats() {
        if (log.isDebugEnabled())
            log.debug(log_format("getStats"));
        return dbc.getStats();
    }

    public boolean isCapped() {
        if (log.isDebugEnabled())
            log.debug(log_format("isCapped"));
        return dbc.isCapped();
    }

    public ZMoCo getCollection(String n) {
        return new ZMoCo(dbc.getCollection(n));
    }

    public String getName() {
        return dbc.getName();
    }

    public String getFullName() {
        return dbc.getFullName();
    }

    public ZMoDB getDB() {
        return new ZMoDB(dbc.getDB());
    }

    public int hashCode() {
        return dbc.hashCode();
    }

    public boolean equals(Object o) {
        return dbc.equals(o);
    }

    public String toString() {
        return dbc.toString();
    }

    public void setObjectClass(Class<? extends DBObject> c) {
        dbc.setObjectClass(c);
    }

    public Class<?> getObjectClass() {
        return dbc.getObjectClass();
    }

    public void setInternalClass(String path, Class<? extends DBObject> c) {
        dbc.setInternalClass(path, c);
    }

    public void setWriteConcern(WriteConcern concern) {
        dbc.setWriteConcern(concern);
    }

    public WriteConcern getWriteConcern() {
        return dbc.getWriteConcern();
    }

    public void setReadPreference(ReadPreference preference) {
        dbc.setReadPreference(preference);
    }

    public ReadPreference getReadPreference() {
        return dbc.getReadPreference();
    }

    public void addOption(int option) {
        dbc.addOption(option);
    }

    public void setOptions(int options) {
        dbc.setOptions(options);
    }

    public void resetOptions() {
        dbc.resetOptions();
    }

    public int getOptions() {
        return dbc.getOptions();
    }

    public void setDBDecoderFactory(DBDecoderFactory fact) {
        dbc.setDBDecoderFactory(fact);
    }

    public DBDecoderFactory getDBDecoderFactory() {
        return dbc.getDBDecoderFactory();
    }

    public void setDBEncoderFactory(DBEncoderFactory fact) {
        dbc.setDBEncoderFactory(fact);
    }

    public DBEncoderFactory getDBEncoderFactory() {
        return dbc.getDBEncoderFactory();
    }

    protected String _to_json(Object obj) {
        return Json.toJson(obj, JsonFormat.compact().setIgnoreNull(false));
    }

    protected String log_format(String op, Object... args) {
        StringBuilder sb = new StringBuilder("db.").append(getName())
                                                   .append('.')
                                                   .append(op)
                                                   .append('(');
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null
                && (arg instanceof Map || arg.getClass().isArray() || arg instanceof Collection))
                sb.append(_to_json(arg));
            else
                sb.append(arg);
            if (i != args.length - 1)
                sb.append(", ");
        }
        sb.append(')');
        return sb.toString();
    }

    // ----------------------------------------------------------------------------------
    // ORM 相关的方法, 类似的
    /**
     * 查询符合条件的对象
     * 
     * @param klass
     *            需要映射的对象
     * @param ref
     *            查询条件
     * @param keys
     *            需要检出的键,可以是null
     * @param pager
     *            分页对象,recordCount会自动设置, 可以是null
     * @return 符合条件的对象
     */
    public <T> List<T> query(Class<T> klass, ZMoDoc ref, ZMoDoc keys, Pager pager) {
        return query(ZMo.me().getEntity(klass), ref, keys, pager);
    }

    /**
     * 查询符合条件的对象
     * 
     * @param klass
     *            需要映射的对象
     * @param ref
     *            查询条件
     * @param pager
     *            分页对象,recordCount会自动设置, 可以是null
     * @return 符合条件的对象
     */
    public <T> List<T> query(Class<T> klass, ZMoDoc ref, Pager pager) {
        return query(ZMo.me().getEntity(klass), ref, null, pager);
    }

    /**
     * 查询符合条件的对象
     * 
     * @param klass
     *            需要映射的对象
     * @param ref
     *            查询条件
     * @param pageNumber
     *            页数,例如2
     * @param pageSize
     *            分页大小,例如10
     * @return 符合条件的对象
     */
    public <T> List<T> query(Class<T> klass, ZMoDoc ref, int pageNumber, int pageSize) {
        return query(ZMo.me().getEntity(klass), ref, null, new Pager(pageNumber, pageSize));
    }

    /**
     * 查询符合条件的对象
     * 
     * @param klass
     *            需要映射的对象
     * @param ref
     *            查询条件
     * @param keys
     *            需要检出的键,可以是null
     * @param pageNumber
     *            页数,例如2
     * @param pageSize
     *            分页大小,例如10
     * @return 符合条件的对象
     */
    public <T> List<T> query(Class<T> klass,
                             ZMoDoc ref,
                             ZMoDoc keys,
                             int pageNumber,
                             int pageSize) {
        return query(ZMo.me().getEntity(klass), ref, keys, new Pager(pageNumber, pageSize));
    }

    /**
     * 查询符合条件的对象ZMoEntity
     * 
     * @param en
     *            需要映射的实体
     * @param ref
     *            查询条件
     * @param keys
     *            需要检出的键,可以是null
     * @param pager
     *            分页对象
     * @return 符合条件的对象
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(ZMoEntity en, ZMoDoc ref, ZMoDoc keys, Pager pager) {
        DBCursor cur = find(ref, keys);
        if (pager != null) {
            pager.setRecordCount(cur.count());
            cur.skip(pager.getOffset()).limit(pager.getPageSize());
        }
        List<T> list = new ArrayList<T>();
        while (cur.hasNext()) {
            list.add((T) ZMo.me().fromDoc(cur.next(), en));
        }
        return list;
    }

    /**
     * 查询符合条件的一个对象
     * 
     * @param klass
     *            需要映射的类
     * @param ref
     *            查询条件
     * @param keys
     *            需要检出的键,可以是null
     * @return 符合条件的一个对象
     */
    public <T> T fetch(Class<T> klass, ZMoDoc ref, ZMoDoc keys) {
        return fetch(ZMo.me().getEntity(klass), ref, keys);
    }

    /**
     * 查询符合条件的一个对象
     * 
     * @param klass
     *            需要映射的类
     * @param ref
     *            查询条件
     * @return 符合条件的一个对象
     */
    public <T> T fetch(Class<T> klass, ZMoDoc keys) {
        return fetch(ZMo.me().getEntity(klass), null, keys);
    }

    /**
     * 查询符合条件的一个对象
     * 
     * @param en
     *            需要映射的实体
     * @param ref
     *            查询条件
     * @param keys
     *            需要检出的键,可以是null
     * @return 符合条件的一个对象
     */
    public <T> T fetch(ZMoEntity en, ZMoDoc ref, ZMoDoc keys) {
        List<T> list = query(en, ref, keys, new Pager(1, 1));
        if (list.isEmpty())
            return null;
        return list.get(0);
    }

}
