# nutz-plugins-protobuf 多视图插件

简介(可用性:生产)
==================================

提供protobuf双向通信所需要的适配器和View

### 传统protobuf使用过程 ###

```
	@At
	@Ok("ioc:proto")
	@AdaptBy(type = ProtobufAdaptor.class, args = { "ioc:protobufAdaptor" })
	public Message proto(UserProto.User message) {
		return message;
	}

```

### jrotobuf使用过程 ###

```
	@At
	@Ok("ioc:jproto")
	@AdaptBy(type = JProtobufAdaptor.class, args = { "ioc:jprotobufAdaptor" })
	public UserJProtoBufProtoClass jproto(UserJProtoBufProtoClass message) {
		return message;
	}

```
#####MainModule添加视图引用#######

```
@Views({ ProtoViewMaker.class })

```
#####user.proto####

```
package org.nutz.plugins.protobuf.pojo;

option java_package = "org.nutz.plugins.protobuf.pojo";
option java_outer_classname = "UserProto";

 message User {
   optional int64 id = 1;
   optional string name = 2;
   message PhoneNumber {
       required string number = 1;
     }
     repeated PhoneNumber phone = 4;
 }

```
######添加ioc定义######

```
var ioc = {
	protobufAdaptor : {
		type : "org.nutz.plugins.protobuf.mvc.adaptor.ProtobufAdaptor"
	},
	jprotobufAdaptor : {
		type : "org.nutz.plugins.protobuf.mvc.adaptor.JProtobufAdaptor"
	}
};

```
#####测试方法#######

```
public void testJProtobuf() throws IOException {
		Codec<UserJProtoBufProtoClass> codec = ProtobufProxy.create(UserJProtoBufProtoClass.class);
		UserJProtoBufProtoClass udbp = new UserJProtoBufProtoClass();
		udbp.id = System.currentTimeMillis();
		udbp.name = "lisi";
		byte[] bytes = codec.encode(udbp);
		String url = "http://127.0.0.1:8080/jproto";
		Request req = Request.create(url, METHOD.POST);
		req.getHeader().set("Content-Type", "application/x-protobuf");
		req.setData(bytes);
		Response resp = Sender.create(req).send();
		UserJProtoBufProtoClass resudbp = codec.decode(Streams.readBytes(resp.getStream()));
	}

	public void testProtobuf() throws IOException {
		UserProto.User user = UserProto.User.newBuilder().setId(1).setName("zhangsan").build();
		byte[] bytes = user.toByteArray();
		String url = "http://127.0.0.1:8080/proto";
		Request req = Request.create(url, METHOD.POST);
		req.getHeader().set("Content-Type", "application/x-protobuf");
		req.setData(bytes);
		Response resp = Sender.create(req).send();
		Message.Builder builder = UserProto.User.newBuilder();
		try {
			builder.mergeFrom(resp.getStream(), ExtensionRegistry.newInstance());
		} catch (IOException e) {
			log.error(e);
		}
		Message res = builder.build();
		System.out.println(Json.toJson(res));
	}
```

#####关于jprotobuf的使用方法请关注#####

https://github.com/jhunters/jprotobuf