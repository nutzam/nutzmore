package org.nutz.plugins.cache.dao;

import java.sql.Connection;

import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.dao.sql.DaoStatement;

public class DaoCacheInterceptor extends CachedNutDaoExecutor implements DaoInterceptor {
    
    protected ThreadLocal<DaoInterceptorChain> t = new ThreadLocal<DaoInterceptorChain>();

    public void filter(DaoInterceptorChain chain) throws DaoException {
        DaoInterceptorChain prev = t.get();
        try {
            t.set(chain);
            super.exec(chain.getConnection(), chain.getDaoStatement());
        } finally {
            if (prev != null)
                t.set(prev);
            else
                t.remove();
        }
    }
    
    protected void _exec(Connection conn, DaoStatement st) {
        t.get().doChain();
    }
}
