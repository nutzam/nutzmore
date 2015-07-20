package org.nutz.plugins.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.lang.Streams;
import org.nutz.plugins.protobuf.pojo.UserJProtoBufProtoClass;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLProxy;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

public class JavaTest {

	public static void main1(String[] args) throws Exception {
		String dir = "D:/Tools/";// System.getProperty("user.dir");
		String source = dir + "protoc-2.5.0-win32/";
		String protoFile = "user.proto";// 如果要更换生成的数据访问类，只需在此进行更改
		// String protoFile = "person.proto";
		String cmd = source + "protoc.exe --java_out=./src ./proto/" + protoFile;
		System.out.println(cmd);
		Runtime run = Runtime.getRuntime();
		Process p = run.exec(cmd);
		if (p.waitFor() != 0) {
			if (p.exitValue() == 1) {// p.exitValue()==0表示正常结束，1：非正常结束
				System.err.println("命令执行失败!");
				System.exit(1);
			}
		}
	}

	public static void main2(String[] args) throws IOException {
		String dir = "D:/Tools/";// System.getProperty("user.dir");
		String source = dir + "protoc-2.5.0-win32/";
		String protoFile = "user.proto";// 如果要更换生成的数据访问类，只需在此进行更改
		InputStream fis = Streams.fileIn(source + protoFile);
		ProtobufIDLProxy.generateSource(fis, new File(source));
	}
	private static ConcurrentHashMap<Short, Class<?>> classCache = new ConcurrentHashMap<Short, Class<?>>();
	public static void main(String[] args) {
		classCache.put((short)1, UserJProtoBufProtoClass.class);
		Class<?> cls = classCache.get((short)1);
		System.out.println(cls);
		Codec<?> codec = ProtobufProxy.create(cls);
		System.out.println(codec);
	}
}
