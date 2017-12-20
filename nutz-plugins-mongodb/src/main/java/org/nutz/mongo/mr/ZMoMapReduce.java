package org.nutz.mongo.mr;

import org.nutz.mongo.ZMoDoc;

public class ZMoMapReduce {

    private String key;

    private String init;

    private ZMoDoc _init_obj;

    private String reduceFunc;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInit() {
        return init;
    }

    public ZMoDoc getInitObj() {
        if (null == _init_obj) {
            synchronized (this) {
                if (null == _init_obj) {
                    _init_obj = ZMoDoc.NEW(init);
                }
            }
        }
        return _init_obj;
    }

    public void setInit(String initObj) {
        this.init = initObj;
    }

    public String getReduce() {
        return reduceFunc;
    }

    public void setReduceFunc(String reduceFunc) {
        this.reduceFunc = reduceFunc;
    }

}
