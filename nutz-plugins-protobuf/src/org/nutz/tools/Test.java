package org.nutz.tools;

import java.io.FileInputStream;
import java.util.List;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;

public class Test {

	public static void main(String[] args) throws Exception {

		Runtime run = Runtime.getRuntime();
		// ""
		String dir = "D:/Tools/";// System.getProperty("user.dir");
		String source = dir + "protoc-2.5.0-win32/";
		String cmd = "cmd /c " + source + "protoc.exe -I=" + source + " --descriptor_set_out=" + source + "user.desc " + source + "user.proto";
		System.out.println(cmd);

		Process p = run.exec(cmd);

		// 如果不正常终止, 则生成desc文件失败
		if (p.waitFor() != 0) {
			if (p.exitValue() == 1) {// p.exitValue()==0表示正常结束，1：非正常结束
				System.err.println("命令执行失败!");
				System.exit(1);
			}
		}

		FileInputStream fin = new FileInputStream(source + "user.desc");
		FileDescriptorSet descriptorSet = FileDescriptorSet.parseFrom(fin);

		for (FileDescriptorProto fdp : descriptorSet.getFileList()) {
			FileDescriptor fd = FileDescriptor.buildFrom(fdp, new FileDescriptor[] {});

			for (Descriptor descriptor : fd.getMessageTypes()) {
				String className = fdp.getOptions().getJavaPackage() + "." + fdp.getOptions().getJavaOuterClassname() + "$" + descriptor.getName();
				List<FieldDescriptor> types = descriptor.getFields();
				for (FieldDescriptor type : types) {
					System.out.println(type.getFullName());
				}
				System.out.println(descriptor.getFullName() + " -> " + className);
			}
		}

		// Descriptor md = fd.getDescriptorForType();
		// byte[] data = null ;
		// DynamicMessage m = DynamicMessage.parseFrom(md, data);

	}

}
