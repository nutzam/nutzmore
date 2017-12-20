package org.nutz.mongo.adaptor;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mongo.ZMo;
import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.ZMoDoc;
import org.nutz.mongo.entity.ZMoEntity;
import org.nutz.mongo.entity.ZMoField;

import com.mongodb.DBObject;

public class ZMoMapAdaptor implements ZMoAdaptor {

    ZMoMapAdaptor() {}

    @Override
    public Object toJava(ZMoField fld, Object obj) {
        if (obj instanceof DBObject) {
            ZMoDoc doc = ZMoDoc.WRAP((DBObject) obj);
            ZMoEntity en = ZMo.me().getEntity(fld.getType());
            return ZMo.me().fromDoc(doc, en);
        }
        throw Lang.makeThrow("toJava error: %s", obj.getClass());
    }

    @Override
    public Object toMongo(ZMoField fld, Object obj) {
        Mirror<?> mi = Mirror.me(obj);
        if (mi.isMap() || mi.isPojo()) {
            ZMoEntity en = ZMo.me().getEntity(mi.getType());
            return ZMo.me().toDoc(obj, en);
        }
        throw Lang.makeThrow("toMongo error: %s", obj.getClass());
    }

}
