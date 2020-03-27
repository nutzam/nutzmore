package org.nutz.postgis.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 */
public class JtsGeometryValueAdapter implements ValueAdaptor {

    /**
     * @param rs
     * @param colName
     * @return
     * @throws SQLException
     * @see org.nutz.dao.jdbc.ValueAdaptor#get(java.sql.ResultSet,
     *      java.lang.String)
     */
    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        return rs.getObject(colName);
    }

    /**
     * @param stat
     * @param obj
     * @param index
     * @throws SQLException
     * @see org.nutz.dao.jdbc.ValueAdaptor#set(java.sql.PreparedStatement,
     *      java.lang.Object, int)
     */
    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        stat.setObject(index, obj);
    }

}
