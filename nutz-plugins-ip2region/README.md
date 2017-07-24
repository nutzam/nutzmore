nutz-plugins-ip2region
==================================

简介(可用性:开发中)
==================================

线程安全,无本地文件的ip2region

改造的内容
==================================

原版地址: https://github.com/lionsoul2014/ip2region

* 原版仅支持File路径,本版本支持byte[]
* 本版本使用byte[]时,DbSearcher是线程安全的,不需要加锁,内部实行也不是加锁

用法
===================================

直接使用内置数据库文件,建议用单例或保存到static属性中

```
DbSearcher searcher = new DbSearcher();
```

指定数据库文件,从classpath加载,或者任意你喜欢的InputStream来源

```java
// classpath
DBReader reader = new ByteArrayDBReader(Streams.readBytes(getClass().getClassLoader().getResourceAsStream("ip2region/ip2region.db")));
DbSearcher searcher = new DbSearcher(null, reader);

// 从文件
DbSearcher searcher = new DbSearcher(null, "/etc/ip2region/ip2region.db");
```

查询

```java
// 直接取
String region = searcher.getRegion("219.136.76.152");
// 绕路取
String region = searcher.binarySearch("219.136.76.152").getRegion();
```