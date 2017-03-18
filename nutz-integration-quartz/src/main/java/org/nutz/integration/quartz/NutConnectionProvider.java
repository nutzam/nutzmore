package org.nutz.integration.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;
import org.quartz.utils.ConnectionProvider;

public class NutConnectionProvider implements ConnectionProvider {
    
    protected DataSource dataSource;
    protected String iocname = "dataSource";

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() throws SQLException {}

    @SuppressWarnings("deprecation")
    public void initialize() throws SQLException {
        if (dataSource != null)
            return;
        Ioc ioc = Mvcs.getIoc();
        if (ioc == null)
            ioc = Mvcs.ctx.getDefaultIoc();
        dataSource = ioc.get(DataSource.class, iocname);
    }

}
