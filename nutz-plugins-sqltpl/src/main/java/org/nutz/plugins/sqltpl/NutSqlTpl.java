package org.nutz.plugins.sqltpl;

import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.jdbc.ValueAdaptor;

public abstract class NutSqlTpl extends NutSql {

    private static final long serialVersionUID = 1L;
    
    protected boolean renderComplete;

    public NutSqlTpl(String source) {
        super(source);
    }
    
    public ValueAdaptor[] getAdaptors() {
        checkRender();
        return super.getAdaptors();
    }

    public String toPreparedStatement() {
        checkRender();
        return super.toPreparedStatement();
    }
    
    public Object[][] getParamMatrix() {
        checkRender();
        return super.getParamMatrix();
    }
    
    protected void checkRender() {
        if (!renderComplete) {
            render();
            renderComplete = true;
        }
    }
    
    protected abstract void render();
}
