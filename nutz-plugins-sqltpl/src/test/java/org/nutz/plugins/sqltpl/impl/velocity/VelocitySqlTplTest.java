package org.nutz.plugins.sqltpl.impl.velocity;

import static org.junit.Assert.*;

import org.apache.velocity.app.Velocity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.sql.Sql;

public class VelocitySqlTplTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        Velocity.init();
    }
    
    @Before
    public void before() throws Exception {
        Sqls.setSqlBorning(VelocitySqlTpl.class);
    }

    @After
    public void after() throws Exception {
        Sqls.setSqlBorning(NutSql.class);
    }

    @Test
    public void test_c() {
        FileSqlManager sqlm = new FileSqlManager("org/nutz/plugins/sqltpl/impl/velocity/sqls");
        assertTrue(sqlm.count() > 0);
        Sql sql = null;
        String dst = null;
        
        // 带name和passwd参数
        sql = sqlm.create("user.fetch");
        sql.params().set("name", "wendal");
        sql.params().set("passwd", "123456");
        
        dst = sql.toPreparedStatement().replaceAll("[ \\t\\n\\r]", "");
        assertEquals("select * from t_user where name = ? and passwd = ?".replaceAll(" ", ""), dst);
        
        // 带token参数
        
        sql = sqlm.create("user.fetch");
        sql.params().set("token", "_123456");
        
        dst = sql.toPreparedStatement().replaceAll("[ \\t\\n\\r]", "");
        assertEquals("select * from t_user where token = ?".replaceAll(" ", ""), dst);
    }

}
