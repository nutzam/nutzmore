package org.nutz.plugins.sqltpl.impl.jetbrick;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.sql.Sql;

import static org.junit.Assert.assertTrue;

public class JetbrickSqlTplTest {
    @Before
    public void before(){
        Sqls.setSqlBorning(JetbrickSqlTpl.class);
    }

    @After
    public void after(){
        Sqls.setSqlBorning(NutSql.class);
    }

    @Test
    public void testC() {
        FileSqlManager sqlm = new FileSqlManager("org/nutz/plugins/sqltpl/impl/jetbrick/sqls");
        assertTrue(sqlm.count() > 0);
        Sql sql = null;
        // 带name和passwd参数
        sql = sqlm.create("user.fetch");
        sql.params().set("name", "wendal");
        sql.params().set("passwd", "123456");
    }
}
