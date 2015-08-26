package org.nutz.plugins.sqltpl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.VarSet;

public class VarSetMap {
    
    protected static Map<String, Object> global;
    
    public static void setGlobal(Map<String, Object> global) {
        VarSetMap.global = global;
    }
    
    public static Map<String, Object> asMap(VarSet vars) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : vars.keys()) {
            map.put(key, vars.get(key));
        }
        return map;
    }
    
    public static Map<String, Object> asCtx(Sql sql) {
        Map<String, Object> params = VarSetMap.asMap(sql.params());
        Map<String, Object> ctx = new HashMap<String, Object>();
        if (global != null) {
            ctx.putAll(global);
        }
        ctx.putAll(params);
        ctx.put("params", params);
        ctx.put("vars", VarSetMap.asMap(sql.vars()));
        return ctx;
    }
}
