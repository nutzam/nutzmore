nutz-integration-spring
==================================

简介(可用性:生产)
==================================

Spring与Nutz集成所需要的一切

### How to ues

在 xml 中加入如下代码

```xml
<bean id="nutDao" class="org.nutz.dao.impl.NutDao">
    <property name="dataSource" ref="dataSource"/>
    <!-- 如果要使用Trans,移除springDaoRunner -->
    <property name="runner" ref="springDaoRunner"/>
</bean>
<bean id="springDaoRunner" class="org.nutz.integration.spring.SpringDaoRunner">
</bean>
```

之后，在被 Spring 管理的类中写


```java
@Autowired
private Dao nutDao;
```

这样，nutz 的 dao 就被注入进来了，随便用。

## 注意事项

使用SpringDaoRunner的话, Trans不会生效, 会依赖Spring的TxManager.