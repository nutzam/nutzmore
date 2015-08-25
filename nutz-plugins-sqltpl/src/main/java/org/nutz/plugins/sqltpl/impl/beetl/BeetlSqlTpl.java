package org.nutz.plugins.sqltpl.impl.beetl;


import java.io.IOException;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.nutz.dao.sql.Sql;
import org.nutz.plugins.sqltpl.VarSetMap;

public class BeetlSqlTpl {
    
    static GroupTemplate gt;

    static {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg;
        try {
            cfg = Configuration.defaultConfiguration();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        gt = new GroupTemplate(resourceLoader, cfg);
    }
    
    public static Sql c(Sql sql) {
        String source = sql.getSourceSql();
        Template t = gt.getTemplate(source);
        t.binding("vars", VarSetMap.asMap(sql.vars()));
        Map<String, Object> params = VarSetMap.asMap(sql.params());
        t.binding(params);
        t.binding("params", params);
        System.out.println(t.getCtx().globalVar);
        String n = t.render();
        sql.setSourceSql(n);
        return sql;
    }
}
