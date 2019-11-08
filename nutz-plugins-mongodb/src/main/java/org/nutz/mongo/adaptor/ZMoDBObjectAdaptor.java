package org.nutz.mongo.adaptor;

import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.mongo.ZMo;
import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.ZMoDoc;
import org.nutz.mongo.entity.ZMoField;

import com.mongodb.DBObject;

/**
 * 如果面对的值的类型是个 DBObject 我们有下面两个策略:
 * <ol>
 * <li>如果是 List，那么就变 ArrayList
 * <li>否则变成 Map
 * </ol>
 * 转成 mongo 的值则不予考虑，直接转换就是了
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoDBObjectAdaptor implements ZMoAdaptor {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object toJava(ZMoField fld, Object obj) {
        // if(obj instanceof ArrayList<?>){
        // return obj;
        // }
        // 可能是 BasicDBList or LazyDBList
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            ArrayList arr = new ArrayList(list.size());
            for (Object o : list) {
                if (o != null && o instanceof DBObject) {
                    arr.add(ZMoAs.dbo().toJava(null, o));
                } else {
                    arr.add(o);
                }
            }
            return arr;
        }
        // 普通 DBObject 变 map
        else if (obj instanceof DBObject) {
            return ZMo.me().fromDocToMap(ZMoDoc.WRAP((DBObject) obj));
        }
        // 不可忍受，抛吧 >:D
        throw Lang.makeThrow("toJava error: %s", obj.getClass());
    }

    @Override
    public Object toMongo(ZMoField fld, Object obj) {
        if (obj instanceof DBObject) {
            return obj;
        }
        throw Lang.makeThrow("toMongo error: %s", obj.getClass());
    }

}
