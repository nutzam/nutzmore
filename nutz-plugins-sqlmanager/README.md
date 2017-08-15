nutz-plugins-sqlmanager
==================================

简介(可用性:试用,维护者:wendal等)
==================================

各式各样的SqlManager实现

## 使用XML管理SQL

总有一小撮人喜欢用XML...

### xml文件示例

CDATA不是必须的,但推荐使用,不然写大于小于的时候就蛋疼了

```xml
<?xml version="1.0" encoding="UTF-8"?>
<sqls>
	<sql key="user.fetch.by_name"><![CDATA[
	select * from t_user where name = @name
	]]></sql>
	<sql key="user.count"><![CDATA[
	select count(*) from t_user $cnd
	]]></sql>
</sqls>
```

规则很简单, 一眼就看懂了吧

### dao.js示例

```js
var ioc = {
    // 这里省略了conf和dataSource的配置
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [{refer:"dataSource"}]
		fields : {
			sqlManager : {refer:"sqlManager"}
		}
	},
	sqlManager : {
		type : "org.nutz.plugins.sqlmanager.xml.XmlSqlManager",
		fields : {
			paths : ["sqls/"]
	    }
	}
}
```

### @IocBean工厂方法

1.r.62及以上的版本支持的写法:

```java
@IocBean
public class MyIocBeans {
	@IocBean
	public Dao getDao(DataSource dataSource, SqlManager sqlManager) {
	    NutDao dao = new NutDao(dataSource);
	    dao.setSqlManager(sqlManager);
		return dao;
	}
	@IocBean
	public SqlManager getSqlManager() {
	    XmlSqlManager sqlManager = new XmlSqlManager();
	    sqlManager.setPaths("sqls/")
		return sqlManager;
	}
}
```

## 使用Dao管理SQL

TODO

## 代理SqlManager,监视文件变化

TODO