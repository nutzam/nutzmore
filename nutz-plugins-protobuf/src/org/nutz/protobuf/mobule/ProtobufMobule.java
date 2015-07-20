package org.nutz.protobuf.mobule;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.protobuf.mvc.adaptor.JProtobufAdaptor;
import org.nutz.protobuf.mvc.adaptor.ProtobufAdaptor;
import org.nutz.protobuf.pojo.UserJProtoBufProtoClass;
import org.nutz.protobuf.pojo.UserProto;

import com.google.protobuf.Message;

@IocBean
@Filters
public class ProtobufMobule {

	@At
	@Ok("ioc:proto")
	@AdaptBy(type = ProtobufAdaptor.class, args = { "ioc:protobufAdaptor" })
	public Message proto(UserProto.User message, @Attr("messageType") short messageType) {
		return message;
	}

	@At
	@Ok("ioc:jproto")
	@AdaptBy(type = JProtobufAdaptor.class, args = { "ioc:jprotobufAdaptor" })
	public Object jproto(UserJProtoBufProtoClass message, @Attr("messageType") short messageType) {
		return message;
	}

}
