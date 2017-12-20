package org.nutz.mongo.adaptor;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.entity.ZMoField;

public class ZMoEnumAdaptor implements ZMoAdaptor {

    ZMoEnumAdaptor() {}

    @Override
    public Object toJava(ZMoField fld, Object obj) {
        if (null == fld)
            return obj;
        return Castors.me().castTo(obj, fld.getType());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object toMongo(ZMoField fld, Object obj) {
        if (obj.getClass().isEnum()) {
            if (null != fld && fld.isEnumStr()) {
                return ((Enum) obj).name();
            }
            return Castors.me().castTo(obj, Integer.class);
        }
        throw Lang.makeThrow("obj<%s> should be ENUM", obj.getClass());
    }

}
