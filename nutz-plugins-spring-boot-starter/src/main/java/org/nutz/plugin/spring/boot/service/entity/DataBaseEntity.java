package org.nutz.plugin.spring.boot.service.entity;

import org.nutz.castor.Castors;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * 
 * @author guiyuan.wang-N
 *
 */
public class DataBaseEntity {

	public <T extends DataBaseEntity> T exchange(Class<T> clazz) {
		return Castors.me().castTo(this, clazz);
	}

	@Override
	public String toString() {
		return Json.toJson(this, JsonFormat.nice());
	}

}
