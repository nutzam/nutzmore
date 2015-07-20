package org.nutz.plugins.protobuf.pojo;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class UserJProtoBufProtoClass {

	@Protobuf(fieldType = FieldType.INT64, order = 1, required = false)
	public Long id;
	@Protobuf(fieldType = FieldType.STRING, order = 2, required = false)
	public String name;
	@Protobuf(fieldType = FieldType.OBJECT, order = 4)
	public PhoneNumberJProtoBufProtoClass phone;

	public static class PhoneNumberJProtoBufProtoClass {
		@Protobuf(fieldType = FieldType.STRING, order = 1, required = true)
		public String number;
	}
}
