package org.nutz.plugins.sqltpl.impl.velocity;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.sql.Sql;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

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
    
    

    public static void main(String[] args) {
        for (File f : new File("E:\\pp").listFiles()) {
            if (f.getName().endsWith(".json")) {
                VelocitySqlTplTest t = new VelocitySqlTplTest();
                t.doit("E:\\pp\\"+f.getName());
                System.out.println("output>>\r\n" + t.sb);
                Files.write(f.getAbsolutePath() + ".txt", t.sb);
            }
        }
    }
    
    private static final Log log = Logs.get();
    protected StringBuilder sb = new StringBuilder();
    private static String NL = "\r\n";
    
    public void doit(String path) {
        NutMap map = Json.fromJsonFile(NutMap.class, new File(path));
        for (String key : map.keySet()) {
            if (key.startsWith("part")) {
                do_part(map.getAs(key, NutMap.class));
            } else if (key.startsWith("testinfo")) {
                do_testinfo(map.getAs(key, NutMap.class));
            }
        }
    }
    
    public void do_part(NutMap part) {
        NutMap partinfo = part.getAs("partinfo", NutMap.class);
        log.debug(">> " + partinfo.getString("part_title"));
        sb.append(NL+NL).append(partinfo.getString("part_title")).append(NL+NL);
        List<NutMap> quest = part.getList("quest", NutMap.class);
        int index = 1;
        for (NutMap q : quest) {
            sb.append("第" + index + "题").append(NL);
            if (!Strings.isBlank(q.getString("question_text"))) {
                sb.append("背景: " + c(q.getString("question_text"))).append(NL);
            }
            for (NutMap sq : q.getList("subquestion", NutMap.class)) {
                sb.append("问: " + c(sq.getString("question_text"))).append(NL);
                for(NutMap option : sq.getList("options", NutMap.class)) {
                    sb.append(option.get("key")+": " + c(option.get("answer_text")) + NL);
                }
                sb.append("答案: " + sq.getString("answer") + NL);
                sb.append("解析: " + c(sq.getString("demo")) + NL);
            }
            sb.append(NL);
            index ++;
        }
    }
    
    public void do_testinfo(NutMap map) {
        
    }
    
    public static String c(Object obj) {
        String str = String.valueOf(obj);
        return str.replace("<p>", "").replace("</p>", "").replace("<span style=\"line-height:1.5;\">", "").replace("&nbsp;", " ").trim().trim();
    }
}
