package org.nutz.plugins.nop;

import java.util.ArrayList;
import java.util.List;

import org.nutz.plugins.nop.core.serialize.NOPSerializer;
import org.nutz.plugins.nop.core.serialize.Serializer;
import org.nutz.plugins.nop.core.serialize.SerizlizeObject;
import org.nutz.plugins.nop.core.sign.NOPSigner;
import org.nutz.plugins.nop.core.sign.Signer;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOP.java
 *
 * @description
 *
 * @time 2016年8月31日 下午3:33:15
 *
 */
public class NOPConfig {

	public static final String tsKey = "nop-ts";
	public static final String methodKey = "nop-service-method";
	public static final String signKey = "nop-sign";
	public static final String signerKey = "nop-signer-name";
	public static final String appkeyKey = "nop-sign-appkey";
	public static final String appSecretKey = "nop-sign-appsecret";
	public static final String parasKey = "nop-paras";

	static Signer signer = new NOPSigner();

	static List<Serializer> serializers;

	static {
		serializers = new ArrayList<Serializer>();
		serializers.add(new NOPSerializer<SerizlizeObject>());
	}

	public static Signer getSigner() {
		return signer;
	}

	public static List<Serializer> getSerializers() {
		return serializers;
	}

	public static void addSerializer(Serializer serializer) {
		NOPConfig.serializers.add(serializer);
	}

	public static void addSerializer(Class clazz) {
		List<Serializer> temp = new ArrayList<Serializer>();
		for (Serializer serializer : NOPConfig.serializers) {
			if (serializer.getObjClazz() != clazz)
				temp.add(serializer);
		}
		NOPConfig.serializers = temp;
	}

	public static String appkeyKey() {
		return appkeyKey;
	}

	public static String parasKey() {
		return parasKey;
	}

	public static String appSecretKey() {
		return appSecretKey;
	}

	public static String signerKey() {
		return signerKey;
	}

	public static String methodKey() {
		return methodKey;
	}

	public static String tskey() {
		return tsKey;
	}

	public static String signkey() {
		return signKey;
	}

}
