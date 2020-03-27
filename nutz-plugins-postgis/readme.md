# JDBC
1. 使用 org.postgis.jts.JtsWrapper 作为jdbc驱动包装,将自动注册JtsGeometry数据的支持
2. jdbc协议使用jdbc:postgres_jts:// 例如:jdbc:postgres_jts://127.0.0.1:5432/crawler
3. GIS相关的字段Java类型设置为org.postgis.jts.JtsGeometry
4. GIS相关的字段加上注解@ColDefine(type = ColType.BINARY, customType = "geometry", adaptor = JtsGeometryValueAdapter.class)

# JSON

## 序列化

```java?linenums
Json.addTypeHandler(new JtsGeometryJsonTypeHandler());
```

## 反序列化

```java?linenums
Castors.me().addCastor(Map2JtsGeometryCastor.class);
```