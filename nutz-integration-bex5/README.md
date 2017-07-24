nutz-integration-bex5
==================================

简介(可用性:试用,维护者:ecoolper)
==================================

Bex5与Nutz集成所

## 如何在Bex5中得到nutz-dao实例，在同一个事务中


```java
    Model model =ModelUtils.getModel("/CRM/card/data");
    Dao dao = DaoUtils.getDaoInTransaction(model);
```
通过上面方式得到的dao和bex5在同一个事务中

## 如果在Bex5中得到nuta-dao实例，没有在同一个事务中

```java
    Model model =ModelUtils.getModel("/CRM/card/data");
    Dao dao = DaoUtils.getDao(model);
```

