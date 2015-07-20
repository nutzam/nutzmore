package org.nutz.plugins.tools;

import java.io.IOException;

import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.protobuf.pojo.UserJProtoBufProtoClass;
import org.nutz.plugins.protobuf.pojo.UserProto;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;

public class PostTest {
	private final static Log log = Logs.get();

	public static void main(String[] args) throws IOException {
		jb();
	}

	public static void testJProtobuf() throws IOException {
		UserJProtoBufProtoClass udbp = new UserJProtoBufProtoClass();
		udbp.id = System.currentTimeMillis();
		udbp.name = "lisi";
		Codec<UserJProtoBufProtoClass> codec = ProtobufProxy.create(UserJProtoBufProtoClass.class);
		byte[] bytes = codec.encode(udbp);
		String url = "http://127.0.0.1:8080/nutz_protobuf/jproto";
		Request req = Request.create(url, METHOD.POST);
		req.getHeader().set("Content-Type", "application/x-protobuf");
		req.setData(bytes);
		Response resp = Sender.create(req).send();
		UserJProtoBufProtoClass resudbp = codec.decode(Streams.readBytes(resp.getStream()));
		System.out.println(Json.toJson(resudbp));
	}

	public static void testProtobuf() throws IOException {
		UserProto.User user = UserProto.User.newBuilder().setId(1).setName("zhangsan").build();
		byte[] bytes = user.toByteArray();
		String url = "http://127.0.0.1:8080/nutz_protobuf/proto";
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

	public static void jb() throws IOException {
		Codec<UserJProtoBufProtoClass> codec = ProtobufProxy.create(UserJProtoBufProtoClass.class);
		UserJProtoBufProtoClass udbp = new UserJProtoBufProtoClass();
		udbp.id = System.currentTimeMillis();
		udbp.name = "lisi";
		byte[] bytes = codec.encode(udbp);
		String url = "http://127.0.0.1:8080/nutz_protobuf/jproto";
		Request req = Request.create(url, METHOD.POST);
		req.getHeader().set("Content-Type", "application/x-protobuf");
		// int len = bytes.length;
		// byte[] temp = new byte[len];
		// byte[] sArr = shortToByteArray((short) 1);
		// System.arraycopy(sArr, 0, temp, 0, 2);
		// System.arraycopy(bytes, 0, temp, 0, len);
		System.out.println(bytes.length);
		req.setData(bytes);
		Response resp = Sender.create(req).send();
		UserJProtoBufProtoClass resudbp = codec.decode(Streams.readBytes(resp.getStream()));
		System.out.println(Json.toJson(resudbp));
	}

	private static byte[] shortToByteArray(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}
}
