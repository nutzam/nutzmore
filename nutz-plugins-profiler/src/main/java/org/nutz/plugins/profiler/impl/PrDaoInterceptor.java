package org.nutz.plugins.profiler.impl;

import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.NutStatement;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.plugins.profiler.Pr;
import org.nutz.plugins.profiler.PrSpan;

public class PrDaoInterceptor implements DaoInterceptor {

    public void filter(DaoInterceptorChain chain) throws DaoException {
        DaoStatement ds = chain.getDaoStatement();
        // 仅记录SQL操作
        if (ds instanceof NutStatement) {
            Entity<?> en = ds.getEntity();
            // 需要检测一下,看看是不是插入PrSpan对象,如果是的话就跳过
            if (en == null ||  ! PrSpan.class.isAssignableFrom(en.getType())) {
                PrSpan span = Pr.begin("jdbc." + ds.getSqlType().name());
                try {
                    chain.doChain();
                }
                finally {
                    span.end();
                }
                return;
            }
        }
        chain.doChain();
    }

}
