package org.nutz.plugins.sqltpl.impl.jetbrick;

import java.io.StringWriter;

import org.nutz.dao.sql.Sql;
import org.nutz.plugins.sqltpl.NutSqlTpl;
import org.nutz.plugins.sqltpl.VarSetMap;

import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;

/**
 * jetbrick-template实现的SqlTpl
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class JetbrickSqlTpl extends NutSqlTpl {

    public JetbrickSqlTpl(String source) {
        super(source);
    }

    private static final long serialVersionUID = 1L;

    protected static JetEngine engine;
    
    public static Sql c(Sql sql) {
        JetTemplate template = engine().createTemplate(sql.getSourceSql());
        StringWriter sw = new StringWriter();
        template.render(VarSetMap.asCtx(sql), sw);
        sql.setSourceSql(sw.toString());
        return sql;
    }
    
    public static JetEngine engine() {
        if (engine == null)
            engine = JetEngine.create();
        return engine;
    }
    
    public static void setEngine(JetEngine engine) {
        JetbrickSqlTpl.engine = engine;
    }

    @Override
    protected void render() {
        c(this);
    }
}
