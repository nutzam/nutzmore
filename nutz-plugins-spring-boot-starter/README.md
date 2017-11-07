# nutz-plugins-spring-boot-starter
## maven

简介(可用性:生产,维护者:王贵源)
==================================

待编写
``` xml
<dependency>
	<groupId>org.nutz</groupId>
	<artifactId>nutz-plugins-spring-boot-starter</artifactId>
	<version>${version}</version>
</dependency>
```

## 零配置自动注入NutDao
## 自定义sql扫描
+ 默认扫描 sqls目录下的全部.sql/.sqls/.sqlx文件
+ 自定义扫描路径 在application.yml中配置
``` yml
    nutz: 
      dao: 
        sqlmanager:
          paths:
            - a
            - b
```
其中 a,b为sql文件保存目录路径

## 自动创建表,自动变更表
    配置
``` yml
    nutz: 
      dao: 
        runtime:
          create: true
          migration:true
          basepackage: 
            - com.domain
            - org.domain
```
create 自动建表
migration 自动变更
basepackage 实体类所在包名

## 使用nutz-dao
    dao操作及自定义sql操作请移步 https://www.nutzam.com
    
## properties配置方式
    数组声明直接用','分割即可,如:
``` java
    nutz.dao.sqlmanager.paths=demo,sqls
```
## 使用nutzJson

### 移除jackson和其他的json包即可,然后配置
```yml
nutz:
  json:
    enable: true
```
### 定制json
```yml
nutz: 
    json:
            auto-unicode: true
            mode: compact  #json模式,支持的模式有COMPACT, FULL, NICE, FORLOOK, TIDY
            quote-name: false #是否对json key加引号
            date-format: yyyy-MM-dd HH:mm:ss Z #时间格式
            actived:  #激活的key的正则匹配
            ignore-null: #是否忽略空值
            indent:  # 缩进
            indent-by:  #缩进
            locked: #不显示的key的正则匹配
            null-as-emtry: #空值转换为空
            unicode-lower: #是否小写Unicode
```
然后就没有然后了



