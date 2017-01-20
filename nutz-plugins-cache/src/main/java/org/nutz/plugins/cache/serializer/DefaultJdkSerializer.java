package org.nutz.plugins.cache.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.CacheResult;

public class DefaultJdkSerializer extends AbstractCacheSerializer {
    
    private static final Log log = Logs.get();

    public Object fromObject(Object obj) {
        if (obj == null)
            return NULL_OBJ;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeUnshared(obj);
            oos.close();
            return bao.toByteArray();
        } catch (Exception e) {
            log.info("Object to bytes fail", e);
            return null;
        }
    }

    public Object toObject(Object obj) {
        if (obj == null)
            return null;
        if (isNULL_OBJ(obj))
            return CacheResult.NULL;
        try {
            ObjectInputStream ins = new ObjectInputStream(new ByteArrayInputStream((byte[])obj));
            Object tmp = ins.readUnshared();
            ins.close();
            return tmp;
        } catch (Exception e) {
            log.info("bytes to Object fail", e);
            return null;
        }
    }
}
