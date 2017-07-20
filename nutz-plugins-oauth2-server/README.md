# Apache Oltu OAuth2 Server + Client


简介(可用性:生产,维护者:Rekoe)
==================================

使用Apache Oltu 搭建Oauth2 Server及Client开放授权

# Oauth2 Server端执行流程

### 1. 先注册应用

http://localhost:8080/client

会生成client_id和client_secret 这两个以后会用到
```
client_id               c1ebe466-1cdc-4bd3-ab69-77c3561b9dee    应用id
client_secret           d8346ea2-6017-43ed-ad68-19c0f971738b    应用secret
```
### 2. 请求授权码

http://localhost:8080/authorize?client_id=c1ebe466-1cdc-4bd3-ab69-77c3561b9dee&response_type=code&redirect_uri=http://nutz.cn

参数说明
```
client_id               应用id
response_type           返回授权码的标识
redirect_uri            回调地址
```
上面的网站会打开oauth server的用户登录页面。用户输入正确的用户名和密码以POST方式提交后会重定向到用户所填的回调地址并在地址后携带授权码.

请求成功后会返回如下的页面:

http://nutz.cn/?code=63910432da9186b22b1ad888d55ae8ae

这里code=63910432da9186b22b1ad888d55ae8ae 即授权码

### 3. 换取accessToken (POST操作)

首先GET方式请求http://localhost:8080/access会打开一个表单在该表单中填入必填项，具体表单参数详见说明部分

表单将会以POST方式提交到http://localhost:8080/accessToken,最终返回accessToken

需要以POST方式提交以下参数换取accessToken
```
client_id       c1ebe466-1cdc-4bd3-ab69-77c3561b9dee            应用id
client_secret   d8346ea2-6017-43ed-ad68-19c0f971738b            应用secret
grant_type      authorization_code                              用于传递授权码的参数名authorization_code
code            63910432da9186b22b1ad888d55ae8ae                用户登录授权后的授权码
redirect_uri    http://aimeizi.net                              回调地址
```
最终返回如下数据

{"expires_in":3600,"access_token":"223ae05dfbb0794396fb60a0960c197e"}

### 4. 使用accessToken测试开放数据服务

http://localhost:8080/v1/openapi/userInfo?access_token=223ae05dfbb0794396fb60a0960c197e

测试ok的话返回用户名信息,access_token=223ae05dfbb0794396fb60a0960c197e为上一步获取的access_token

注：其中的参数名不要随意更改，固定写法。