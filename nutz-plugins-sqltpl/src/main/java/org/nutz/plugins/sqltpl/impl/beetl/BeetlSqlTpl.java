package org.nutz.plugins.sqltpl.impl.beetl;

import java.io.IOException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.nutz.dao.sql.Sql;
import org.nutz.plugins.sqltpl.NutSqlTpl;
import org.nutz.plugins.sqltpl.VarSetMap;

/**
 * Beetl版SqlTpl实现,可用变量为 params.XXX vars.XXXX, 另外params的变量可直接访问
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class BeetlSqlTpl extends NutSqlTpl {

    private static final long serialVersionUID = 1L;

    public BeetlSqlTpl(String source) {
        super(source);
    }

    /**
     * 自定义GroupTemplate
     */
    protected static GroupTemplate gt;

    /**
     * 渲染一个Sql对象
     * 
     * @param sql
     *            需要渲染的Sql实例
     * @return 原对象,用于链式调用
     */
    public static Sql c(Sql sql) {
        String source = sql.getSourceSql();
        Template t = gt().getTemplate(source);
        t.binding(VarSetMap.asCtx(sql));
        String n = t.render();
        sql.setSourceSql(n);
        return sql;
    }

    /**
     * 自定义设置GroupTemplate
     * 
     * @param gt
     *            自定义的GroupTemplate
     */
    public static void setGroupTemplate(GroupTemplate gt) {
        BeetlSqlTpl.gt = gt;
    }

    /**
     * 获取GroupTemplate
     * 
     * @return GroupTemplate实例,如果没有自定义,就生成一个默认的
     */
    public static GroupTemplate gt() {
        if (gt == null) {
            ClasspathStringResourceLoader resourceLoader = new ClasspathStringResourceLoader();
            Configuration cfg;
            try {
                cfg = Configuration.defaultConfiguration();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            gt = new GroupTemplate(resourceLoader, cfg);
        }
        return gt;
    }

    protected void render() {
        c(this);
    }
}
