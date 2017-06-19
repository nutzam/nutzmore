package org.nutz.plugins.sqltpl.impl.velocity;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.nutz.dao.DaoException;
import org.nutz.dao.sql.Sql;
import org.nutz.plugins.sqltpl.NutSqlTpl;
import org.nutz.plugins.sqltpl.VarSetMap;

/**
 * Velocity版SqlTpl实现,可用变量为 params.XXX vars.XXXX, 另外params的变量可直接访问 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class VelocitySqlTpl extends NutSqlTpl {

    private static final long serialVersionUID = 1L;
    
    public VelocitySqlTpl(String source) {
        super(source);
    }

    /**
     * 自定义VelocityEngine
     */
    protected static VelocityEngine engine;

    /**
     * 渲染一个Sql对象
     * @param sql 需要渲染的Sql实例
     * @return 原对象,用于链式调用
     */
    public static Sql c(Sql sql) {
        VelocityContext context = new VelocityContext(VarSetMap.asCtx(sql));
        StringWriter sw = new StringWriter();
        try {
            if (engine == null) {
                if (!RuntimeSingleton.isInitialized())
                    Velocity.init();
                Velocity.evaluate(context, sw, "sqltpl", sql.getSourceSql());
            }
            else
                engine.evaluate(context, sw, "sqltpl", sql.getSourceSql());
        }
        catch (Exception e) {
            throw new DaoException(e);
        }
        sql.setSourceSql(sw.toString());
        return sql;
    }
    
    /**
     * 可自定义VelocityEngine,默认使用Velocity.evaluate进行渲染
     * @param engine 自定义的VelocityEngine
     */
    public static void setEngine(VelocityEngine engine) {
        VelocitySqlTpl.engine = engine;
    }

    @Override
    protected void render() {
        c(this);
    }
}
