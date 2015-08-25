package org.nutz.plugins.sqltpl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.sql.VarSet;

public class VarSetMap {
    
    public static Map<String, Object> asMap(VarSet vars) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : vars.keys()) {
            map.put(key, vars.get(key));
        }
        return map;
    }
}
