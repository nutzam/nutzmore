package org.nutz.mongo.adaptor;

import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;
import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.entity.ZMoField;

public class ZMoSimpleAdaptor implements ZMoAdaptor {

    ZMoSimpleAdaptor() {}

    @Override
    public Object toJava(ZMoField fld, Object obj) {
        if (null == fld || obj == null)
            return obj;
        Mirror<?> mirror = fld.getMirror();
        if (null != mirror && (mirror.isArray() || mirror.isCollection())) {
            return Castors.me().castTo(obj, fld.getEleType());
        }
        return Castors.me().castTo(obj, fld.getType());
    }

    @Override
    public Object toMongo(ZMoField fld, Object obj) {
        return obj;
    }

}
