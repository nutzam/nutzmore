package org.nutz.tools;

import java.io.IOException;

import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.protobuf.pojo.UserJProtoBufProtoClass;
import org.nutz.protobuf.pojo.UserJProtoBufProtoClass.PhoneNumberJProtoBufProtoClass;
import org.nutz.protobuf.pojo.UserProto;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;

public class PostTest {

	private final static Log log = Logs.get();

	public void testProto() {
		UserProto.User user = UserProto.User.newBuilder().setId(1).setName("zhangsan").build();
		byte[] bytes = user.toByteArray().clone();
		String url = "http://127.0.0.1:8080/proto";
		Request req = Request.create(url, METHOD.POST);
		req.getHeader().set("Content-Type", "application/x-protobuf");
		int len = bytes.length;
		byte[] temp = new byte[len];
		System.arraycopy(bytes, 0, temp, 0, len);
		req.setData(temp);
		Response resp = Sender.create(req).send();

		Message.Builder builder = UserProto.User.newBuilder();
		try {
			builder.mergeFrom(resp.getStream(), ExtensionRegistry.newInstance());
		} catch (IOException e) {
			log.error(e);
		}
		Message res = builder.build();
		System.out.println(res);
	}

	public void testJProto() throws IOException {
		UserJProtoBufProtoClass user = new UserJProtoBufProtoClass();// .setId(1).setName("zhangsan").build();
		user.id = System.currentTimeMillis();
		user.name = "zhangsan";
		PhoneNumberJProtoBufProtoClass phone = new UserJProtoBufProtoClass.PhoneNumberJProtoBufProtoClass();
		phone.number = "1234567890";
		user.phone = phone;
		Codec<UserJProtoBufProtoClass> codec = ProtobufProxy.create(UserJProtoBufProtoClass.class);
		byte[] bytes = codec.encode(user);
		String url = "http://127.0.0.1:8080/proto";
		Request req = Request.create(url, METHOD.POST);
		req.getHeader().set("Content-Type", "application/x-protobuf");
		int len = bytes.length;
		byte[] temp = new byte[len];
		System.arraycopy(bytes, 0, temp, 0, len);
		req.setData(temp);
		Response resp = Sender.create(req).send();

		UserJProtoBufProtoClass builder = codec.decode(Streams.readBytes(resp.getStream()));
		System.out.println(builder);
	}

	public static void main(String[] args) {

	}
}
