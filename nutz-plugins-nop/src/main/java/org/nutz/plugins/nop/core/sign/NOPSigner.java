package org.nutz.plugins.nop.core.sign;

import java.util.Arrays;
import java.util.Collection;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.serialize.NOPBaseSerializer;
import org.nutz.plugins.nop.core.serialize.Serializer;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOPSigner.java
 *
 * @description 默认的签名实现
 *
 * @time 2016年8月31日 下午2:36:05
 *
 */
public class NOPSigner implements Signer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.plugins.nop.core.sign.Signer#sign(java.lang.String,
	 * java.lang.String, org.nutz.plugins.nop.core.NOPRequest)
	 */
	@Override
	public String sign(String appKey, String appSecret, NOPRequest request) {
		String target = appKey;
		for (String key : sortKeys(request.getParams().keySet())) {// 参数
			target += key + serialize(request.getParams().get(key));
		}
		String info = (target + appSecret).replace("\\s", "");
		System.err.println(info);
		return Lang.sha1(info);
	}

	public String serialize(Object object) {
		for (Serializer serializer : NOPConfig.getSerializers()) {
			if (Mirror.me(object).isOf(serializer.getObjClazz())) {
				return serializer.serialize(object);
			}
		}
		return new NOPBaseSerializer().serialize(object);
	}

	public String[] sortKeys(Collection<String> keys) {// 字典序
		if (keys != null && keys.size() > 0) {
			String[] target = Lang.collection2array(keys);
			Arrays.sort(target);
			return target;
		}
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.plugins.nop.core.sign.Signer#name()
	 */
	@Override
	public String name() {
		return "NOPSigner";
	}

}
