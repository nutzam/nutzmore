package org.nutz.mongo.adaptor;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mongo.ZMo;
import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.entity.ZMoEntity;
import org.nutz.mongo.entity.ZMoField;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class ZMoArrayAdaptor implements ZMoAdaptor {

    ZMoArrayAdaptor() {}

    @Override
    public Object toJava(ZMoField fld, Object obj) {
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;

            // 获取元素的实体
            ZMoEntity en = null;

            // 创建数组
            Object arr = null;
            if (fld == null) {
                arr = Array.newInstance(Object.class, list.size());
            }
            // 让 fld 的 Borning 来创建
            else {
                arr = fld.getBorning().born(list.size());
            }

            // 开始循环数组
            int i = 0;
            Iterator<?> it = list.iterator();
            while (it.hasNext()) {
                Object eleMongo = it.next();
                Object elePojo;

                // 如果元素是个 Mongo 类型
                if (eleMongo instanceof DBObject) {
                    // 确保已经获得过实体过了，这里这个代码考虑到效率
                    // 就是说一个集合或者数组，映射方式总是一样的
                    // 如果有不一样的，那么就完蛋了
                    if (null == en) {
                        en = ZMo.me().getEntity(eleMongo.getClass());
                    }
                    // 转换
                    elePojo = ZMo.me().fromDoc((DBObject) eleMongo, en);
                }
                // 如果 fld 有 adaptor
                else if (null != fld && null != fld.getEleAdaptor()) {
                    elePojo = fld.getEleAdaptor().toJava(fld, eleMongo);
                }
                // 其他情况，直接上 smart 咯
                else {
                    elePojo = ZMoAs.smart().toJava(null, eleMongo);
                }
                // 加入到数组中
                Array.set(arr, i++, elePojo);
            }

            return arr;
        }
        throw Lang.makeThrow("toJava error: %s", obj.getClass());
    }

    @Override
    public Object toMongo(ZMoField fld, Object obj) {
        if (obj.getClass().isArray()) {
            BasicDBList list = new BasicDBList();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                Object objPojo = Array.get(obj, i);
                Object objMongo;
                Mirror<?> mi = Mirror.me(objPojo);
                // null
                if (null == objPojo) {
                    objMongo = null;
                }
                // 对于 ObjectId
                else if (objPojo instanceof ObjectId) {
                    objMongo = objPojo;
                }
                // 普通的 DBObject
                else if (objPojo instanceof DBObject) {
                    objMongo = obj;
                }
                // Map 或者 POJO
                else if (mi.isMap() || mi.isPojo()) {
                    objMongo = ZMo.me().toDoc(objPojo);
                }
                // 其他类型用 smart 转一下咯
                else {
                    objMongo = ZMoAs.smart().toMongo(null, objPojo);
                }
                // 加入到列表
                list.add(objMongo);
            }

            return list;
        }
        throw Lang.makeThrow("toMongo error: %s", obj.getClass());
    }

}
