package org.nutz.mongo.adaptor;

import org.bson.types.ObjectId;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.entity.ZMoField;

public class ZMoIdAdaptor implements ZMoAdaptor {

    @Override
    public Object toJava(ZMoField fld, Object obj) {
        if (obj instanceof ObjectId || obj instanceof Integer || obj instanceof Long) {
            if (null != fld) {
                Mirror<?> mi = fld.getMirror();
                if (null != mi) {
                    if (mi.isOf(Object.class)) {
                        return obj;
                    }
                    if (mi.isArray() && fld.getEleMirror().isOf(Object.class)) {
                        return obj;
                    }
                }
            }
            return obj.toString();
        }
        throw Lang.makeThrow("should be ObjectId");
    }

    @Override
    public Object toMongo(ZMoField fld, Object obj) {
        if (obj instanceof ObjectId) {
            return obj;
        }
        return new ObjectId(obj.toString());
    }

}
