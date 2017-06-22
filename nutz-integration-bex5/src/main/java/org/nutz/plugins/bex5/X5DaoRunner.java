package org.nutz.plugins.bex5;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.impl.DaoRunner;
import org.nutz.dao.impl.sql.run.NutDaoRunner;

import java.sql.Connection;
import javax.sql.DataSource;

/**
 * Created by ecoolper on 2017/6/19.
 */
public class X5DaoRunner implements DaoRunner {

    @Override
    public void run(DataSource dataSource, ConnCallback callback) {
        try {
            Connection con = dataSource.getConnection();
            callback.invoke(con);
        } catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new RuntimeException(e);
        }
    }
}
