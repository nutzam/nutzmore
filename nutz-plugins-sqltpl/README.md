nutz-plugins-sqltpl SQL模板实现
==================================

简介(可用性:试用,维护者:wendal)
==================================

支持多种模板引擎

## 基本思路

把SQL当成模板文本, 用var和param作为上下文参数, 渲染出真正要执行的SQL语句.

```
SQL(模板文本) |
              | ---> 需要执行的SQL
var/param     |
```

## 用法(以BeetlSqlTpl为例)

注意,这不是与BeetlSql的集成.

首先, 在MainSetup.init内加入下面的语句, 全局启用

```java
Sqls.setSqlBorning(BeetlSqlTpl.class);
```

然后,你需要一个SQL文件(或者你直接写在代码里面),注意模板引擎的语法

```sql
/* user.fetch */
select * from t_user 
<% if (params.~size > 0) {%>
where
	<% if (has("name") && has("passwd")) {%>
		name = @name
		and passwd = @passwd
	<% } else { %>
		token = @token
	<% } %>

<% } %>
/* ... 其他SQL语句 */
...
```

最后呢,执行SQL的代码,没有任何痕迹, 正常使用即可.

```java
        // 带name和passwd参数
        Sql sql = dao.sqls().create("user.fetch");
        //--------------------------------------------------------
        // 如果name和passwd都赋值了,那么输出下面的SQL.如果没有name但是有token,输出的SQL就应该是 select * from t_user where token = @token
        sql.params().set("name", "wendal");
        sql.params().set("passwd", "123456");
        //String dst = sql.toPreparedStatement().replaceAll("[ \\t\\n\\r]", "");
        //assertEquals("select * from t_user where name = ? and passwd = ?".replaceAll(" ", ""), dst);
        // --------------------------------------------------------
        sql.setEntity(dao.getEntity(User.class));
        sql.setCallback(Sqls.callback.queryEntites());
        List<User> list = dao.execute(sql).getList(User.class);
```