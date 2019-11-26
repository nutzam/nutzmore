package org.nutz.plugin.spring.boot.service.entity;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * 
 * @author kerbores(kerbores@gmail.com)
 *
 */
public class DataBaseEntity {

    public <T extends DataBaseEntity> T exchange(Class<T> clazz) {
        return Json.fromJson(clazz, Json.toJson(this, JsonFormat.compact().ignoreJsonShape()));
    }

    @Override
    public String toString() {
        return Json.toJson(this, JsonFormat.nice());
    }

}
