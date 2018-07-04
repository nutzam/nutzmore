package org.nutz.mongo;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.entity.JsonCallback;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mongo.adaptor.ZMoAs;
import org.nutz.mongo.entity.ZMoEntity;
import org.nutz.mongo.entity.ZMoEntityHolder;
import org.nutz.mongo.entity.ZMoEntityMaker;
import org.nutz.mongo.entity.ZMoField;
import org.nutz.mongo.entity.ZMoGeneralMapEntity;
import org.nutz.mongo.fieldfilter.ZMoFF;

import com.mongodb.Cursor;
import com.mongodb.DBObject;

/**
 * 一个工厂类，用来转换普通 JavaObject 与 ZMoDoc 对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMo {

    /**
     * @return 一个新创建的文档
     */
    public ZMoDoc newDoc() {
        return ZMoDoc.NEW();
    }

    /**
     * @return 对象映射生成器
     */
    public ZMoEntityMaker maker() {
        return maker;
    }

    /**
     * 将任何 Java 对象（也包括 ZMoDoc）转换成 ZMoDoc
     * 
     * @param obj
     *            Java 对象，可以是 Pojo 或者 Map
     * @return 文档对象
     */
    public ZMoDoc toDoc(Object obj) {
        ZMoEntity en;
        // 如果 NULL 直接返回咯
        if (null == obj) {
            return null;
        }
        // 本身就是 DBObject
        else if (obj instanceof DBObject) {
            return ZMoDoc.WRAP((DBObject) obj);
        }
        // 普通 Map
        else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            // 获取 Map 的 KEY
            String _key = (String) map.get("_key");
            // 获取映射关系
            if (null == _key) {
                en = getEntity(obj.getClass());
            } else {
                en = holder.get(_key);
            }
            // 确定一定会有映射关系
            if (null == en) {
                throw Lang.makeThrow("Map[%s] without define!", _key);
            }
        }
        // POJO
        else {
            Class<? extends Object> objType = obj.getClass();
            // 数组不可以
            if (objType.isArray()) {
                throw Lang.makeThrow("Array can not toDoc : %s", objType.getName());
            }
            // 集合不可以
            else if (obj instanceof Collection) {
                throw Lang.makeThrow("Collection can not toDoc : %s", objType.getName());
            }
            // POJO
            else {
                en = getEntity(objType);
            }
        }
        // 执行转换
        return toDoc(obj, en);
    }

    /**
     * 根据一个 JSON 字符串生成的 Map 对象来生成 ZMoDoc 对象
     * 
     * @param json
     *            JSON 字符串，可以省略前后的大括号，即，可以是 "x:100,y:'23'"
     * @param args
     *            如果参数 json 为一个格式化字符串模板，那么这里给出参数
     * @return 文档对象
     */
    public ZMoDoc toDoc(String json, Object... args) {
        return toDoc(Lang.mapf(json, args));
    }

    /**
     * 将任何一个对象转换成 ZMoDoc，并强制指定映射关系
     * 
     * @param obj
     *            Java 对象，可以是 Pojo 或者 Map
     * @param en
     *            映射关系，如果为 null 相当于 toDoc(obj)
     * @return 文档对象
     */
    public ZMoDoc toDoc(Object obj, ZMoEntity en) {
        ZMoDoc doc = ZMoDoc.NEW();
        if (en == null)
        	en = getEntity(obj.getClass());
        Set<String> javaNames = en.getJavaNames(obj);
        // 获取字段过滤器
        ZMoFF ff = ZMoFF.get();

        // 循环每个字段
        for (String javaName : javaNames) {
            Object v = en.getValue(obj, javaName);
            ZMoField fld = en.javaField(javaName);
            /*
             * 是否需要过滤
             */
            if (null != ff && ff.isIgnore(fld, v)) {
                continue;
            }
            /*
             * 适配值
             */
            String mongoName = en.getMongoNameFromJava(javaName);
            // 空值
            if (null == v) {
                doc.put(mongoName, v);
            }
            // _id
            else if ("_id".equals(mongoName)) {
                if (v instanceof ObjectId || v instanceof Boolean || v instanceof Integer || v instanceof Long) {
                    doc.put(mongoName, v);
                } else {
                    try {
                        doc.put(mongoName, new ObjectId(v.toString()));
                    }
                    catch (IllegalArgumentException e) {
                        doc.put(mongoName, v);
                    }
                }
            }
            // 其他值适配
            else {
                Object dbv = fld.getAdaptor().toMongo(fld, v);
                doc.put(mongoName, dbv);
            }
        }
        return doc;
    }

    /**
     * 将一组 Java 对象变成文档数组
     * 
     * @param objs
     *            Java 对象数组
     * @return 文档数组
     */
    public ZMoDoc[] toDocArray(Object[] objs) {
        return toDocArray(null, objs);
    }

    /**
     * 将一组 Java 对象变成文档数组
     * 
     * @param objs
     *            Java 对象数组
     * @return 文档数组
     */
    public ZMoDoc[] toDocArray(List<?> objs) {
        return toDocArray(null, objs);
    }

    /**
     * 将一组 Java 对象变成文档数组，并强制指定映射关系，以便提高速度
     * 
     * @param en
     *            映射关系，如果为 null 则自动判断如何映射
     * @param objs
     *            Java 对象数组
     * @return 文档数组
     */
    public ZMoDoc[] toDocArray(ZMoEntity en, Object[] objs) {
        ZMoDoc[] docs = new ZMoDoc[objs.length];
        if (en == null && objs.length > 0)
            en = this.getEntity(objs[0].getClass());
        int i = 0;
        for (Object obj : objs)
            docs[i++] = toDoc(obj, en);
        return docs;
    }

    /**
     * 将一组 Java 对象变成文档数组，并强制指定映射关系，以便提高速度
     * 
     * @param en
     *            映射关系，如果为 null 则自动判断如何映射
     * @param objs
     *            Java 对象数组
     * @return 文档数组
     */
    public ZMoDoc[] toDocArray(ZMoEntity en, List<?> objs) {
        ZMoDoc[] docs = new ZMoDoc[objs.size()];
        if (!objs.isEmpty())
            en = this.getEntity(objs.get(0).getClass());
        int i = 0;
        for (Object obj : objs)
            docs[i++] = toDoc(obj, en);
        return docs;
    }

    /**
     * 将一组 Java 对象变成文档列表
     * 
     * @param objs
     *            Java 对象数组
     * @return 文档列表
     */
    public List<ZMoDoc> toDocList(Object[] objs) {
        return toDocList(null, objs);
    }

    /**
     * 将一组 Java 对象变成文档列表
     * 
     * @param objs
     *            Java 对象数组
     * @return 文档列表
     */
    public List<ZMoDoc> toDocList(List<?> objs) {
        return toDocList(null, objs);
    }

    /**
     * 将一组 Java 对象变成文档列表，并强制指定映射关系，以便提高速度
     * 
     * @param en
     *            映射关系，如果为 null 则自动判断如何映射
     * @param objs
     *            Java 对象数组
     * @return 文档列表
     */
    public List<ZMoDoc> toDocList(ZMoEntity en, Object[] objs) {
        List<ZMoDoc> docs = new ArrayList<ZMoDoc>(objs.length);
        if (en == null && objs.length > 0)
        	en = getEntity(objs[0].getClass());
        for (Object obj : objs)
            docs.add(toDoc(obj, en));
        return docs;
    }

    /**
     * 将一组 Java 对象变成文档列表，并强制指定映射关系，以便提高速度
     * 
     * @param en
     *            映射关系，如果为 null 则自动判断如何映射
     * @param objs
     *            Java 对象数组
     * @return 文档列表
     */
    public List<ZMoDoc> toDocList(ZMoEntity en, List<?> objs) {
        List<ZMoDoc> docs = new ArrayList<ZMoDoc>(objs.size());
        if (en == null && objs.size() > 0)
        	en = getEntity(objs.get(0).getClass());
        for (Object obj : objs)
            docs.add(toDoc(obj, en));
        return docs;
    }

    /**
     * 将任何一个文档对象转换成 ZMoDoc，<br>
     * 根据传入的映射关系来决定是变成 Pojo还是Map
     * 
     * @param dbobj
     *            文档对象
     * @param en
     *            映射关系，如果为 null，则变成普通 Map
     * @return 普通Java对象
     */
    public Object fromDoc(DBObject dbobj, ZMoEntity en) {
        if (null == dbobj)
            return null;
        ZMoDoc doc = ZMoDoc.WRAP(dbobj);
        if (null == en) {
            en = holder.get(DFT_MAP_KEY);
        }
        Object obj = en.born();
        Set<String> keys = en.getMongoNames(doc);
        for (String key : keys) {
            try {
                // 获得映射关系
                ZMoField fld = en.mongoField(key);
                String javaName = en.getJavaNameFromMongo(key);
                String mongoName = en.getMongoNameFromJava(javaName);

                // 获取值
                Object v = doc.get(mongoName);
                Object pojov;

                // 空值
                if (null == v) {
                    pojov = null;
                }
                // _id
                else if ("_id".equals(key)) {
                    pojov = ZMoAs.id().toJava(fld, v);
                }
                // 其他值适配
                else {
                    pojov = fld.getAdaptor().toJava(fld, v);
                }
                // 设置值

                en.setValue(obj, javaName, pojov);
            }
            catch (Exception e) {
                throw Lang.wrapThrow(e, "fail to set field %s#%s", en.getType(), key);
            }
        }
        return obj;
    }

    /**
     * 将任何一个文档对象转换成指定 Java 对象（不可以是 Map 等容器）
     * 
     * @param <T>
     *            对象的类型参数
     * @param dbobj
     *            文档对象
     * @param classOfT
     *            对象类型，根据这个类型可以自动获得映射关系
     * @return 特定的Java对象
     */
    @SuppressWarnings("unchecked")
    public <T> T fromDocToObj(DBObject dbobj, Class<T> classOfT) {
        return (T) (fromDoc(dbobj, getEntity(classOfT)));
    }

    /**
     * 将任何一个文档对象转换成一个 Map 对象
     * 
     * @param dbobj
     *            文档对象
     * @return Map 对象
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> fromDocToMap(DBObject dbobj) {
        return (Map<String, Object>) fromDoc(dbobj, null);
    }

    /**
     * 将任何一个文档对象转换成一个指定类型的 Map 对象
     * 
     * @param <T>
     *            Map 对象的类型参数
     * @param dbobj
     *            文档对象
     * @return Map 对象
     */
    @SuppressWarnings("unchecked")
    public <T extends Map<String, Object>> T fromDocToMap(DBObject dbobj,
                                                          Class<T> classOfMap) {
        return (T) fromDoc(dbobj, getEntity(classOfMap));
    }

    /**
     * 根据对象类型，得到一个映射关系（懒加载）
     * 
     * @param type
     *            对象类型可以是 POJO 或者 Map
     * @return 映射关系
     */
    public ZMoEntity getEntity(Class<? extends Object> type) {
        ZMoEntity en = holder.get(type.getName());
        // 如果木有加载过，那么尝试加载
        if (null == en) {
            synchronized (this) {
                en = holder.get(type.getName());
                if (null == en) {
                    // 如果是 Map 或者 DBObject 用默认Map映射对象来搞
                    if (Map.class.isAssignableFrom(type)
                        || DBObject.class.isAssignableFrom(type)) {
                        en = holder.get(DFT_MAP_KEY).clone();
                        en.setType(type);
                        en.setBorning(en.getMirror().getBorning());
                        holder.add(type.getName(), en);
                    }
                    // 普通 POJO
                    else {
                        en = maker.make(type);
                        holder.add(type.getName(), en);
                    }
                }
            }
        }
        return en;
    }

    /**
     * @return 对象映射持有器
     */
    public ZMoEntityHolder holder() {
        return holder;
    }

    // ------------------------------------------------------------
    // 下面是这个类的字段和单例方法
    private static ZMo _me_ = new ZMo();

    /**
     * 默认的 Map 映射实体键值
     */
    public static final String DFT_MAP_KEY = "$nutz-mongo-dftmap-key$";

    // 这里创建一个默认的 Map 实体
    static {
        _me_.holder.add(DFT_MAP_KEY, new ZMoGeneralMapEntity());
    }

    /**
     * @return 单例
     */
    public static ZMo me() {
        return _me_;
    }

    private ZMoEntityHolder holder;

    private ZMoEntityMaker maker;

    private ZMo() {
        holder = new ZMoEntityHolder();
        maker = new ZMoEntityMaker();
    }

    //
    // 下面是一下常用的帮助函数
    //

    private static final Pattern OBJ_ID = Pattern.compile("^[0-9a-f]{24}$");

    /**
     * 判断给定的字符串是否是 MongoDB 默认的 ID 格式
     * 
     * @param ID
     *            给定 ID
     * @return true or false
     */
    public static boolean isObjectId(String ID) {
        if (null == ID || ID.length() != 24)
            return false;
        return OBJ_ID.matcher(ID).find();
    }

    static {
        try {
            Json.getEntity(Mirror.me(ObjectId.class)).setJsonCallback(new JsonCallback() {
                public boolean toJson(Object obj, JsonFormat jf, Writer writer) throws IOException {
                    writer.write("\"" + ((ObjectId)obj).toHexString()+"\"");
                    return true;
                }
                public Object fromJson(Object obj) {
                    return null;
                }
            });
        }
        catch (Exception e) {
            // 不可能吧?
        }
    }
    
    public List<Object> fromDoc(Cursor cursor, ZMoEntity en) {
        List<Object> list = new ArrayList<Object>();
        while (cursor.hasNext()) {
            list.add(fromDoc(cursor.next(), en));
        }
        return list;
    }
}
