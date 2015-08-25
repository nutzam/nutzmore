package org.nutz.plugins.sqltpl.impl.beetl;


import java.io.IOException;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.nutz.dao.sql.Sql;
import org.nutz.plugins.sqltpl.VarSetMap;

/**
 * Beetl版SqlTpl实现,可用变量为 params.XXX vars.XXXX, 另外params的变量可直接访问 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class BeetlSqlTpl {
    
    protected static GroupTemplate gt;
    
    protected static Object lock = new Object();
    
    /**
     * 渲染一个Sql对象
     * @param sql 需要渲染的Sql实例
     * @return 原对象,用于链式调用
     */
    public static Sql c(Sql sql) {
        String source = sql.getSourceSql();
        Template t = gt().getTemplate(source);
        Map<String, Object> params = VarSetMap.asMap(sql.params());
        t.binding(params);
        t.binding("params", params);
        t.binding("vars", VarSetMap.asMap(sql.vars()));
        String n = t.render();
        sql.setSourceSql(n);
        return sql;
    }
    
    /**
     * 自定义设置GroupTemplate
     * @param gt 自定义的GroupTemplate
     */
    public static void setGroupTemplate(GroupTemplate gt) {
        BeetlSqlTpl.gt = gt;
    }
    
    public static GroupTemplate gt() {
        if (gt == null) {
            synchronized (lock) {
                if (gt == null) {
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
            }
        }
        return gt;
    }
}
