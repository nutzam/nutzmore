package org.nutz.integration.spring;

import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class SimpleDaoInterceptor implements DaoInterceptor {

    private static final Log log = Logs.get();
    
    public void filter(DaoInterceptorChain chain) throws DaoException {
        log.debug("before >>> " + chain.getDaoStatement().toPreparedStatement());
        chain.doChain();
        log.debug("after  <<< " + chain.getDaoStatement().toPreparedStatement());
    }

}
