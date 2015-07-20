
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
	public Object jproto(UserJProtoBufProtoClass message) {
		return message;
	}

```

#####关于jprotobuf的使用方法请关注#####

https://github.com/jhunters/jprotobuf

