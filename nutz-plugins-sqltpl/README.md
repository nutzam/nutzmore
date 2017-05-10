nutz-plugins-sqltpl SQL模板实现
==================================

简介(可用性:试用)
==================================

支持多种模板引擎

## 用法(以BeetlSqlTpl为例)

注意,这不是与BeetlSql的集成.

在MainSetup.init内加入下面的语句, 即可全局启用

```java
Sqls.setSqlBorning(BeetlSqlTpl.class);
```