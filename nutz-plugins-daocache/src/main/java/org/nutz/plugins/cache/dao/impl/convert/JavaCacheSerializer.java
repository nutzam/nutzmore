package org.nutz.plugins.cache.dao.impl.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 使用java序列化
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class JavaCacheSerializer extends AbstractCacheSerializer {
    
    private static final Log log = Logs.get();

    public Object from(Object obj) {
        if (obj == null)
            return NULL_OBJ;
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(obj);
            return bao.toByteArray();
        } catch (Exception e) {
            log.info("Object to bytes fail", e);
            return null;
        }
    }

    public Object back(Object obj) {
        if (isNULL_OBJ(obj))
            return null;
        try {
            return new ObjectInputStream(new ByteArrayInputStream((byte[])obj)).readObject();
        } catch (Exception e) {
            log.info("bytes to Object fail", e);
            return null;
        }
    }


}
