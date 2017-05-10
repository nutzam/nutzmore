package org.nutz.plugins.sqltpl;

import org.nutz.dao.impl.sql.NutSql;

public abstract class NutSqlTpl extends NutSql {

    private static final long serialVersionUID = 1L;

    public NutSqlTpl(String source) {
        super(source);
    }

    public String toPreparedStatement() {
        render();
        return super.toPreparedStatement();
    }
    
    protected abstract void render();
}
