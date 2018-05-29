package org.nutz.plugins.sqltpl.impl.velocity;

import org.apache.velocity.app.Velocity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.sql.Sql;
import org.nutz.plugins.sqltpl.impl.bean.SqlTplUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        
        SimpleDataSource ds = new SimpleDataSource();
        ds.setJdbcUrl("jdbc:h2:~/nutztest");
        NutDao dao = new NutDao(ds);
        
        dao.create(SqlTplUser.class, true);
        
        
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
        
        
        // 走一下dao接口,测试一下
        sql = sqlm.create("user.fetch");
        sql.params().set("name", "wendal");
        sql.params().set("passwd", "123456");
        dao.execute(sql);
        
        
        
        
        
        
        
        
    }
}
