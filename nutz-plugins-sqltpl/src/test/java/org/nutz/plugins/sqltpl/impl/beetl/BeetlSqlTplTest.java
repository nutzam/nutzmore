package org.nutz.plugins.sqltpl.impl.beetl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;

public class BeetlSqlTplTest {
    
    @Test
    public void testC() {
        String source = "select * from t_user where 1=1 <%if (has('id')) {%>and id > @id<%}%>";
        
        Sql sql = Sqls.create(source);
        sql.params().set("id", 1000); // 带指定参数,那么对应的and子句应该出现
        sql = BeetlSqlTpl.c(sql);
        
        assertEquals("select * from t_user where 1=1 and id > ?", sql.toPreparedStatement());
        
        sql = Sqls.create(source);
        //sql.params().set("id", 1000); // 不带指定参数,所以没有and子句
        sql = BeetlSqlTpl.c(sql);
        assertEquals("select * from t_user where 1=1", sql.toPreparedStatement());
    }

    @Test
    public void testC_2() {
        String source = "select * from t_user <% if(params.~size>0) {%>where 1=1 <%if (has('id')) {%>and id > @id<%}%><%}%>";
        
        Sql sql = Sqls.create(source);
        //sql.params().set("id", 1000); // 不添加任何参数,那么where语句不应该出现
        sql = BeetlSqlTpl.c(sql);
        assertEquals("select * from t_user", sql.toPreparedStatement());
    }
}
