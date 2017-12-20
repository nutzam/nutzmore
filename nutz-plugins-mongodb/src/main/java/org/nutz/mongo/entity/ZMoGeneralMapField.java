package org.nutz.mongo.entity;

import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.adaptor.ZMoAs;

public class ZMoGeneralMapField extends ZMoField {

    @Override
    public boolean isEnumStr() {
        return true;
    }

    @Override
    public void setEnumStr(boolean isEnumString) {}

    @Override
    public ZMoField clone() {
        return new ZMoGeneralMapField();
    }

    @Override
    public ZMoAdaptor getAdaptor() {
        return ZMoAs.smart();
    }

    @Override
    public void setAdaptor(ZMoAdaptor adaptor) {}

}
