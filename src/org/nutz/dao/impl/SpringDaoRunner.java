package org.nutz.dao.impl;
import java.sql.Connection;

import javax.sql.DataSource;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.DaoRunner;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class SpringDaoRunner implements DaoRunner {

        public void run(DataSource dataSource, ConnCallback callback) {
                Connection con = DataSourceUtils.getConnection(dataSource);  
                try {  
                        callback.invoke(con);  
                }
                catch (Exception e) {  
                        if (e instanceof RuntimeException)  
                                throw (RuntimeException) e;  
                        else  
                                throw new RuntimeException(e);  
                } finally {  
                        DataSourceUtils.releaseConnection(con, dataSource);  
                }  
        }
}