package org.nutz.plugins.sqltpl.impl.freemarker;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.nutz.dao.sql.Sql;
import org.nutz.plugins.sqltpl.NutSqlTpl;
import org.nutz.plugins.sqltpl.VarSetMap;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * FreeMarker版SqlTpl实现
 *
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class FreeMarkerSqlTpl extends NutSqlTpl {

    private static final long serialVersionUID = 1L;

    public FreeMarkerSqlTpl(String source) {
        super(source);
    }

    /**
     * 自定义Configuration
     */
    protected static Configuration cfg;

    /**
     * 渲染一个Sql对象
     *
     * @param sql
     *            需要渲染的Sql实例
     * @return 原对象,用于链式调用
     */
    public static Sql c(Sql sql) {
        try {
            Template t = new Template("sqltpl", new StringReader(sql.getSourceSql()), cfg);
            Map<String, Object> ctx = VarSetMap.asCtx(sql);
            StringWriter sw = new StringWriter();
            t.process(ctx, sw);
            sql.setSourceSql(sw.toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (TemplateException e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    /**
     * 获取Configuration
     *
     * @return Configuration实例,如果没有自定义的,就生成一个默认的
     */
    public static Configuration cfg() {
        if (cfg == null) {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            FreeMarkerSqlTpl.cfg = cfg;
        }
        return cfg;
    }

    /**
     * 设置Configuration
     *
     * @param cfg
     *            自定义Configuration
     */
    public static void setConfiguration(Configuration cfg) {
        FreeMarkerSqlTpl.cfg = cfg;
    }

    @Override
    protected void render() {
        c(this);
    }
}
